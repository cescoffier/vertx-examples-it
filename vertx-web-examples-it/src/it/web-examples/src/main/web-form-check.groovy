import groovyx.net.http.HTTPBuilder
import org.apache.commons.io.IOUtils
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.StringBody

import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.POST
import static org.assertj.core.api.Assertions.assertThat

def http = new HTTPBuilder('http://localhost:8080/form')
def data;
http.request(POST) { multipartRequest ->
    MultipartEntityBuilder multipartRequestEntity = new MultipartEntityBuilder()
    multipartRequestEntity.addPart('name', new StringBody("vert.x"))
    multipartRequest.entity = multipartRequestEntity.build()

    response.success = { resp, reader ->
        assert resp.status == 200
        data = IOUtils.toString(reader)
        println data
        assertThat(data).contains("Hello vert.x")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }

    return data
}

assert data != null

// Try using URL Encoding
data = null

http.request(POST) {
    body = [name: 'bob']
    requestContentType = URLENC

    response.success = { resp, reader ->
        assert resp.status == 200
        data = IOUtils.toString(reader)
        println data
        assertThat(data).contains("Hello bob")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }

    return data
}

return data != null

