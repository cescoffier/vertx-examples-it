import groovyx.net.http.HTTPBuilder
import org.apache.commons.io.IOUtils

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET

def data
Thread.sleep(3000)
def http = new HTTPBuilder('http://localhost:8080/page2.html')
http.request(GET, TEXT) { req ->
  response.success = { resp, reader ->
    assert resp.status == 200
    println "My response handler got response: ${resp.statusLine}"
    println "Response length: ${resp.headers.'Content-Length'}"
    data = IOUtils.toString(reader)
  }

  // called only for a 404 (not found) status code:
  response.'404' = { resp ->
    assert false
  }
}

return data != null

