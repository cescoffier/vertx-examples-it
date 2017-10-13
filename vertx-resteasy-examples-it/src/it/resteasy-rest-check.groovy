import com.jayway.restassured.http.ContentType

import static com.jayway.restassured.RestAssured.*
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItems

Thread.sleep(5000)
println get("/products").asString()
get("/products").then().body("name", hasItems("Egg Whisk", "Tea Cosy", "Spatula"))
get("/products/prod3568").then().body("name", equalTo("Egg Whisk"))
get("/products/prod7340").then().body("name", equalTo("Tea Cosy"))

given()
        .body("{\"name\":\"Whisky on the rocks\",\"price\":6,\"weight\":10}").contentType(ContentType.JSON)
        .when().put("/products/foo")
        .then().statusCode(200)

get("/products/foo").then().body("name", equalTo("Whisky on the rocks"))


return true

