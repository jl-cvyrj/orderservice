package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private static final Logger log = LogManager.getLogger(UserClient.class);

    private final RestTemplate restTemplate;

    @Value("${USER_SERVICE_URL}")
    private String userServiceUrl;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public UserDto getUserById(Long userId) {

        log.info("Sending HTTP-request to UserService for userId: {}", userId);

        String url = userServiceUrl + "/" + userId;
        return restTemplate.getForObject(url, UserDto.class);
    }

    public UserDto getUserByIdFallback(Long userId, Throwable throwable) {

        log.error("Circuit Breaker triggered! User Service is unavailable. Reason: {}", throwable.getMessage());

        UserDto fallbackUser = new UserDto();
        fallbackUser.setId(userId);
        fallbackUser.setName("Unknown");
        fallbackUser.setSurname("User");
        fallbackUser.setActive(false);
        return fallbackUser;
    }
}
