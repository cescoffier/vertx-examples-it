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
        assertThat(data)
                .contains("Static and dynamic server. Click on some links below")
                .contains("<a href=\"page1.html\">Static Page 1</a>")
                .contains("<a href=\"page2.html\">Static Page 2</a>")
                .contains("dynamic/mytempl.templ")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }
}

http = new HTTPBuilder( 'http://localhost:8080/page1.html' )
http.request(GET,TEXT) { req ->
    response.success = { resp, reader ->
        assert resp.status == 200
        def data = IOUtils.toString(reader)
        assertThat(data).contains("<h1>Welcome to page1!</h1>")
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

http = new HTTPBuilder( 'http://localhost:8080/dynamic/mytempl.templ' )
http.request(GET,TEXT) { req ->
    response.success = { resp, reader ->
        assert resp.status == 200
        def data = IOUtils.toString(reader)
        println data
        assertThat(data).contains("Request path:/dynamic/mytempl.templ")
    }

    // called only for a 404 (not found) status code:
    response.'404' = { resp ->
        assert false
    }
}

return true

