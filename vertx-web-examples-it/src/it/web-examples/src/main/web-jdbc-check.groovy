import com.jayway.restassured.http.ContentType

import static com.jayway.restassured.RestAssured.*
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItems

println get("/products").asString()
get("/products").then().body("NAME", hasItems("Egg Whisk", "Tea Cosy", "Spatula"))
get("/products/0").then().body("NAME", equalTo("Egg Whisk"))
get("/products/1").then().body("NAME", equalTo("Tea Cosy"))

given()
        .body("{\"name\":\"Whisky on the rocks\",\"price\":6,\"weight\":10}").contentType(ContentType.JSON)
        .when().post("/products")
        .then().statusCode(200)

get("/products/3").then().body("NAME", equalTo("Whisky on the rocks"))

return true

