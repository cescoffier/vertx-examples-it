import com.google.common.base.Predicate
import org.fluentlenium.core.FluentPage
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

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

def last
page.await().pollingEvery(1, TimeUnit.SECONDS).atMost(15, TimeUnit.SECONDS)
        .until((Predicate) {
    last = page.$("#value").text
    ! page.$("#value").isEmpty()  && ! last.isEmpty();
})

println "last: " + last

page.await().pollingEvery(1, TimeUnit.SECONDS).atMost(5, TimeUnit.SECONDS)
        .until((Predicate) {
    println "value : " + page.$("#value").text + " / " + last
    !page.$("#value").text.equals(last);
})

return true
