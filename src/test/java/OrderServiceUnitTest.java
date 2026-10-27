import org.example.OrderCreateRequest;
import org.example.OrderResponse;
import org.example.dto.UserDto;
import org.example.servise.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderServiceUnitTest {

    @Test
    void getOrderById_shouldReturnStoredOrder() {
        RestClient restClient = mock(RestClient.class);
        OrderService service = new OrderService(restClient);

        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headerSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UserDto.class)).thenReturn(new UserDto(1L, "Alice", "a@mail.com"));

        OrderResponse created = service.createOrder(new OrderCreateRequest(1L, "Book"));

        assertThat(created.orderId()).isEqualTo(1L);
        assertThat(service.getOrderById(created.orderId())).isEqualTo(created);
        assertThat(service.getOrderById(42L)).isNull();
    }
}
