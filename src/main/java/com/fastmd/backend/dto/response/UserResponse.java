
package com.fastmd.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
