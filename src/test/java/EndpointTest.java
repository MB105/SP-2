import dat.config.HibernateConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import dat.config.HibernateConfig;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;


public class EndpointTest {


    // Base URI for the API
    static String baseURI;

    static EntityManagerFactory emfTest;

    static EntityManager em;


    @BeforeAll
    public static void setup() {
        // Set up base URI
        baseURI = "http://localhost:7070/travel";
        RestAssured.baseURI = baseURI;

        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        SecurityDAO securityDAO = new SecurityDAO(emfTest);


    }

    @BeforeEach
    public void setUp() {


        em = emfTest.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    public void tearDown() {
        em.getTransaction().commit(); // Commit the transaction
        em.close(); // Close the EntityManager
    }


    @Test
    public void testLogin() {


        // Send POST request to login endpoint
        Response response = given()
                .contentType("application/json")
                .body("{ \"username\": \"admin\", \"password\": \"test123\" }")
                .when()
                .post("/auth/login")
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
                .body("{ \"username\": \"admin10\", \"password\": \"test123\" }")
                .when()
                .post("/auth/register/")
                .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("username", equalTo("admin10"))
                .extract()
                .response();


    }

    @Test
    public void testAdminAccessWithUser() {


        Response loginResponse = given()
                .contentType("application/json")
                .body("{ \"username\": \"user\", \"password\": \"test123\" }")
                .when()
                .post("/auth/login")
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
