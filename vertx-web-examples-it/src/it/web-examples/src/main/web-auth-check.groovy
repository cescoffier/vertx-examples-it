import org.fluentlenium.core.FluentPage
import org.openqa.selenium.Capabilities
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService

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

// Go to public page
page.click("a[href=\"page1.html\"]")

println "step1\n" + page.text("body")

assertThat(page.$("h1").first().text).contains("Welcome to page1!")

// Go back
driver.navigate().back()
println "step2\n" + page.text("body")
assertThat(page.$("h1").first().text).contains("Web site with public and private pages")

page.click("a[href=\"private/private_page.html\"]")
println "step3\n" + page.text("body")
// We go to a login page
assertThat(page.$("h2").first().text).contains("Please login")
page.fill("input[name=\"username\"]").with("tim")
page.fill("input[name=\"password\"]").with("sausages")
page.click("input[type=\"submit\"]")

// We should land into the private area
println "step4\n" + page.text("body")
assertThat(page.$("h2").first().text).contains("You can only see this page if you are logged in!")

return true
