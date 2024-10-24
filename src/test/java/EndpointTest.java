import dat.config.ApplicationConfig;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class EndpointTest {

    private static final String BASE_URI = "http://localhost:7070/travel";

    private static Javalin app;

    @BeforeAll
    public static void setup() {

        app=ApplicationConfig.startServer(7070);
        RestAssured.baseURI = BASE_URI;
    }

    @AfterAll
    public static void tearDown() {


        ApplicationConfig.stopServer(app);
    }

    @Test
    public void testLogin() {
        // Send POST request to login endpoint
        Response response = given()
                .contentType("application/json")
                .body("{ \"username\": \"admin\", \"password\": \"test123\" }")
                .when()
                .post("/auth/login/") // Use the base URI here
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo("admin"))
                .extract()
                .response();


    }

    @Test
    public void testRegister() {
        Response response = given()
                .contentType("application/json")
                .body("{ \"username\": \"adminTest\", \"password\": \"test123\" }")
                .when()
                .post("/auth/register/")
                .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("username", equalTo("adminTest"))
                .extract()
                .response();


    }

    @Test
    public void testAdminAccessWithUser() {
        Response loginResponse = given()
                .contentType("application/json")
                .body("{ \"username\": \"user\", \"password\": \"test123\" }")
                .when()
                .post("/auth/login/")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        String token = loginResponse.jsonPath().getString("token");

        ValidatableResponse validatableResponse = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/reviews/6")
                .then();

        validatableResponse.log().all();

        validatableResponse
                .statusCode(401);
    }
}

