package com.studyshare.client.service;

import com.studyshare.common.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public void cacheUserData(UserDTO user) {
        cache.put("currentUser", user);
    }

    public UserDTO getCachedUser() {
        return (UserDTO) cache.get("currentUser");
    }
}
