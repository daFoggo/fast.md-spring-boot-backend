package com.fastmd.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagRequest {
    @NotBlank(message = "Tên tag không được để trống")
    @Size(max = 50, message = "Tên tag không được quá 50 ký tự")
    private String name;

    @Size(max = 255, message = "Mô tả tag không được quá 255 ký tự")
    private String description;
}
