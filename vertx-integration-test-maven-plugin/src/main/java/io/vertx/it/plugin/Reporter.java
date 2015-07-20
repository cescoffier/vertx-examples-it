package io.vertx.it.plugin;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by clement on 29/06/2015.
 */
public class Reporter {

  private static Template markdownTemplate;
  private static Template htmlTemplate;
  private static Template globalReport;
  private static Template junitReport;

  public static void init() throws IOException {
    Handlebars handlebars = new Handlebars();
    markdownTemplate = handlebars.compile("templates/execution");
    htmlTemplate = handlebars.compile("templates/execution-html");
    globalReport = handlebars.compile("templates/report-html");
    junitReport = handlebars.compile("templates/junit-report");
  }

  public static void createGlobalReports(List<Execution> executions, File directory) throws IOException {

    String status = "Success";
    long total = 0;
    int success = 0;
    int failures = 0;
    int errors = 0;
    int execution = 0;
    List<Execution> nonSkippedExecutions = new ArrayList<>();


    for (Execution exec : executions) {
      if (! exec.isSkipped()) {
        if (!exec.isSuccess()) {
          status = "Failure";
        }

        if (exec.isSuccess()) {
          success++;
        } else if (exec.isFailure()) {
          failures++;
        } else if (exec.isError()) {
          errors++;
        }
        execution++;
        total += exec.getTime();
        nonSkippedExecutions.add(exec);
      }
    }

    Map<String, Object> model = ImmutableMap.<String, Object>builder()
        .put("date", DateFormat.getDateTimeInstance().format(new Date()))
        .put("status", status)
        .put("time", total)
        .put("execution", execution)
        .put("success", success)
        .put("failures", failures)
        .put("errors", errors)
        .put("executions", nonSkippedExecutions)
        .build();

    Context context = Context.newContext(model);
    final String result = globalReport.apply(context);
    File out = new File(directory, "index.html");
    FileUtils.write(out, result);
  }

  public static void createReports(Execution execution, File directory) throws IOException {
    createMarkdownReport(execution, directory);
    createHTMLReport(execution, directory);
    createJunitReport(execution, directory);
  }

  private static void createJunitReport(Execution execution, File directory) throws IOException {
    Context context = Context.newContext(execution);
    final String result = junitReport.apply(context);
    File out = new File(directory, execution.getReportName() + ".xml");
    FileUtils.write(out, result);
  }

  public static void createMarkdownReport(Execution execution, File directory) throws IOException {
    Context context = Context.newContext(execution);
    final String result = markdownTemplate.apply(context);
    File out = new File(directory, execution.getReportName() + ".md");
    FileUtils.write(out, result);
  }

  public static void createHTMLReport(Execution execution, File directory) throws IOException {
    Context context = Context.newContext(execution);
    final String result = htmlTemplate.apply(context);
    File out = new File(directory, execution.getReportName() + ".html");
    FileUtils.write(out, result);
  }

}
