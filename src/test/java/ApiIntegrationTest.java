import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.OrderCreateRequest;
import org.example.dto.UserDto;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiIntegrationTest {

    @BeforeAll
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    @DisplayName("Сценарий 1: Успех запроса данных пользователя сервисом заказов и время отклика")
    public void testSuccessfulOrderCreationAndResponseTime() {
        UserDto newUser = new UserDto(100L, "Иван Иванов", "ivan@test.com");

        given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("http://localhost:8081/users")
                .then()
                .statusCode(201);

        OrderCreateRequest orderRequest = new OrderCreateRequest(100L, "Ноутбук");

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("http://localhost:8082/orders")
                .then()
                .statusCode(201)
                .body("user.id", equalTo(100))
                .body("user.name", equalTo("Иван Иванов"))
                .body("product", equalTo("Ноутбук"))
                .time(lessThan(1000L));
    }

    @Test
    @Order(2)
    @DisplayName("Сценарий 2: Обработка ошибок при отсутствии пользователя или недоступности сервиса")
    public void testUserServiceUnavailableOrUserNotFound() {
        OrderCreateRequest orderRequest = new OrderCreateRequest(999L, "Телефон");

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("http://localhost:8082/orders")
                .then()
                .statusCode(503)
                .body("error", notNullValue());
    }
}