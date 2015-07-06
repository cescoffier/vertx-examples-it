package io.vertx.it.plugin;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

import java.util.Collections;

/**
 * Created by clement on 05/07/2015.
 */
public class ExecutionSelector {

  private final Iterable<String> includes;
  private final Iterable<String> excludes;
  private final String tag;

  public ExecutionSelector(String includes, String excludes, String tag) {
    if (includes == null) {
      this.includes = Collections.emptyList();
    } else {
      this.includes = Splitter.on(",").trimResults().split(includes);
    }

    if (excludes == null) {
      this.excludes = Collections.emptyList();
    } else {
      this.excludes = Splitter.on(",").trimResults().split(excludes);
    }

    this.tag = tag;
  }

  public boolean accept(Run run, Execution execution) {
    if (tag != null) {
      if (!run.hasTag(tag)) {
        return false;
      }
    }

    // check if not excluded
    if (!Iterables.isEmpty(excludes)) {
      for (String wildcard : excludes) {
        if (FilenameUtils.wildcardMatch(execution.getFullName(), wildcard, IOCase.INSENSITIVE)) {
          return false;
        }
      }
    }

    // check if included
    if (!Iterables.isEmpty(includes)) {
      for (String wildcard : includes) {
        if (FilenameUtils.wildcardMatch(execution.getFullName(), wildcard, IOCase.INSENSITIVE)) {
          return true;
        }
      }
    }

    if (Iterables.isEmpty(excludes) && Iterables.isEmpty(includes)) {
      // Accept all
      return true;
    } else if (!Iterables.isEmpty(excludes) && Iterables.isEmpty(includes)) {
      // We had excludes, it did not match
      return true;
    } else if (!Iterables.isEmpty(includes)) {
      // We had includes, it did not match
      return false;
    } else {
      // neither excludes or includes are empty
      // it did not match -> reject
      return false;
    }
  }

}
