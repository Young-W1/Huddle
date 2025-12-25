package com.capstone.huddle.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileUpdateRequest {

    private String bio;
    private String profilePicture;
    private String location;
    private String website;
}
