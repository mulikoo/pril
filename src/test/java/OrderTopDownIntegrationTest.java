import io.restassured.http.ContentType;
import org.example.OrderCreateRequest;
import org.example.dto.UserDto;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OrderTopDownIntegrationTest {

    @Test
    public void testFullTopDownIntegrationWithUserService() {
        UserDto newUser = new UserDto(200L, "Петр Петров", "petr@test.com");
        given().contentType(ContentType.JSON).body(newUser)
                .when().post("http://localhost:8081/users")
                .then().statusCode(201);

        OrderCreateRequest orderRequest = new OrderCreateRequest(200L, "Смартфон");
        given().contentType(ContentType.JSON).body(orderRequest)
                .when().post("http://localhost:8082/orders")
                .then().statusCode(201)
                .body("user.id", equalTo(200))
                .body("product", equalTo("Смартфон"));
    }
}