package io.vertx.it.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.scriptinterpreter.RunFailureException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Run {

  public static final ObjectMapper MAPPER = new ObjectMapper();

  private final JsonNode node;
  private final File file;
  private final File vertx;
  private final File reportDirectory;
  private final String itf;


  private List<Execution> executions = new ArrayList<>();

  private long totalExecutionTime;

  public Run(File json, Log log, File vertx, File reportDirectory, String itf) throws IOException {
    file = json;
    node = MAPPER.readTree(file);
    this.vertx = vertx;
    this.reportDirectory = reportDirectory;
    if (!reportDirectory.isDirectory()) {
      reportDirectory.mkdirs();
    }

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
    this.itf = itf;
  }


  public void execute(String exec) throws IOException {
    // Execution
    for (Execution execution : executions) {
      if (exec == null  || ! exec.contains("#") || execution.getFullName().equalsIgnoreCase(exec)) {
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

  public boolean matches(String exec) {
    if (!exec.contains("#")) {
      return name().equalsIgnoreCase(exec);
    } else {
      for (Execution e : executions) {
        if (e.getFullName().equalsIgnoreCase(exec)) {
          return true;
        }
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
}
