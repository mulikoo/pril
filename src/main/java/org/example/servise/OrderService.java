package org.example.servise;

import org.example.OrderCreateRequest;
import org.example.OrderResponse;
import org.example.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final Map<Long, OrderResponse> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final RestClient userRestClient;

    public OrderService(RestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    public OrderResponse createOrder(OrderCreateRequest request) {
        UserDto user = userRestClient.get()
                .uri("/users/{id}", request.userId())
                .retrieve()
                .body(UserDto.class);

        Long orderId = idGenerator.getAndIncrement();
        OrderResponse response = new OrderResponse(orderId, request.product(), user);
        orders.put(orderId, response);
        return response;
    }

    public OrderResponse getOrderById(Long id) {
        return orders.get(id);
    }
}