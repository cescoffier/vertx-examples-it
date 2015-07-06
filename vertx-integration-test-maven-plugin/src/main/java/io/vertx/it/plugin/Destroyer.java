package io.vertx.it.plugin;

import io.vertx.it.plugin.win.WindowsDestroyer;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.ProcessDestroyer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clement on 25/06/2015.
 */
class Destroyer implements ProcessDestroyer {

  public static final Destroyer INSTANCE = new Destroyer();

  private List<Process> processes = new ArrayList<>();

  private Destroyer() {
    // Avoid direct instantiation.
  }

  public void killThemAll() {
    System.out.println(processes.size() + " processes need to be destroyed");
    List<Process> copy = new ArrayList<>(processes);
    for (Process p : copy) {
      if (OS.isFamilyWindows()) {
        // Well windows...
        // Destroying the process won't kill children process,
        // so we need another dirty way
        killWindowsProcessAndChildren(p);
      } else {
        p.destroyForcibly();
      }
      processes.remove(p);
      System.out.println("Process destroyed");
    }
  }

  private void killWindowsProcessAndChildren(Process p) {
    WindowsDestroyer.destroy(p);
  }

  /**
   * Returns <code>true</code> if the specified
   * {@link Process} was
   * successfully added to the list of processes to be destroy.
   *
   * @param process the process to add
   * @return <code>true</code> if the specified
   * {@link Process} was
   * successfully added
   */
  @Override
  public boolean add(Process process) {
    return processes.add(process);
  }

  /**
   * Returns <code>true</code> if the specified
   * {@link Process} was
   * successfully removed from the list of processes to be destroy.
   *
   * @param process the process to remove
   * @return <code>true</code> if the specified
   * {@link Process} was
   * successfully removed
   */
  @Override
  public boolean remove(Process process) {
    return processes.remove(process);
  }

  /**
   * Returns the number of registered processes.
   *
   * @return the number of register process
   */
  @Override
  public int size() {
    return processes.size();
  }

}
