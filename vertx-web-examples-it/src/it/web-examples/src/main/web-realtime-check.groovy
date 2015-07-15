import com.google.common.base.Predicate
import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.wait.FluentWait
import org.openqa.selenium.Capabilities
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService
import org.fluentlenium.core.search.Search
import java.util.concurrent.TimeUnit

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


page.await().pollingEvery(1, TimeUnit.SECONDS).atMost(15, TimeUnit.SECONDS)
        .until( (Predicate) {
            page.$("code").size() > 3
        })

println page.text("body")

return true
