package io.vertx.it.plugin;

import java.lang.reflect.Field;

/**
 * Created by clement on 05/07/2015.
 */
public class WindowsDestroyer {

  public static int getPid(Process p) {
    Field f;
    try {
      f = p.getClass().getDeclaredField("handle");
      f.setAccessible(true);
      return Kernel32.INSTANCE.GetProcessId((Long) f.get(p));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static void destroy(Process p) {
    final int pid = getPid(p);
    System.out.println("The PID: " + pid);
    String killCmd = "taskkill /F /T /PID " + pid;
    final Process process;
    try {
      process = Runtime.getRuntime().exec(killCmd);
      process.waitFor();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
