import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT
import static org.assertj.core.api.Assertions.*;
import org.apache.commons.io.IOUtils

def http = new HTTPBuilder( 'http://localhost:8080' )
http.request(GET,TEXT) { req ->
    response.success = { resp, reader ->
        assert resp.status == 200
        def data = IOUtils.toString(reader)
        assertThat(data).contains("This is the vert.x static web server. Click on some links below")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }
}

http = new HTTPBuilder( 'http://localhost:8080/page2.html' )
http.request(GET,TEXT) { req ->
    response.success = { resp, reader ->
        assert resp.status == 200
        def data = IOUtils.toString(reader)
        assertThat(data).contains("<h1>Welcome to page2!</h1>")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }
}

return true

