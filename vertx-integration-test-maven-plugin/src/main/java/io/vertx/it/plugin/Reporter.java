package io.vertx.it.plugin;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by clement on 29/06/2015.
 */
public class Reporter {

  private static Template template;

  public static void init() throws IOException {
    Handlebars handlebars = new Handlebars();
    template = handlebars.compile("templates/execution");
  }

  public static void createReportForExecution(Execution execution, File directory) throws IOException {
    Context context = Context.newContext(execution);
    final String result = template.apply(context);
    File out = new File(directory, execution.getFullName().replace(" ", "_") + ".md");
    FileUtils.write(out, result);
  }

  public void createGlobalReport() {

  }

}
