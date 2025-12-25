package com.capstone.huddle.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserFilterDto {

    private String searchTerm;
//    private String role;
//    private Boolean isActive;
//
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//    private LocalDateTime registeredAfter;
//
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//    private LocalDateTime registeredBefore;
}
