import org.fluentlenium.core.FluentPage
import org.openqa.selenium.Capabilities
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService

import static org.assertj.core.api.Assertions.assertThat

DesiredCapabilities capabilities = new DesiredCapabilities();
// Because script are loaded from https (CDN)
capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, ["--web-security=no", "--ignore-ssl-errors=yes"]);
capabilities.setCapability("acceptSslCerts", true)
PhantomJSDriverService service = PhantomJSDriverService.createDefaultService(capabilities)

driver = new PhantomJSDriver(service, capabilities)
helper.enqueueCloseable(driver)

def page = new FluentPage(driver) {
    public String getUrl() {
        "http://localhost:8080/index.html"
    }
}

page.go()
println page.text("body")
assertThat(page.$("#totalTimes").first().text).isEqualTo("1")
def cookie = driver.manage().getCookieNamed("visits")
println cookie.name + " " + cookie.value

driver.navigate().refresh();
cookie = driver.manage().getCookieNamed("visits")
println cookie.name + " " + cookie.value
assertThat(page.$("#totalTimes").first().text).isEqualTo("2")

return true


