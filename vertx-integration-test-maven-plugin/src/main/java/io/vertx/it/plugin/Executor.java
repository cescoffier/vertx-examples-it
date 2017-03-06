package io.vertx.it.plugin;

import org.apache.commons.exec.*;
import org.apache.commons.exec.util.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Executor {

  private static Log logger;
  private static Map<String, File> extra;

  private final LoggedOutputStream out;
  private final LoggedOutputStream err;

  private DefaultExecuteResultHandler result = new DefaultExecuteResultHandler();
  private Map<String, String> env;

  public static void init(Log log, Map<String, File> extraPath) {
    extra = extraPath;
    logger = log;
  }

  public Executor() {
    out = new LoggedOutputStream(logger, false, true);
    err = new LoggedOutputStream(logger, true, true);
  }

  public Executor setEnv(Map<String, String> env) {
    this.env = env;
    return this;
  }

  public String execute(String cmd, File cwd) throws IOException {
    DefaultExecutor executor = new DefaultExecutor();
    CommandLine command = null;
    cmd = cmd.trim();

    // Manage the command to execute
    boolean found = false;
    for (Map.Entry<String, File> entry : extra.entrySet()) {
      if (cmd.startsWith(entry.getKey() + " ")) {
        if (entry.getKey().startsWith("java")) {
          command = new CommandLine("java");
        } else {
          cmd = cmd.replaceFirst(entry.getKey(), entry.getValue().getAbsolutePath());
          command = new CommandLine(entry.getValue());
        }
        found = true;
        break;
      }
    }

    if (!found) {
      command = new CommandLine(CommandLine.parse(cmd).getExecutable().replace("/", File.separator));
    }

    // Manage -cp or -classpath (replace : by the system character, fix / and \\)
    command = prepare(command, cmd);

    ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
    executor.setWorkingDirectory(cwd);
    executor.setWatchdog(watchdog);
    executor.setStreamHandler(new PumpStreamHandler(out, err));
    executor.setProcessDestroyer(Destroyer.INSTANCE);
    executor.setExitValues(new int[]{143, 137, 0, 1});
    logger.info("Executing " + command.toString());
    executor.execute(command, getEnv(), result);

    return StringUtils.toString(command.toStrings(), " ");
  }

  private Map<String, String> getEnv() {
    if (this.env == null) {
      return System.getenv();
    } else {
      Map<String, String> current = new HashMap<>(System.getenv());
      logger.info("Extending environment with " + env);
      current.putAll(env);
      return current;
    }
  }

  private CommandLine prepare(CommandLine command, String cmd) {
    final CommandLine line = CommandLine.parse(cmd);

    final String[] arguments = line.getArguments();
    boolean cpFlag = false;
    for (String arg : arguments) {
      if (!cpFlag) {
        if (arg.equalsIgnoreCase("-cp") || arg.equalsIgnoreCase("-classpath")) {
          cpFlag = true;
        }
        command.addArgument(arg);
      } else {
        command.addArgument(arg.replace(":", File.pathSeparator));
        cpFlag = false;
      }
    }
    return command;
  }

  public void waitForTermination() throws IOException {
    try {
      result.waitFor(TimeUnit.SECONDS.toMillis(10));
    } catch (InterruptedException e) {
      // Ignore it.
    }
//    if (result.getException() != null) {
//      // Will mark the execution in error.
//      throw new IOException(result.getException());
//    }
  }

  public String getOutput() {
    return out.getOutput();
  }

  public String getError() {
    return err.getOutput();
  }


}
