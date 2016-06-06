import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT
import static org.assertj.core.api.Assertions.*;
import org.apache.commons.io.IOUtils

def http = new HTTPBuilder( 'http://localhost:8081' )
http.request(GET,TEXT) { req ->
  response.success = { resp, reader ->
    assert resp.status == 200
    def data = IOUtils.toString(reader)
    assertThat(data).contains("Hello World!")
  }

  // called only for a 404 (not found) status code:
  response.'404' = { resp ->
    assert false
  }
}

return true

