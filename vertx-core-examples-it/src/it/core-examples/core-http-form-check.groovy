import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.TEXT
import static org.assertj.core.api.Assertions.*;
import org.apache.commons.io.IOUtils
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.StringBody

def http = new HTTPBuilder( 'http://localhost:8080/form' )
def data;
http.request (POST) { multipartRequest ->
    MultipartEntityBuilder multipartRequestEntity = new MultipartEntityBuilder()
    multipartRequestEntity.addPart('foo', new StringBody("val1"))
    multipartRequestEntity.addPart('bar', new StringBody("val2"))
    multipartRequestEntity.addPart('quux', new StringBody("val3"))
    multipartRequestEntity.addPart('name', new StringBody("val4"))
    multipartRequest.entity = multipartRequestEntity.build()

    response.success = { resp, reader ->
        assert resp.status == 200
        data = IOUtils.toString(reader)
        println data
        assertThat(data).contains("bar : val2", "foo : val1", "name : val4", "quux : val3")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }

    return data
}

return data != null

