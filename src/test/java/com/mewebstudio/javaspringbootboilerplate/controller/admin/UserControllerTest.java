package com.mewebstudio.javaspringbootboilerplate.controller.admin;

import com.mewebstudio.javaspringbootboilerplate.dto.request.user.CreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UserResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UsersPaginationResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.PaginationCriteria;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.UserCriteria;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for Admin - UserController")
class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void before() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private final User user = Instancio.create(User.class);

    @Nested
    @DisplayName("Test class for users list scenarios")
    public class ListTest {
        private final List<String> roles = List.of(Constants.RoleEnum.USER.toString());

        @Test
        @DisplayName("Happy path")
        void given_whenList_thenAssertBody() {
            // Given
            Page<User> page = new PageImpl<>(List.of(user));
            when(userService.findAll(any(UserCriteria.class), any(PaginationCriteria.class)))
                .thenReturn(page);
            // When
            ResponseEntity<UsersPaginationResponse> response = userController.list(roles, null,
                null, null, null, null, null, null, null, null);
            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getPage());
            assertEquals(1, response.getBody().getPages());
            assertEquals(1, response.getBody().getTotal());
            assertEquals(user.getId().toString(), response.getBody().getItems().get(0).getId());
            assertEquals(user.getEmail(), response.getBody().getItems().get(0).getEmail());
            assertEquals(user.getName(), response.getBody().getItems().get(0).getName());
            assertEquals(user.getLastName(), response.getBody().getItems().get(0).getLastName());
        }

        @Test
        @DisplayName("When invalid sortBy parameter then throw BadRequestException")
        void given_whenInvalidSortColumn_thenThrowBadRequestException() {
            // When
            Executable executable = () -> userController.list(roles, null,
                null, null, null, null, null, null,
                "invalid", null);
            // Then
            assertThrows(BadRequestException.class, executable);
            assertEquals(messageSourceService.get("invalid_sort_column"),
                assertThrows(BadRequestException.class, executable).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for user create scenarios")
    public class CreateTest {
        @Test
        @DisplayName("Happy path")
        void given_whenCreate_thenAssertBody() throws BindException {
            // Given
            CreateUserRequest request = Instancio.create(CreateUserRequest.class);
            when(userService.create(request)).thenReturn(user);
            // When
            ResponseEntity<URI> response = userController.create(request);
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertNotNull(response.getHeaders());
            assertNotNull(response.getHeaders().getLocation());
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        @DisplayName("When invalid body parameter then throw BindException")
        void given_whenInvalidBody_thenThrowBindException() throws BindException {
            // Given
            CreateUserRequest request = Instancio.create(CreateUserRequest.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
            when(userService.create(request)).thenThrow(new BindException(bindingResult));
            // When
            Executable executable = () -> userController.create(request);
            // Then
            assertThrows(BindException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for user show scenarios")
    public class ShowTest {
        @Test
        @DisplayName("Happy path")
        void given_whenShow_thenAssertBody() {
            // Given
            when(userService.findById(user.getId().toString())).thenReturn(user);
            // When
            ResponseEntity<UserResponse> response = userController.show(user.getId().toString());
            // Then
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(user.getId().toString(), response.getBody().getId());
            assertEquals(user.getEmail(), response.getBody().getEmail());
            assertEquals(user.getName(), response.getBody().getName());
        }

        @Test
        @DisplayName("When user not found then throw NotFoundException")
        void given_whenUserNotFound_thenThrowNotFoundException() {
            // Given
            when(userService.findById(user.getId().toString())).thenThrow(new NotFoundException());
            // When
            Executable executable = () -> userController.show(user.getId().toString());
            // Then
            assertThrows(NotFoundException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for user update scenarios")
    public class UpdateTest {
        @Test
        @DisplayName("Happy path")
        void given_whenUpdate_thenAssertBody() throws BindException {
            // Given
            UpdateUserRequest request = Instancio.create(UpdateUserRequest.class);
            when(userService.update(user.getId().toString(), request)).thenReturn(user);
            // When
            ResponseEntity<UserResponse> response = userController.update(user.getId().toString(), request);
            // Then
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(user.getId().toString(), response.getBody().getId());
            assertEquals(user.getEmail(), response.getBody().getEmail());
            assertEquals(user.getName(), response.getBody().getName());
        }

        @Test
        @DisplayName("When user not found then throw NotFoundException")
        void given_whenUserNotFound_thenThrowNotFoundException() throws BindException {
            // Given
            UpdateUserRequest request = Instancio.create(UpdateUserRequest.class);
            when(userService.update(user.getId().toString(), request)).thenThrow(new NotFoundException());
            // When
            Executable executable = () -> userController.update(user.getId().toString(), request);
            // Then
            assertThrows(NotFoundException.class, executable);
        }

        @Test
        @DisplayName("When invalid body parameter then throw BindException")
        void given_whenInvalidBody_thenThrowBindException() throws BindException {
            // Given
            UpdateUserRequest request = Instancio.create(UpdateUserRequest.class);
            BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
            when(userService.update(user.getId().toString(), request)).thenThrow(new BindException(bindingResult));
            // When
            Executable executable = () -> userController.update(user.getId().toString(), request);
            // Then
            assertThrows(BindException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for user delete scenarios")
    public class DeleteTest {
        @Test
        @DisplayName("Happy path")
        void given_whenDelete_thenAssertBody() {
            // When
            ResponseEntity<Void> response = userController.delete(user.getId().toString());
            // Then
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("When user not found then throw NotFoundException")
        void given_whenUserNotFound_thenThrowNotFoundException() {
            // Given
            doThrow(new NotFoundException()).when(userService).delete(user.getId().toString());
            // When
            Executable executable = () -> userController.delete(user.getId().toString());
            // Then
            assertThrows(NotFoundException.class, executable);
        }
    }
}
