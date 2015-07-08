import org.fluentlenium.core.FluentPage
import org.openqa.selenium.Capabilities
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService

import static org.assertj.core.api.Assertions.assertThat

Capabilities capabilities = new DesiredCapabilities();
DriverService service = PhantomJSDriverService.createDefaultService(capabilities);
def driver = new PhantomJSDriver(service, capabilities);

def page = new FluentPage(driver) {
    public String getUrl() {
        "http://localhost:8080"
    }
}

page.go()
println page.text("body")
assertThat(page.$("h1").first().text).contains("1")

driver.navigate().refresh();
assertThat(page.$("h1").first().text).contains("2")

driver.navigate().refresh();
assertThat(page.$("h1").first().text).contains("3")


return true


