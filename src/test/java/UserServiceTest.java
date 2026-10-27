import org.example.dto.UserDto;
import org.example.servise.UserService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void createAndGetUser_shouldWork() {
        UserDto user = new UserDto(1L, "Alice", "alice@mail.com");

        UserDto created = userService.createUser(user);
        UserDto found = userService.getUserById(1L);

        assertThat(created).isEqualTo(user);
        assertThat(found).isEqualTo(user);
    }

    @Test
    void getUnknownUser_shouldReturnNull() {
        assertThat(userService.getUserById(999L)).isNull();
    }
}