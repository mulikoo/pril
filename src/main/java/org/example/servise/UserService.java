package org.example.servise;

import org.example.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final Map<Long, UserDto> users = new ConcurrentHashMap<>();

    public UserDto createUser(UserDto user) {
        users.put(user.id(), user);
        return user;
    }

    public UserDto getUserById(Long id) {
        return users.get(id);
    }
}