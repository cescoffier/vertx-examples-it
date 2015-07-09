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

driver = new PhantomJSDriver(service, capabilities);
print capabilities.asMap()

FluentWait await(FluentPage page) {  new FluentWait(page, new Search(driver)) }

def page = new FluentPage(driver) {
    public String getUrl() {
        "http://localhost:8080"
    }
}

page.go()

// Wait for the connection to be established
// TODO Would be nice to have an idea about that.
Thread.sleep(5000)

println page.find("#chat").first().text
assertThat(page.find("#chat").text).isEmpty()

// Write a message
// The \n is important it simulate the enter key.
page.fill("#input").with("hello\n")

await(page).atMost(10, TimeUnit.SECONDS).until("#chat").containsText("hello")

return true
