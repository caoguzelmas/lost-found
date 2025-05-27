package com.caoguzelmas.lost_found.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MockUserService {

    private final Map<Long, String> userNames = new HashMap<>();

    public MockUserService() {
        userNames.put(1001L, "John Doe");
        userNames.put(1002L, "Jane Doe");
        userNames.put(1003L, "Jack Doe");
    }

    public String getUserName(final Long userId) {
        return userNames.getOrDefault(userId, "Unknown user ID: " + userId);
    }
}
