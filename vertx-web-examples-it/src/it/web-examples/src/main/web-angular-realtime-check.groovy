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

driver = new PhantomJSDriver(service, capabilities)
helper.enqueueCloseable(driver)

def page = new FluentPage(driver) {
    public String getUrl() {
        "http://localhost:8080"
    }
}

page.go()

// Ghost Driver does not support alert yet. This is a workaround 'overriding' alerts
driver.executeScript("window.alert = function(msg){};");
driver.executeScript("window.confirm = function(msg){return true;};");

println "Login"

page.fill("#username").with("tim")
page.fill("#password").with("sausages")
page.find("input[type='submit']").first().click()

println "Shopping"
page.await().atMost(10, TimeUnit.SECONDS).until("#shop h2").containsText("Please choose")
page.find("#shop a[ng-click='addToCart(album)']").first().click()

println "Cart"
page.click("a[href='#cart']")

println "Cancel"
assertThat(page.find("#cart table tr")).hasSize(2)
page.find("#cart a[ng-click='removeFromCart(item)']").first().click()

assertThat(page.find("#cart table tr")).hasSize(1)

return true
