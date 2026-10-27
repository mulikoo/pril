import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.OrderApplication;
import org.example.OrderCreateRequest;
import org.example.OrderResponse;
import org.example.controller.OrderController;
import org.example.dto.UserDto;
import org.example.servise.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = OrderApplication.class)
class OrderControllerWebMvcTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean OrderService orderService;

    @Test
    void createOrder_returns201_whenServiceSucceeds() throws Exception {
        var user = new UserDto(1L, "Alice", "a@mail.ru");
        given(orderService.createOrder(any()))
                .willReturn(new OrderResponse(10L, "book", user));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OrderCreateRequest(1L, "book"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.user.name").value("Alice"));
    }

    @Test
    void createOrder_returns503_whenDownstreamFails() throws Exception {
        given(orderService.createOrder(any()))
                .willThrow(new RuntimeException("user service down"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"product\":\"book\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getOrder_returns404_whenNotFound() throws Exception {
        given(orderService.getOrderById(99L)).willReturn(null);

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isNotFound());
    }
}