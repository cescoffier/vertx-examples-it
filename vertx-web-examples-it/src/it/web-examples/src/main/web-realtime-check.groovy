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

Capabilities capabilities = new DesiredCapabilities();
DriverService service = PhantomJSDriverService.createDefaultService(capabilities);
driver = new PhantomJSDriver(service, capabilities);

FluentWait await(FluentPage page) {  new FluentWait(page, new Search(driver)) }

def page = new FluentPage(driver) {
    public String getUrl() {
        "http://localhost:8080"
    }
}

page.go()


await(page).pollingEvery(1, TimeUnit.SECONDS).atMost(15, TimeUnit.SECONDS).until( (Predicate) {
    page.$("code").size() > 3
})

println page.text("body")

return true
