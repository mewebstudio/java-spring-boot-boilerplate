package com.mewebstudio.javaspringbootboilerplate.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.controller.AccountController;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdatePasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.AppExceptionHandler;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("mvcIT")
@ActiveProfiles(value = "mvcIT")
@WebMvcTest(AccountController.class)
@DisplayName("MVC Integration Tests for AccountController")
class AccountControllerMvcIT {
    @Autowired
    private AccountController accountController;

    @Autowired
    private AppExceptionHandler appExceptionHandler;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageSourceService messageSourceService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private final User user = Instancio.create(User.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
            .setControllerAdvice(appExceptionHandler)
            .build();
    }

    @Nested
    @DisplayName("Test class for me scenarios")
    class MeTest {
        @Test
        @DisplayName("Given when me then assert body")
        void given_whenMe_thenAssertBody() throws Exception {
            // Given
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/account/me");
            when(userService.getUser()).thenReturn(user);
            // When
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isOk());
            perform.andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(user.getId().toString())));
            perform.andExpect(MockMvcResultMatchers.jsonPath("$.email", equalTo(user.getEmail())));
            perform.andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(user.getName())));
            perform.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", equalTo(user.getLastName())));
        }

        @Test
        @DisplayName("Given when me then should throw exception")
        void given_whenMe_thenShouldThrowException() throws Exception {
            // Given
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/account/me");
            when(userService.getUser()).thenThrow(new BadRequestException());
            // When
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isBadRequest());
            perform.andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo("Bad request!")));
        }
    }

    @Nested
    @DisplayName("Test class for update password scenarios")
    class PasswordTest {
        private final UpdatePasswordRequest request = Instancio.create(UpdatePasswordRequest.class);

        @BeforeEach
        void setUp() {
            request.setOldPassword("oldPassword123.");
            request.setPassword("newPassword123.");
            request.setPasswordConfirm("newPassword123.");
        }

        @Test
        @DisplayName("Given when update password then assert body")
        void given_whenPassword_thenAssertBody() throws Exception {
            // Given
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
            when(userService.updatePassword(request)).thenReturn(user);
            // When
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isOk());
        }

        @Test
        @DisplayName("Given when update password then should throw exception")
        void given_whenPassword_thenShouldThrowException() throws Exception {
            // Given
            request.setOldPassword("wrongPassword");
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/account/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
            when(userService.updatePassword(request)).thenThrow(new BindException(request, "request"));
            // When
            ResultActions perform = mockMvc.perform(requestBuilder);
            // Then
            perform.andExpect(status().isUnprocessableEntity());
        }
    }
}
