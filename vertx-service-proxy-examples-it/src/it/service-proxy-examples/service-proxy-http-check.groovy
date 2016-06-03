import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.search.Search
import org.fluentlenium.core.wait.FluentWait
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

import java.util.concurrent.TimeUnit

import static org.assertj.core.api.Assertions.assertThat

DesiredCapabilities capabilities = new DesiredCapabilities();
// Because script are loaded from https (CDN)
capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, ["--web-security=no", "--ignore-ssl-errors=yes"]);
capabilities.setCapability("acceptSslCerts", true)
PhantomJSDriverService service = PhantomJSDriverService.createDefaultService(capabilities);

driver = new PhantomJSDriver(service, capabilities)
helper.enqueueCloseable(driver)

def page = new FluentPage(driver) {
  public String getUrl() {
    "http://localhost:8080"
  }
}

page.go()

// Ghost Driver does not support alert yet. This is a workaround 'overriding' alerts
driver.executeScript("window.alert = function(msg){};");
driver.executeScript("window.confirm = function(msg){return true;};");

println "Approved check"

println page.text("body")

page.await().atMost(10, TimeUnit.SECONDS).until("#result").containsText("approved")

page = new FluentPage(driver) {
  public String getUrl() {
    "http://localhost:8080?document_name=bad"
  }
}

page.go()

page.await().atMost(10, TimeUnit.SECONDS).until("#result").containsText("Bad name in the document")


return true
