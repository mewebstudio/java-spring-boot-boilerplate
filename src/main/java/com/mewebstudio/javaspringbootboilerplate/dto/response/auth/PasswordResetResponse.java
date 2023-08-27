package com.mewebstudio.javaspringbootboilerplate.dto.response.auth;

import com.mewebstudio.javaspringbootboilerplate.dto.response.AbstractBaseResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UserResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.PasswordResetToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
public class PasswordResetResponse extends AbstractBaseResponse {
    @Schema(
        name = "id",
        description = "UUID",
        type = "String",
        example = "120b2663-412a-4a98-8c7b-19115fd8a0b0"
    )
    private String id;

    @Schema(
        name = "token",
        description = "Token",
        type = "String",
        example = "KQJGpJ..."
    )
    private String token;

    @Schema(
        name = "user",
        description = "User",
        type = "UserResponse"
    )
    private UserResponse user;

    @Schema(
        name = "expirationDate",
        description = "Expiration date",
        type = "String",
        example = "2023-09-15 12:34:46.7"
    )
    private Date expirationDate;

    @Schema(
        name = "createdAt",
        description = "Date time field of user creation",
        type = "LocalDateTime",
        example = "2022-09-29T22:37:31"
    )
    private LocalDateTime createdAt;

    @Schema(
        name = "updatedAt",
        type = "LocalDateTime",
        description = "Date time field of user update",
        example = "2022-09-29T22:37:31"
    )
    private LocalDateTime updatedAt;

    /**
     * Convert PasswordResetToken to PasswordResetResponse
     *
     * @param passwordResetToken PasswordResetToken
     * @return PasswordResetResponse
     */
    public static PasswordResetResponse convert(PasswordResetToken passwordResetToken) {
        return PasswordResetResponse.builder()
            .id(passwordResetToken.getId().toString())
            .token(passwordResetToken.getToken())
            .user(UserResponse.convert(passwordResetToken.getUser()))
            .expirationDate(passwordResetToken.getExpirationDate())
            .createdAt(passwordResetToken.getCreatedAt())
            .updatedAt(passwordResetToken.getUpdatedAt())
            .build();
    }
}
