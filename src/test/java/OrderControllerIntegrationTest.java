import org.example.OrderCreateRequest;
import org.example.OrderResponse;
import org.example.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = org.example.OrderApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;
    @MockBean RestClient userRestClient;   // подменяем внешний клиент

    @Test
    void createOrder_shouldReturn201() {
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headerSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec respSpec = mock(RestClient.ResponseSpec.class);

        when(userRestClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(respSpec);
        when(respSpec.body(UserDto.class)).thenReturn(new UserDto(1L, "Dave", "d@mail.com"));

        ResponseEntity<OrderResponse> resp = rest.postForEntity(
                "http://localhost:" + port + "/orders",
                new OrderCreateRequest(1L, "Laptop"),
                OrderResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody().product()).isEqualTo("Laptop");
        assertThat(resp.getBody().user().name()).isEqualTo("Dave");
    }

    @Test
    void createOrder_whenUserServiceFails_shouldReturn503() {
        when(userRestClient.get()).thenThrow(new RuntimeException("down"));

        ResponseEntity<String> resp = rest.postForEntity(
                "http://localhost:" + port + "/orders",
                new OrderCreateRequest(1L, "X"), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}