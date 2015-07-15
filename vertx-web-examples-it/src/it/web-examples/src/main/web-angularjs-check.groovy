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

println "Initial checks"

// Check the <h1>
assertThat(page.find("h2").text).isEqualToIgnoringCase("Users")

// Check the the table is empty
assertThat(page.find("table tbody tr")).hasSize(2)

// Fill the form
println "Filling the form"
page.click("a.btn-primary")
page.fill("input[ng-model='user.username']").with("cescoffier")
page.fill("input[ng-model='user.firstName']").with("Clement")
page.fill("input[ng-model='user.lastName']").with("Escoffier")
page.fill("input[ng-model='user.address']").with("France")
page.click("button.btn-primary")

// Wait for response processing
page.await().atMost(10, TimeUnit.SECONDS).until("table tbody tr").hasSize(3)

return true
