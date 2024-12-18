package com.fastmd.backend.exception;

import com.fastmd.backend.dto.response.UserResponse;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthException {
    public AuthException(String jwt, UserResponse userResponse) {
    }
    private String message;
    private Object error;
}
