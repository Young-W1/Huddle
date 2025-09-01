package com.capstone.huddle.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {

    private String bio;
    private String profilePicture;
    private String location;
    private String website;
}
