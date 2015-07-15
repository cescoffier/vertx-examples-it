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
import static org.assertj.core.api.Assertions.assertThat

Capabilities capabilities = new DesiredCapabilities();
DriverService service = PhantomJSDriverService.createDefaultService(capabilities);
driver = new PhantomJSDriver(service, capabilities);
helper.enqueueCloseable(driver)

def page = new FluentPage(driver) {
    public String getUrl() {
        "http://localhost:8080"
    }
}

page.go()

// Check we are on the index page
assertThat(page.$("h1").first().text).contains("Web site with public and private pages")

// No token, no access to anything
page.click("#getProtected")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")

// Get a token without authorities
page.click("#generateToken")
page.await().atMost(10, TimeUnit.SECONDS).until((Predicate) {
    page.$("#token").text.length() > 30
})
page.click("#getProtected")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("a secret you should keep for yourself...")

return true
