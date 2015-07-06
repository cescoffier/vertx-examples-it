package io.vertx.it.plugin.win;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Created by clement on 05/07/2015.
 */
interface Kernel32 extends Library {

  public static Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

  public int GetProcessId(Long hProcess);
}
