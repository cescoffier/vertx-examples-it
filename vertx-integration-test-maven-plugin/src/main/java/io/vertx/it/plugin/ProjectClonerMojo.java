package io.vertx.it.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Mojo(name = "clone", threadSafe = false,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresProject = true,
    defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ProjectClonerMojo extends AbstractMojo {

  /**
   * The target directory of the project.
   */
  @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
  public File buildDirectory;

  /**
   * The current build session instance.
   */
  @Component
  public MavenSession session;

  /**
   * Maven ProjectHelper.
   */
  @Component
  public MavenProjectHelper projectHelper;

  @Parameter
  public String[] includes;

  @Parameter
  public String[] excludes;

  @Parameter(required = true)
  public URL url;

  @Parameter(defaultValue = "${clone.skip}")
  public boolean skip;

  @Parameter(defaultValue = "${basedir}/src/it")
  public File output;

  @Parameter
  public String prefix;

  @Component
  public ArchiverManager archiverManager;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Cloning skipped");
      return;
    }

    File tmp = new File(buildDirectory, "tmp");
    if (!tmp.isDirectory()) {
      tmp.mkdirs();
    }

    File out = new File(tmp, url.getFile());
    getLog().info("Downloading " + url + " to " + out.getAbsolutePath());
    try {
      FileUtils.copyURLToFile(url, out);
    } catch (IOException e) {
      getLog().error("Clone failed", e);
      throw new MojoExecutionException("Cannot download file " + url, e);
    }

    // Downloaded
    getLog().info(url + " downloaded (" + out.length() + " bytes)");

    // Unzipping
    try {
      unpack(out, excludes, includes);
    } catch (Exception e) {
      getLog().error("Unpack failed", e);
      throw new MojoExecutionException("Cannot unpack " + out.getAbsolutePath() + " to " + output.getAbsolutePath(), e);
    }

    // Copy
    File root = new File(buildDirectory, "tmp/unzip");
    if (! root.isDirectory()) {
      throw new MojoExecutionException("Something went wrong during the unpacking - " + root.getAbsolutePath() + " is" +
          " not a directory");
    }

    if (prefix != null) {
      root = new File(root, prefix);
    }

    try {
      FileUtils.copyDirectory(root, output);
    } catch (IOException e) {
      throw new MojoExecutionException("Cannot copy unzipped content", e);
    }

  }

  protected void unpack(File srcFile, String[] excludes, String[] includes) throws Exception {
    File dest = new File(buildDirectory, "tmp/unzip");
    dest.mkdirs();
    UnArchiver unArchiver = archiverManager.getUnArchiver(srcFile);
    unArchiver.setSourceFile(srcFile);
    unArchiver.setDestDirectory(dest);
    IncludeExcludeFileSelector includeExcludeFileSelector = new IncludeExcludeFileSelector();
    includeExcludeFileSelector.setExcludes(excludes);
    includeExcludeFileSelector.setIncludes(includes);
    unArchiver.setFileSelectors(new FileSelector[]{includeExcludeFileSelector});
    unArchiver.extract("", dest);
  }
}
