import org.example.dto.UserDto;
import org.example.servise.OrderService;
import org.example.servise.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = org.example.UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceUserIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Test
    void orderService_shouldFetchUserFromRealUserService() {
        userService.createUser(new UserDto(10L, "Bob", "bob@mail.com"));

        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        OrderService orderService = new OrderService(client);
        var order = orderService.createOrder(new org.example.OrderCreateRequest(10L, "Pen"));

        assertThat(order.user().name()).isEqualTo("Bob");
        assertThat(order.product()).isEqualTo("Pen");
    }
}