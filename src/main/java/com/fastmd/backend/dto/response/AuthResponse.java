package com.fastmd.backend.dto.response;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class AuthResponse {
    private String token;
    private Optional<UserResponse> user;
}
