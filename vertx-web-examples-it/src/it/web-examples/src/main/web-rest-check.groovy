import com.jayway.restassured.http.ContentType

import static com.jayway.restassured.RestAssured.*
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItems

get("/products").then().body("id", hasItems("prod3568", "prod7340", "prod8643"))
get("/products/prod3568").then().body("name", equalTo("Egg Whisk"))
get("/products/prod7340").then().body("name", equalTo("Tea Cosy"))

given()
        .body("{\"name\":\"Whisky on the rocks\",\"price\":6,\"weight\":10}").contentType(ContentType.JSON)
        .when().put("/products/prod1234")
        .then().statusCode(200)

get("/products/prod1234").then().body("name", equalTo("Whisky on the rocks"))

return true

