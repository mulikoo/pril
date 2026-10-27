import org.example.OrderApplication;
import org.example.OrderCreateRequest;
import org.example.OrderResponse;
import org.example.dto.UserDto;
import org.example.servise.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = OrderApplication.class)
class OrderServiceHybridTest {

    @Autowired
    OrderService orderService;

    @MockBean RestClient userRestClient;

    @Test
    void createOrder_persistsOrderWithRealUserData() {
        var requestSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        var uriSpec     = org.mockito.Mockito.mock(RestClient.RequestHeadersSpec.class);
        var retrieve    = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);

        given(userRestClient.get()).willReturn(requestSpec);
        given(requestSpec.uri(anyString(), org.mockito.ArgumentMatchers.<Object>any()))
                .willReturn(uriSpec);
        given(uriSpec.retrieve()).willReturn(retrieve);
        given(retrieve.body(UserDto.class))
                .willReturn(new UserDto(1L, "Alice", "a@mail.ru"));

        OrderResponse resp = orderService.createOrder(new OrderCreateRequest(1L, "book"));

        assertThat(resp.orderId()).isNotNull();
        assertThat(resp.product()).isEqualTo("book");
        assertThat(resp.user().name()).isEqualTo("Alice");
        assertThat(orderService.getOrderById(resp.orderId())).isEqualTo(resp);
    }

    @Test
    void createOrder_propagatesTransportError() {
        var requestSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        given(userRestClient.get()).willReturn(requestSpec);
        given(requestSpec.uri(anyString(), org.mockito.ArgumentMatchers.<Object>any()))
                .willThrow(new ResourceAccessException("timeout"));

        assertThatThrownBy(() ->
                orderService.createOrder(new OrderCreateRequest(1L, "book")))
                .isInstanceOf(ResourceAccessException.class);
    }
}