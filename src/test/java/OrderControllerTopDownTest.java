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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ContextConfiguration(classes = OrderApplication.class)
public class OrderControllerTopDownTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreateOrderTopLevelSuccess() throws Exception {
        UserDto mockUser = new UserDto(100L, "Иван Иванов", "ivan@test.com");
        OrderResponse mockResponse = new OrderResponse(1L, "Ноутбук", mockUser);

        when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 100, \"product\": \"Ноутбук\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product").value("Ноутбук"))
                .andExpect(jsonPath("$.user.name").value("Иван Иванов"));
    }
}