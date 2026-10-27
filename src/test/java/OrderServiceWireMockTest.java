import com.github.tomakehurst.wiremock.WireMockServer;
import org.example.OrderApplication;
import org.example.OrderCreateRequest;
import org.example.dto.UserDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.ResourceAccessException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = OrderApplication.class)
class OrderServiceWireMockTest {

    static WireMockServer wireMock;

    @BeforeAll
    static void start() { wireMock = new WireMockServer(0); wireMock.start(); }
    @AfterAll  static void stop()  { wireMock.stop(); }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry reg) {
        reg.add("app.user-service.base-url",
                () -> "http://localhost:" + wireMock.port());
    }

    @Autowired org.example.servise.OrderService orderService;

    @Test
    void createOrder_overRealHttp_ok() {
        wireMock.stubFor(get(urlPathEqualTo("/users/7"))
                .willReturn(okJson("""
                    {"id":7,"name":"Bob","email":"bob@mail.ru"}
                    """)));

        var resp = orderService.createOrder(new OrderCreateRequest(7L, "pen"));

        assertThat(resp.user().name()).isEqualTo("Bob");
        wireMock.verify(getRequestedFor(urlEqualTo("/users/7")));
    }

    @Test
    void createOrder_userNotFound_shouldFail() {
        wireMock.stubFor(get(urlPathEqualTo("/users/404"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() ->
                orderService.createOrder(new OrderCreateRequest(404L, "pen")))
                .isInstanceOf(org.springframework.web.client.HttpClientErrorException.NotFound.class);
    }

    @Test
    void createOrder_timeout_isBoundedByReadTimeout() {
        wireMock.stubFor(get(urlPathEqualTo("/users/8"))
                .willReturn(okJson("{\"id\":8}").withFixedDelay(3000)));

        long t0 = System.currentTimeMillis();
        assertThatThrownBy(() ->
                orderService.createOrder(new OrderCreateRequest(8L, "pen")))
                .isInstanceOf(ResourceAccessException.class);
        long elapsed = System.currentTimeMillis() - t0;
        assertThat(elapsed).isLessThan(2900);
    }
}