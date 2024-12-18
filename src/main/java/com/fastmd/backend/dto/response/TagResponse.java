package com.fastmd.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TagResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}