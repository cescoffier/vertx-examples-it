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

println "Initial checks"

// Check the <h1>
assertThat(page.find("h1").text).isEqualToIgnoringCase("Vert.x Web")

// Check the the table is empty
assertThat(page.find("#userList table tbody tr")).isEmpty()

// Fill the form
println "Filling the form"

page.fill("#inputUserName").with("clement")
page.fill("#inputUserEmail").with("c@a.fr")
page.fill("#inputUserFullname").with("ces")
page.fill("#inputUserAge").with("34")
page.fill("#inputUserLocation").with("France")
page.fill("#inputUserGender").with("M")
page.click("#btnAddUser")

// Wait for response processing
page.await().atMost(10, TimeUnit.SECONDS).until("#userList table tbody tr").hasSize(1)

println "Check user detail"
page.click("a[rel='clement']")
page.await().atMost(10, TimeUnit.SECONDS).until("#userInfoName").hasText("ces")
assertThat(page.find("#userInfoAge").text).contains("34")
assertThat(page.find("#userInfoGender").text).containsIgnoringCase("M")
assertThat(page.find("#userInfoLocation").text).containsIgnoringCase("France")

println "Deleting user"
page.click(".linkdeleteuser")

page.await().atMost(10, TimeUnit.SECONDS).until("#userList table tbody tr").hasSize(0)

return true
