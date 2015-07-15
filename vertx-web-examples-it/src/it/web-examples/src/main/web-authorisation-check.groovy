import com.google.common.base.Predicate
import org.fluentlenium.core.FluentPage
import org.openqa.selenium.Capabilities
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService

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
page.click("#getProtected0")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")

// Get a token without authorities
page.click("#generateToken0")
page.await().atMost(10, TimeUnit.SECONDS).until((Predicate) {
    page.$("#token").text.length() > 50
})
page.click("#getProtected0")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is not defcon!")
page.click("#getProtected1")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")

// Reload the page
driver.navigate().refresh()

// Generate a token defcon 1
page.click("#generateToken1")
page.await().atMost(10, TimeUnit.SECONDS).until((Predicate) {
    page.$("#token").text.length() > 50
})
page.click("#getProtected0")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is not defcon!")
page.click("#getProtected1")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is defcon1!")
page.click("#getProtected2")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")

// Reload the page
driver.navigate().refresh()

// Generate a token defcon 2
page.click("#generateToken2")
page.await().atMost(10, TimeUnit.SECONDS).until((Predicate) {
    page.$("#token").text.length() > 50
})
page.click("#getProtected0")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is not defcon!")
page.click("#getProtected1")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")
page.click("#getProtected2")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is defcon2!")
page.click("#getProtected3")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")

// Reload the page
driver.navigate().refresh()

// Generate a token defcon 1 and 2
page.click("#generateToken3")
page.await().atMost(10, TimeUnit.SECONDS).until((Predicate) {
    page.$("#token").text.length() > 50
})
page.click("#getProtected0")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is not defcon!")
page.click("#getProtected1")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is defcon1!")
page.click("#getProtected2")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("this secret is defcon2!")
page.click("#getProtected3")
page.await().atMost(10, TimeUnit.SECONDS).until("#protected").containsText("Error:")

return true
