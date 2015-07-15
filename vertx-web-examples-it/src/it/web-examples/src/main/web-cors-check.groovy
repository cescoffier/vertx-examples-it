import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.search.Search
import org.fluentlenium.core.wait.FluentWait
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

import java.util.concurrent.TimeUnit

import static org.assertj.core.api.Assertions.assertThat


// Download preflight and no-preflight pages
println "Downloading pages"
File nopreflight = File.createTempFile("no-preflight", ".html")
File preflight = File.createTempFile("no-preflight", ".html")
FileUtils.write(nopreflight,
        IOUtils.toString(new URL("http://localhost:8080/nopreflight.html")))
FileUtils.write(preflight,
        IOUtils.toString(new URL("http://localhost:8080/preflight.html")))

println "Loading first page (preflight)"

DesiredCapabilities capabilities = new DesiredCapabilities();
PhantomJSDriverService service = PhantomJSDriverService.createDefaultService(capabilities);
driver = new PhantomJSDriver(service, capabilities)
helper.enqueueCloseable(driver)

def page = new FluentPage(driver) {
    public String getUrl() {
        preflight.toURI().toURL().toExternalForm()
    }
}

page.go()

page.click("input")
assertThat(page.$("#textDiv").text).contains("localhost:8080")

println "Loading second page (no-preflight)"

page = new FluentPage(driver) {
    public String getUrl() {
        nopreflight.toURI().toURL().toExternalForm()
    }
}

page.go()

page.click("input")
assertThat(page.$("#textDiv").text).contains("localhost:8080")

return true
