package io.vertx.it.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.exec.OS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Mojo(name = "run-it", threadSafe = false,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresProject = true,
    defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class VertxITMojo extends AbstractMojo {

  /**
   * The maven project.
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  public MavenProject project;

  /**
   * The project base directory.
   */
  @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
  public File basedir;
  /**
   * The target directory of the project.
   */
  @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
  public File buildDirectory;

  @Parameter(defaultValue = "${vertx.home}", required = true)
  public File vertxHome;

  @Parameter(required = true)
  public File outputDirectory;

  @Parameter(required = true)
  public File inputDirectory;

  @Parameter(defaultValue = "${tag}")
  public String tag;

  @Parameter(defaultValue = "${exec}")
  public String exec;

  @Parameter(defaultValue = "${exec.includes}")
  public String includes;

  @Parameter(defaultValue = "${exec.excludes}")
  public String excludes;

  @Parameter(defaultValue = "${interface}")
  public String itf;

  @Component
  public MavenSession session;

  @Component
  public MavenResourcesFiltering filtering;

  @Parameter
  public Properties additionalProperties;

  @Parameter(defaultValue = "${project.compileClasspathElements}", required = false)
  private List<String> classpathElements;

  /**
   * The JAVA_HOME value.
   */
  @Parameter(defaultValue = "${java.home}", required = true, readonly = true)
  public File javaHome;

  private Set<Run> runs = new LinkedHashSet<>();
  private ExecutionSelector selector;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (exec != null && tag != null) {
      throw new MojoExecutionException("Cannot use the `tag` and `exec` parameters together");
    }

    if (exec != null  && (includes != null  || excludes != null)) {
      throw new MojoExecutionException("Cannot use the `exec` and `includes/excludes` parameters together");
    }

    // Manage selector
    if (exec != null) {
      if (exec.contains("#")) {
        selector = new ExecutionSelector(exec, null, null);
      } else {
        selector = new ExecutionSelector(exec + "#*", null, null);
      }
    } else if (excludes != null  || includes != null) {
      selector = new ExecutionSelector(includes, excludes, null);
    } else if (tag != null) {
      selector = new ExecutionSelector(null, null, tag);
    }

    if (vertxHome == null) {
      throw new MojoExecutionException("vert.x home not defined");
    }

    // Initialization
    try {
      Reporter.init();
    } catch (IOException e) {
      throw new MojoExecutionException("Cannot initialize templates", e);
    }

    File vertx;
    File java;
    if (!OS.isFamilyWindows()) {
      vertx = new File(vertxHome, "bin/vertx");
      java = new File(javaHome, "bin/java");
    } else {
      vertx = new File(vertxHome, "bin/vertx.bat");
      java = new File(javaHome, "bin/java.exe");
    }

    if (!vertx.isFile()) {
      throw new MojoExecutionException("vert.x command not found in "
          + vertxHome.getAbsolutePath());
    }

    if (!java.isFile()) {
      throw new MojoExecutionException("java command not found in "
          + javaHome.getAbsolutePath());
    }

    Executor.init(getLog(), ImmutableMap.of("vertx", vertx, "java", java));
    GroovyScriptHelper.init(classpathElements);

    try {
      filterRunFiles();
    } catch (MavenFilteringException e) {
      throw new MojoExecutionException("Cannot filter run.json file", e);
    }

    // Find the run.json files
    final Collection<File> files = FileUtils.listFiles(outputDirectory, new IOFileFilter() {
      @Override
      public boolean accept(File file) {
        return FilenameUtils.wildcardMatch(file.getName(), "*-run.json");
      }

      @Override
      public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
      }
    }, TrueFileFilter.INSTANCE);


    getLog().info(files.size() + " run files found");

    // Execute them
    for (File f : files) {
      try {
        Run run = new Run(f, getLog(), vertx, new File(buildDirectory, "it-reports"), itf);
        for (Execution execution : run.executions()) {
          if (selector.accept(run, execution)) {
            runs.add(run);
            run.execute(execution);
          }
        }
      } catch (IOException e) {
        getLog().error("Failed to execute " + f.getAbsolutePath(), e);
        throw new MojoExecutionException("Execution failure", e);
      }
    }

    // Report and decide whether we should fail the build
    getLog().info("--------------------------------------");
    for (Run r : runs) {
      for (Execution e : r.executions()) {
        if (Execution.Status.FAILURE.name().equals(e.getStatus())) {
          getLog().warn(e.getFullName() + " has failed: " + e.getReason().getMessage());
        }
        if (Execution.Status.ERROR.name().equals(e.getStatus())) {
          getLog().warn(e.getFullName() + " is in error: " + e.getReason().getMessage());
        }
      }
    }
    getLog().info("--------------------------------------");
    getLog().info("Number of runs: " + runs.size());
    getLog().info("Number of executions: " + getNumberOfExecutions());
    getLog().info("Number of succeeded executions: " + getNumberOfSucceededExecution());
    getLog().info("Number of failed executions: " + getNumberOfFailedExecution());
    getLog().info("Number of executions in error: " + getNumberOfExecutionInError());
    getLog().info("--------------------------------------");

    if (getNumberOfFailedExecution() > 0 || getNumberOfExecutionInError() > 0) {
      throw new MojoFailureException("Some executions has failed or are in error");
    }
  }

  private int getNumberOfExecutions() {
    int acc = 0;
    for (Run r : runs) {
      for (Execution e : r.executions()) {
        if (!Execution.Status.SKIPPED.name().equals(e.getStatus())) {
          acc++;
        }
      }
    }
    return acc;
  }

  private int getNumberOfSucceededExecution() {
    int acc = 0;
    for (Run r : runs) {
      for (Execution e : r.executions()) {
        if (Execution.Status.SUCCESS.name().equals(e.getStatus())) {
          acc++;
        }
      }
    }
    return acc;
  }

  private int getNumberOfFailedExecution() {
    int acc = 0;
    for (Run r : runs) {
      for (Execution e : r.executions()) {
        if (Execution.Status.FAILURE.name().equals(e.getStatus())) {
          acc++;
        }
      }
    }
    return acc;
  }

  private int getNumberOfExecutionInError() {
    int acc = 0;
    for (Run r : runs) {
      for (Execution e : r.executions()) {
        if (Execution.Status.ERROR.name().equals(e.getStatus())) {
          acc++;
        }
      }
    }
    return acc;
  }

  private void filterRunFiles() throws MavenFilteringException {
    Resource resource = new Resource();
    resource.setFiltering(true);
    resource.setDirectory(inputDirectory.getAbsolutePath());
    resource.setTargetPath(outputDirectory.getAbsolutePath());
    resource.setIncludes(ImmutableList.of("**/*-run.json"));

    MavenResourcesExecution mre = new MavenResourcesExecution(ImmutableList.of(resource), outputDirectory, project,
        "UTF-8", Collections.<String>emptyList(), Collections.<String>emptyList(), session);

    if (additionalProperties != null) {
      mre.setAdditionalProperties(additionalProperties);
    }
    filtering.filterResources(mre);
  }
}
