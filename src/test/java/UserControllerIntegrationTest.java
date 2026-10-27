import org.example.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = org.example.UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    @Test
    void createAndGetUser_viaHttp() {
        var user = new UserDto(5L, "Carol", "c@mail.com");

        ResponseEntity<UserDto> created = rest.postForEntity(
                "http://localhost:" + port + "/users", user, UserDto.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isEqualTo(user);

        ResponseEntity<UserDto> got = rest.getForEntity(
                "http://localhost:" + port + "/users/5", UserDto.class);
        assertThat(got.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(got.getBody().name()).isEqualTo("Carol");
    }

    @Test
    void getUnknownUser_shouldReturn404() {
        ResponseEntity<String> resp = rest.getForEntity(
                "http://localhost:" + port + "/users/999", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}