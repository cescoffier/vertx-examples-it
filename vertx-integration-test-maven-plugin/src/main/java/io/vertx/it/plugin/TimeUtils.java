package io.vertx.it.plugin;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

  public static void sleep(long millis) {
    sleep(millis, TimeUnit.MILLISECONDS);
  }

  private static void sleep(long amount, TimeUnit unit) {
    try {
      Thread.sleep(unit.toMillis(amount) * getTimeFactor());
    } catch (InterruptedException e) {
      // Ignore it.
    }
  }

  public static int getTimeFactor() {
    return Integer.getInteger("time.factor", 1);
  }

}
