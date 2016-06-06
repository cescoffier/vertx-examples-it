import org.fluentlenium.core.FluentPage
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

import java.util.concurrent.TimeUnit

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
        "http://localhost:8080"
    }
}

page.go()

System.out.println("content: " + page.$("body").text)

page.await().atMost(10, TimeUnit.SECONDS).until("input").hasSize().greaterThanOrEqualTo(1)
page.fill("input").with("clement\n")

page.await().atMost(10, TimeUnit.SECONDS).until("span").containsText("type a message")
page.fill("input").with("bonjour\n")

page.await().atMost(10, TimeUnit.SECONDS).until("span").containsText("bonjour")

return true
