package io.vertx.it.plugin;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.exec.*;
import org.apache.commons.exec.util.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Executor {

  private static Log logger;
  private static Map<String, File> extra;

  private final LoggedOutputStream out;
  private final LoggedOutputStream err;

  private DefaultExecuteResultHandler result = new DefaultExecuteResultHandler();

  public static void init(Log log, Map<String, File> extraPath) {
    extra = extraPath;
    logger = log;
  }

  public Executor() {
    out = new LoggedOutputStream(logger, false, true);
    err = new LoggedOutputStream(logger, true, true);
  }

  public String execute(String cmd, File cwd) throws IOException {
    DefaultExecutor executor = new DefaultExecutor();

    cmd = cmd.trim();

    for (Map.Entry<String, File> entry : extra.entrySet()) {
      if (cmd.startsWith(entry.getKey() + " ")) {
        cmd = cmd.replaceFirst(entry.getKey(), entry.getValue().getAbsolutePath());
        break;
      }
    }

    final CommandLine command = CommandLine.parse(cmd);

    ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
    executor.setWorkingDirectory(cwd);
    executor.setWatchdog(watchdog);
    executor.setStreamHandler(new PumpStreamHandler(out, err));
    executor.setProcessDestroyer(Destroyer.INSTANCE);
    executor.setExitValues(new int[]{143, 137, 0, 1});

    executor.execute(command, result);

    return StringUtils.toString(command.toStrings(), " ");
  }

  public void waitForTermination() throws IOException {
    try {
      result.waitFor();
    } catch (InterruptedException e) {
      // Ignore it.
    }
    if (result.getException() != null) {
      // Will mark the execution in error.
      throw new IOException(result.getException());
    }
  }

  public String getOutput() {
    return out.getOutput();
  }

  public String getError() {
    return err.getOutput();
  }


}
