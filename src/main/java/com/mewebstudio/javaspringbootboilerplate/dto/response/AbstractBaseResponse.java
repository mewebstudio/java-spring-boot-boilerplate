package com.mewebstudio.javaspringbootboilerplate.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder
public abstract class AbstractBaseResponse {
    protected AbstractBaseResponse() {
    }
}
