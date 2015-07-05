import groovyx.net.http.HTTPBuilder
import org.apache.commons.io.FileUtils
import org.apache.http.entity.mime.content.FileBody

import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.TEXT
import static org.assertj.core.api.Assertions.*;
import org.apache.commons.io.IOUtils
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.StringBody

def http = new HTTPBuilder( 'http://localhost:8080/form' )
def data;

def file = new File("junk.txt")
FileUtils.write(file, "hello I'm just here because I'm going to be uploaded")
http.request (POST) { multipartRequest ->
    MultipartEntityBuilder multipartRequestEntity = new MultipartEntityBuilder()
    multipartRequestEntity.addPart('foo', new FileBody(file));
    multipartRequest.entity = multipartRequestEntity.build()

    response.success = { resp, reader ->
        assert resp.status == 200
        data = IOUtils.toString(reader)
        println data
        assertThat(data).contains("Successfully uploaded to junk.txt")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }

    return data
}

FileUtils.deleteQuietly(file)

return data != null

