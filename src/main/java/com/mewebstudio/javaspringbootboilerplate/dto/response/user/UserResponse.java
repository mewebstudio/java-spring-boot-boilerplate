package com.mewebstudio.javaspringbootboilerplate.dto.response.user;

import com.mewebstudio.javaspringbootboilerplate.dto.response.AbstractBaseResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class UserResponse extends AbstractBaseResponse {
    @Schema(
        name = "id",
        description = "UUID",
        type = "String",
        example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    private String id;

    @Schema(
        name = "email",
        description = "E-mail of the user",
        type = "String",
        example = "mail@example.com"
    )
    private String email;

    @Schema(
        name = "name",
        description = "Name of the user",
        type = "String",
        example = "John"
    )
    private String name;

    @Schema(
        name = "lastName",
        description = "Lastname of the user",
        type = "String",
        example = "DOE"
    )
    private String lastName;

    @Schema(
        name = "roles",
        description = "role of the user",
        type = "List",
        example = "[\"USER\"]"
    )
    private List<String> roles;

    @Schema(
        name = "emailVerifiedAt",
        description = "Date time field of user e-mail verified",
        type = "LocalDateTime",
        example = "2022-09-29T22:37:31"
    )
    private LocalDateTime emailVerifiedAt;

    @Schema(
        name = "blockedAt",
        description = "Date time field of user blocked",
        type = "LocalDateTime",
        example = "2022-09-29T22:37:31"
    )
    private LocalDateTime blockedAt;

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
     * Convert User to UserResponse
     * @param user User
     * @return UserResponse
     */
    public static UserResponse convert(User user) {
        return UserResponse.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .name(user.getName())
            .lastName(user.getLastName())
            .roles(user.getRoles().stream().map(role -> role.getName().name()).toList())
            .emailVerifiedAt(user.getEmailVerifiedAt())
            .blockedAt(user.getBlockedAt())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
