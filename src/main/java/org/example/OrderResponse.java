package org.example;

import org.example.dto.UserDto;

public record OrderResponse(Long orderId, String product, UserDto user) {

}