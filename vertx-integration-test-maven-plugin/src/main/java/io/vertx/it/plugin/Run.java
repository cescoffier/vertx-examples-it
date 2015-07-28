package io.vertx.it.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.scriptinterpreter.RunFailureException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Run {

  public static final ObjectMapper MAPPER = new ObjectMapper();
  private final Log log;
  private final File libs;
  private JsonNode node;
  private final File file;
  private final File vertx;
  private final File reportDirectory;
  private final String itf;

  private List<Execution> executions = new ArrayList<>();

  private long totalExecutionTime;

  public Run(File json, Log log, File vertx, File reportDirectory, String itf) {
    file = json;
    this.vertx = vertx;
    this.reportDirectory = reportDirectory;
    this.itf = itf;
    this.log = log;
    this.libs = new File(vertx.getParentFile().getParentFile(), "lib");
  }

  public void prepare() throws IOException {
    node = MAPPER.readTree(file);
    final JsonNode exec = node.get("executions");
    if (exec == null) {
      throw new IOException("No execution defined in " + file.getAbsolutePath());
    }
    if (!exec.isObject()) {
      throw new IOException("Invalid executions object - it must be an object");
    }

    Iterator<String> names = exec.fieldNames();
    while (names.hasNext()) {
      String name = names.next();
      executions.add(new Execution(this, name, exec.get(name), log));
    }

    if (node.get("libraries") != null && node.get("libraries").isArray()) {
      for (JsonNode t : node.get("libraries")) {
        File file = new File(t.asText());
        if (!file.isFile()) {
          log.warn("Cannot copy " + file.getAbsolutePath() + " to the lib directory - the file does not exist");
        } else {
          log.info("Coyping " + file.getAbsolutePath() + " to " + libs.getAbsolutePath() + " - the file will be " +
              "removed once the run has been completed");
          FileUtils.copyFileToDirectory(file, libs);
        }
      }
    }
  }

  public void cleanup() {
    if (node.get("libraries") != null && node.get("libraries").isArray()) {
      for (JsonNode t : node.get("libraries")) {
        File file = new File(libs, new File(t.asText()).getName());
        if (!file.isFile()) {
          log.warn("Cannot delete " + file.getAbsolutePath() + " - the file does not exist");
        } else {
          FileUtils.deleteQuietly(file);
        }
      }
    }
  }


  public void execute(Execution execution) throws IOException {
    long begin = System.currentTimeMillis();
    try {
      execution.execute();
      execution.markAsSuccess();
      System.err.println("\t" + execution.getFullName() + " succeeded");
    } catch (RunFailureException e) {
      System.err.println("\t" + execution.getFullName() + " has failed - " + e.getMessage());
      execution.markAsFailed(e);
    } catch (Throwable e) {
      System.err.println("\t" + execution.getFullName() + " is in error - " + e.getMessage());
      execution.markAsError(e);
    } finally {
      execution.cleanup();
      long end = System.currentTimeMillis();
      execution.setExecutionTime(end - begin);
      totalExecutionTime += (end - begin);
      execution.dumpReport(reportDirectory);
    }
  }

  public File vertx() {
    return vertx;
  }

  public File base() {
    return file.getParentFile();
  }

  public String name() {
    return node.get("name").asText();
  }

  public void waitExecutionTime() {
    final JsonNode jsonNode = node.get("execution-time");
    if (jsonNode == null) {
      TimeUtils.sleep(2000);
    } else {
      TimeUtils.sleep(jsonNode.asInt());
    }
  }

  public String getPostCheck() {
    if (node.get("post-check") != null) {
      return node.get("post-check").asText();
    }
    return null;
  }

  public File file() {
    return file;
  }

  public long totalExecutionTime() {
    return totalExecutionTime;
  }

  public List<Execution> executions() {
    return executions;
  }

  public boolean hasTag(String tag) {
    if (node.get("tags") == null || !node.get("tags").isArray()) {
      return false;
    }
    for (final JsonNode v : node.get("tags")) {
      if (tag.equalsIgnoreCase(v.asText())) {
        return true;
      }
    }
    return false;
  }

  public String getClientCheck() {
    if (node.get("client-check") != null) {
      return node.get("client-check").asText();
    }
    return null;
  }

  public boolean requireCluster() {
    return node.get("cluster") != null && node.get("cluster").asBoolean();
  }

  public String getInterface() {
    if (itf != null) {
      return itf;
    } else {
      try {
        return InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        throw new IllegalArgumentException("Cannot find the localhost network interface, " +
            "set it using -Dinterface=...");
      }
    }
  }

  public int getGracePeriod() {
    final JsonNode jsonNode = node.get("grace-period");
    if (jsonNode == null) {
      return 2000;
    } else {
      return jsonNode.asInt();
    }
  }

  public String getGraceText() {
    JsonNode n = node.get("grace-text");
    if (n != null) {
      return n.asText();
    }
    return null;
  }

  public String getExecuteUntil() {
    if (node.get("execute-until") != null) {
      return node.get("execute-until").asText();
    }
    return null;
  }

  public String getClientExecuteUntil() {
    if (node.get("client-execute-until") != null) {
      return node.get("client-execute-until").asText();
    }
    return null;
  }

  public Map<String, String> getEnv() {
    if (node.get("env") != null) {
      return Run.MAPPER.convertValue(node.get("env"), Map.class);
    } else {
      return null;
    }
  }
}
