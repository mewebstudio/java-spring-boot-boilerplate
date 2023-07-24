package com.mewebstudio.javaspringbootboilerplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SuccessResponse extends AbstractBaseResponse {
    @Schema(
        name = "message",
        type = "Integer",
        description = "Response message field",
        example = "Success!"
    )
    private String message;
}
