package com.capstone.huddle.users.service;

import com.capstone.huddle.common.specification.GenericSpecificationBuilder;
import com.capstone.huddle.users.dto.UserDto;
import com.capstone.huddle.users.dto.UserFilterDto;
import com.capstone.huddle.users.dto.request.UserRequest;
import com.capstone.huddle.users.dto.response.UserResponse;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // In UserService.java
    public UserResponse<UserEntity> signup(UserRequest userRequest) {
        log.info("Creating new user {}", userRequest.getUsername());

        UserEntity user = new UserEntity();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        // Set roles - FIX THE SYNTAX ERROR HERE
        if (userRequest.getRoles() == null || userRequest.getRoles().isEmpty()) {
            user.setRoles(Set.of(UserEntity.Role.USER)); // Default role
        } else {
            user.setRoles(userRequest.getRoles());
        }

        UserEntity savedEntity = userRepository.save(user);

        return UserResponse.<UserEntity>builder()
                .success(true)
                .message("User created successfully")
                .data(savedEntity)
                .build();
    }


    public UserEntity login(String username, String password) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public Page<UserDto> searchUsers(UserFilterDto filter, Pageable pageable) {
        GenericSpecificationBuilder<UserEntity> builder = new GenericSpecificationBuilder<>();

        Specification<UserEntity> spec = Specification.where(null);

        // Text search across multiple fields
        if (filter.getSearchTerm() != null) {
            spec = spec.and(builder.withTextSearch(
                    filter.getSearchTerm(),
                    "username", "email", "firstName", "lastName"
            ));
        }

//        // Role filter
//        if (filter.getRole() != null) {
//            spec = spec.and(builder.withEquals(filter.getRole(), "role"));
//        }
//
//        // Active status filter
//        if (filter.getIsActive() != null) {
//            spec = spec.and(builder.withBoolean(filter.getIsActive(), "isActive"));
//        }
//
//        // Date range filter
//        if (filter.getRegisteredAfter() != null && filter.getRegisteredBefore() != null) {
//            spec = spec.and(builder.withDateRange(
//                    filter.getRegisteredAfter(),
//                    filter.getRegisteredBefore(),
//                    "createdAt"
//            ));
//        }

        return userRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    private UserDto mapToDto(UserEntity user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
