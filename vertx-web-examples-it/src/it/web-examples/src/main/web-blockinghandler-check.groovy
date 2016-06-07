import groovyx.net.http.HTTPBuilder
import org.apache.commons.io.IOUtils

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET
import static org.assertj.core.api.Assertions.assertThat

def data
def http = new HTTPBuilder('http://localhost:8080')
long begin = System.nanoTime()
http.request(GET, TEXT) { req ->
    response.success = { resp, reader ->
        assert resp.status == 200
        println "My response handler got response: ${resp.statusLine}"
        println "Response length: ${resp.headers.'Content-Length'}"
        data = IOUtils.toString(reader)
        assertThat(data).contains("Hello World!")
        long end = System.nanoTime()
        def time = end - begin
        assertThat(time).isGreaterThanOrEqualTo(5000l)
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }
}

return data != null

