package com.mewebstudio.javaspringbootboilerplate.dto.request.user;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.MinListSize;
import com.mewebstudio.javaspringbootboilerplate.dto.annotation.ValueOfEnum;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateUserRequest extends AbstractBaseCreateUserRequest {
    @NotEmpty(message = "{not_blank}")
    @MinListSize(min = 1, message = "{min_list_size}")
    @Schema(
        name = "roles",
        description = "Roles of the user",
        type = "List<String>",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = {"ADMIN", "USER"},
        example = "[\"USER\"]"
    )
    private List<@ValueOfEnum(enumClass = Constants.RoleEnum.class) String> roles;

    @Schema(
        name = "isEmailVerified",
        description = "Is the user's email verified",
        type = "Boolean",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "true"
    )
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Schema(
        name = "isBlocked",
        description = "Is the user blocked",
        type = "Boolean",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "false"
    )
    @Builder.Default
    private Boolean isBlocked = false;
}
