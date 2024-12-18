package com.fastmd.backend.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkdownFileRequest {
    @NotBlank(message = "Tên file không được để trống")
    @Size(max = 255, message = "Tên file không được quá 255 ký tự")
    private String title;

    private String content;
    private Set<Long> tagIds;
}