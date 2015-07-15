package io.vertx.it.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.scriptinterpreter.ScriptRunner;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GroovyScriptHelper {

  private static List<String> classpath;

  public final Run run;

  public final Executor main;

  public final Executor client;

  public final File base;

  public final JsonNode json;

  public final String client_output;

  public final String client_error;

  public final String output;

  public final String error;

  private List<Object> closeables = new ArrayList<>();

  public static void init(List<String> classpathElements) {
    classpath = classpathElements;
  }


  public GroovyScriptHelper(Run run, Executor main, Executor client, File base, JsonNode json) {
    this.run = run;
    this.main = main;
    this.client = client;
    this.base = base;
    this.json = json;

    if (client != null) {
      this.client_output = client.getOutput();
      this.client_error = client.getError();
    } else {
      this.client_output = "";
      this.client_error = "";
    }

    output = main.getOutput();
    error = main.getError();
  }

  public boolean ensureSucceededInDeployingVerticle() {
    return ensureTextInErrorStream("Succeeded in deploying verticle");
  }

  public boolean ensureClientSucceededInDeployingVerticle() {
    return ensureTextInClientErrorStream("Succeeded in deploying verticle");
  }

  public boolean ensureTextInErrorStream(String text) {
    if (!error.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Cannot find text `" + text + "` in the error stream");
    }
    return true;
  }

  public boolean ensureTextInClientErrorStream(String text) {
    if (!client_error.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Cannot find text `" + text + "` in the client error stream");
    }
    return true;
  }

  public boolean ensureTextInOutputStream(String text) {
    if (!output.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Cannot find text `" + text + "` in the output stream");
    }
    return true;
  }

  public boolean ensureTextInClientOutputStream(String text) {
    if (!client_output.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Cannot find text `" + text + "` in the client output stream");
    }
    return true;
  }

  public boolean ensureTextNotContainedInErrorStream(String text) {
    if (error.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Text `" + text + "` found in the error stream");
    }
    return true;
  }

  public boolean ensureTextNotContainedInClientErrorStream(String text) {
    if (client_error.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Text `" + text + "` found in the client error stream");
    }
    return true;
  }

  public boolean ensureTextNotContainedInOutputStream(String text) {
    if (output.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Text `" + text + "` found in the output");
    }
    return true;
  }

  public boolean ensureTextNotContainedInClientOutputStream(String text) {
    if (client_output.toLowerCase().contains(text.toLowerCase())) {
      throw new AssertionError("Text `" + text + "` found in the client output");
    }
    return true;
  }

  public boolean enqueueCloseable(Object o) {
    return closeables.add(o);
  }

  public void close() {
    for (Object o : closeables) {
      if (o instanceof Closeable) {
        IOUtils.closeQuietly((Closeable) o);
      } else {
        IOUtils.closeQuietly(() -> {
          try {
            Method method = o.getClass().getMethod("close");
            method.invoke(o);

            try {
              method = o.getClass().getMethod("quit");
              method.invoke(o);
            } catch (NoSuchMethodException e) {
              // Ignore it.
            }
          } catch (Exception e) {
            System.err.println("Cannot close " + o);
            e.printStackTrace();
          }
        });
      }
    }

  }

  public static void addClasspathToScript(ScriptRunner runner) {
    runner.setClassPath(classpath);
  }

}
