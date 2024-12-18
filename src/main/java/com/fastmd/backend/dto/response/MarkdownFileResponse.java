package com.fastmd.backend.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class MarkdownFileResponse {
    private String id;
    private String name;
    private String content;
    private Set<TagResponse> tags;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}