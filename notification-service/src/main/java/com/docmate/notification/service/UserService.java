package com.docmate.notification.service;

import com.docmate.common.entity.User;
import com.docmate.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserService {
    
    // This service would typically call the user-service to get user details
    // For now, we'll create a mock implementation
    
    public User findById(UUID userId) {
        log.info("Fetching user with ID: {}", userId);
        
        // In a real microservices architecture, this would make an HTTP call to user-service
        // For now, we'll create a mock user object
        User user = User.builder()
                .email("user@example.com")
                .phone("+1234567890")
                .fullName("John Doe")
                .build();
        
        // Set the id manually since it's inherited from BaseEntity
        user.setId(userId);
        
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "User not found", 404);
        }
        
        return user;
    }
}
