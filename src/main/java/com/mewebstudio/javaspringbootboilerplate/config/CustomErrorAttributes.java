package com.mewebstudio.javaspringbootboilerplate.config;

import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public final Map<String, Object> getErrorAttributes(final WebRequest webRequest,
                                                        final ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        Object errorMessage = webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST);

        Map<String, Object> map = new HashMap<>();
        map.put("status", errorAttributes.get("status"));
        if (errorMessage != null) {
            String message = (String) (Objects.nonNull(errorAttributes.get("message")) ? errorAttributes.get("message")
                : "Server error");
            map.put("message", message);
        }
        if (Objects.nonNull(errorAttributes.get("items"))) {
            map.put("items", errorAttributes.get("items"));
        }

        return map;
    }
}
