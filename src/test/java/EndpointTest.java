import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.security.entities.Role;
import dat.security.entities.User;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class EndpointTest {

    private static final String BASE_URI = "http://localhost:7070/travel";

    private static Javalin app;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    public static void setup() {
        HibernateConfig.setTest(true);
        app = ApplicationConfig.startServer(7070);
        RestAssured.baseURI = BASE_URI;


        entityManagerFactory = HibernateConfig.getEntityManagerFactoryForTest();
    }

    @BeforeEach
    public void setupEach() {

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();


        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");


        entityManager.persist(adminRole);
        entityManager.persist(userRole);


        User adminUser = new User("admin", "1234");
        adminUser.addRole(adminRole);

        User regularUser = new User("user", "1234");
        regularUser.addRole(userRole);


        entityManager.persist(adminUser);
        entityManager.persist(regularUser);

        entityManager.getTransaction().commit();
    }

    @AfterEach
    public void tearDownEach() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.getTransaction().begin();
            entityManager.createQuery("DELETE FROM User").executeUpdate();
            entityManager.createQuery("DELETE FROM Role").executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }

    @AfterAll
    public static void tearDown() {
        ApplicationConfig.stopServer(app);
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    public void testLogin() {
        Response response = given()
                .contentType("application/json")
                .body("{ \"username\": \"admin\", \"password\": \"1234\" }")
                .when()
                .post("/auth/login/")
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
                .body("{ \"username\": \"adminTest\", \"password\": \"1234\" }")
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
                .body("{ \"username\": \"user\", \"password\": \"1234\" }")
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

