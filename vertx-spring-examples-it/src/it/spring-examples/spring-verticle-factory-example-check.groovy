import groovyx.net.http.HTTPBuilder
import org.apache.commons.io.IOUtils

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET

Thread.sleep(5000)
def data
def http = new HTTPBuilder('http://localhost:8080?name=vert.x')
http.request(GET, TEXT) { req ->
  response.success = { resp, reader ->
    assert resp.status == 200
    println "My response handler got response: ${resp.statusLine}"
    println "Response length: ${resp.headers.'Content-Length'}"
    data = IOUtils.toString(reader)
    assert data.contains("Hello vert.x")
  }

  // called only for a 404 (not found) status code:
  response.'404' = { resp ->
    assert false
  }
}

return data != null

