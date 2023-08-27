package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.RegisterRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.ResetPasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.CreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdatePasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.PasswordResetToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.UserFilterSpecification;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.PaginationCriteria;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.UserCriteria;
import com.mewebstudio.javaspringbootboilerplate.event.UserEmailVerificationSendEvent;
import com.mewebstudio.javaspringbootboilerplate.event.UserPasswordResetSendEvent;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.UserRepository;
import com.mewebstudio.javaspringbootboilerplate.security.JwtUserDetails;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserService")
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private EmailVerificationTokenService emailVerificationTokenService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    private final User user = Instancio.create(User.class);

    private final JwtUserDetails jwtUserDetails = JwtUserDetails.create(user);

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(jwtUserDetails);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
    }

    @Nested
    @DisplayName("Test class for getUser scenarios")
    class GetUserTest {
        @Test
        @DisplayName("Happy path")
        void given_whenGetUser_thenAssertBody() {
            // Given
            when(userRepository.findById(UUID.fromString(jwtUserDetails.getId()))).thenReturn(Optional.of(user));
            // When
            User result = userService.getUser();
            // Then
            assertNotNull(result);
            assertEquals(user, result);
        }

        @Test
        @DisplayName("When not authenticated")
        void given_whenGetUserNotAuthenticated_thenShouldThrowBadCredentialsException() {
            // Given
            when(authentication.isAuthenticated()).thenReturn(false);
            // When
            Executable executable = () -> userService.getUser();
            // Then
            assertThrows(BadCredentialsException.class, executable);
        }

        @Test
        @DisplayName("When user not found")
        void given_whenGetUserNotFound_thenShouldThrowBadCredentialsException() {
            // Given
            when(userRepository.findById(UUID.fromString(jwtUserDetails.getId()))).thenReturn(Optional.empty());
            // When
            Executable executable = () -> userService.getUser();
            // Then
            assertThrows(BadCredentialsException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        @DisplayName("Happy path")
        void given_whenCount_thenAssertBody() {
            // Given
            when(userRepository.count()).thenReturn(1L);
            // When
            Long count = userService.count();
            // Then
            assertNotNull(count);
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findAll with pagination scenarios")
    class FindAllWithPaginationTest {
        @Test
        @DisplayName("Happy path")
        void given_whenFindAllWithPagination_thenAssertBody() {
            // Given
            Page<User> users = new PageImpl<>(List.of(user));
            when(userRepository.findAll(any(UserFilterSpecification.class), any(Pageable.class)))
                .thenReturn(users);
            // When
            Page<User> results = userService.findAll(Instancio.create(UserCriteria.class),
                PaginationCriteria.builder().page(1).size(1).build());
            // Then
            assertNotNull(results);
            assertEquals(1, results.getTotalElements());
            assertEquals(1, results.getTotalPages());
            assertEquals(1, results.getContent().size());
            assertEquals(user, results.getContent().get(0));
            assertEquals(user.getEmail(), results.getContent().get(0).getEmail());
            assertEquals(user.getName(), results.getContent().get(0).getName());
            assertEquals(user.getLastName(), results.getContent().get(0).getLastName());
            assertEquals(user.getRoles(), results.getContent().get(0).getRoles());
        }
    }

    @Nested
    @DisplayName("Test class for findById scenarios")
    class FindByIdTest {
        @Test
        @DisplayName("Happy path")
        void given_whenFindById_thenAssertBody() {
            // Given
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            // When
            User result = userService.findById(user.getId());
            // Then
            assertNotNull(result);
            assertEquals(user, result);
            assertEquals(user.getEmail(), result.getEmail());
            assertEquals(user.getName(), result.getName());
            assertEquals(user.getLastName(), result.getLastName());
            assertEquals(user.getRoles(), result.getRoles());
        }

        @Test
        @DisplayName("When user not found")
        void given_whenFindById_thenAssertNotFound() {
            // When
            Executable executable = () -> userService.findById(user.getId().toString());
            // Then
            assertThrows(NotFoundException.class, executable);
            assertEquals(messageSourceService.get("not_found_with_param",
                    new String[]{messageSourceService.get("user")}),
                assertThrows(NotFoundException.class, executable).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for findByEmail scenarios")
    class FindByEmailTest {
        @Test
        @DisplayName("Happy path")
        void given_whenFindByEmail_thenAssertBody() {
            // Given
            when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
            // When
            User result = userService.findByEmail(user.getEmail());
            // Then
            assertNotNull(result);
            assertEquals(user, result);
            assertEquals(user.getEmail(), result.getEmail());
            assertEquals(user.getName(), result.getName());
            assertEquals(user.getLastName(), result.getLastName());
            assertEquals(user.getRoles(), result.getRoles());
        }

        @Test
        @DisplayName("When user not found")
        void given_whenFindByEmail_thenAssertNotFound() {
            // When
            Executable executable = () -> userService.findByEmail(user.getEmail());
            // Then
            assertThrows(NotFoundException.class, executable);
            assertEquals(messageSourceService.get("not_found_with_param",
                    new String[]{messageSourceService.get("user")}),
                assertThrows(NotFoundException.class, executable).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for loadUserByEmail scenarios")
    class LoadUserByEmailTest {
        @Test
        @DisplayName("Happy path")
        void given_whenLoadUserByEmail_thenAssertBody() {
            // Given
            when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
            // When
            UserDetails userDetails = userService.loadUserByEmail(user.getEmail());
            // Then
            assertNotNull(userDetails);
            assertEquals(user.getEmail(), userDetails.getUsername());
        }

        @Test
        @DisplayName("When user not found")
        void given_whenLoadUserByEmail_thenAssertNotFound() {
            // When
            Executable executable = () -> userService.loadUserByEmail(user.getEmail());
            // Then
            assertThrows(NotFoundException.class, executable);
            assertEquals(messageSourceService.get("not_found_with_param",
                    new String[]{messageSourceService.get("user")}),
                assertThrows(NotFoundException.class, executable).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for loadUserById scenarios")
    class LoadUserByIdTest {
        @Test
        @DisplayName("Happy path")
        void given_whenLoadUserById_thenAssertBody() {
            // Given
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            // When
            UserDetails userDetails = userService.loadUserById(user.getId().toString());
            // Then
            assertNotNull(userDetails);
            assertEquals(user.getEmail(), userDetails.getUsername());
        }

        @Test
        @DisplayName("When user not found")
        void given_whenLoadUserById_thenAssertNotFound() {
            // When
            Executable executable = () -> userService.loadUserById(user.getId().toString());
            // Then
            assertThrows(NotFoundException.class, executable);
            assertEquals(messageSourceService.get("not_found_with_param",
                    new String[]{messageSourceService.get("user")}),
                assertThrows(NotFoundException.class, executable).getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for getPrincipal scenarios")
    class GetPrincipalTest {
        @Test
        @DisplayName("Happy path")
        void given_whenGetPrincipal_thenAssertBody() {
            // When
            JwtUserDetails result = userService.getPrincipal(authentication);
            // Then
            assertEquals(result, jwtUserDetails);
        }
    }

    @Nested
    @DisplayName("Test class for register scenarios")
    class RegisterTest {
        @Test
        @DisplayName("Happy path")
        void given_whenRegister_thenAssertBody() throws BindException {
            // Given
            RegisterRequest request = Instancio.create(RegisterRequest.class);
            user.setPassword("password");
            when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
            when(roleService.findByName(any(Constants.RoleEnum.class))).thenReturn(user.getRoles().get(0));
            when(userRepository.save(any(User.class))).thenReturn(user);
            // When
            User result = userService.register(request);
            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("When user email already exists")
        void given_whenRegister_thenAssertBindException() {
            // Given
            when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
            // When
            Executable executable = () -> userService.register(Instancio.create(RegisterRequest.class));
            // Then
            assertThrows(BindException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        @DisplayName("Happy path")
        void given_whenCreate_thenAssertBody() throws BindException {
            // Given
            CreateUserRequest request = Instancio.create(CreateUserRequest.class);
            request.setRoles(List.of(Constants.RoleEnum.USER.name()));
            request.setIsEmailVerified(true);
            user.setEmailVerifiedAt(LocalDateTime.MIN);
            request.setIsBlocked(true);
            user.setBlockedAt(LocalDateTime.MIN);
            user.setPassword("encodedPassword");
            when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
            when(roleService.findByName(any(Constants.RoleEnum.class))).thenReturn(user.getRoles().get(0));
            when(userRepository.save(any(User.class))).thenReturn(user);
            // When
            User result = userService.create(request);
            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("When user email already exists")
        void given_whenCreate_thenAssertBindException() {
            // Given
            when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
            // When
            Executable executable = () -> userService.create(Instancio.create(CreateUserRequest.class));
            // Then
            assertThrows(BindException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for update scenarios")
    class UpdateTest {
        private final UpdateUserRequest request = Instancio.create(UpdateUserRequest.class);

        @Test
        @DisplayName("Happy path - Email verified")
        void given_whenUpdate_thenAssertBodyWithEmailVerified() throws BindException {
            // Given
            request.setRoles(List.of(Constants.RoleEnum.USER.name()));
            request.setIsEmailVerified(true);
            user.setEmail("oldEmail");
            request.setEmail("newEmail");
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            // When
            User result = userService.update(user.getId().toString(), request);
            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("Happy path - Email not verified")
        void given_whenUpdate_thenAssertBodyWithEmailNotVerified() throws BindException {
            // Given
            request.setRoles(List.of(Constants.RoleEnum.USER.name()));
            request.setIsEmailVerified(false);
            request.setEmail("newEmail");
            request.setName("newName");
            request.setLastName("newLastName");
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            // When
            User result = userService.update(user.getId().toString(), request);
            // Then
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Test class for update password scenarios")
    class UpdatePasswordTest {
        @Test
        @DisplayName("Happy path")
        void given_whenUpdatePassword_thenAssertBody() throws BindException {
            // Given
            UpdatePasswordRequest request = Instancio.create(UpdatePasswordRequest.class);
            request.setOldPassword("OldP@ssw0rd123.");
            request.setPassword("P@ssw0rd123.");
            request.setPasswordConfirm("P@ssw0rd123.");
            when(userRepository.findById(UUID.fromString(jwtUserDetails.getId()))).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
            when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            // When
            User result = userService.updatePassword(request);
            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("Wrong old password test")
        void given_whenUpdatePasswordWrongOldPassword_thenBindingException() {
            // Given
            UpdatePasswordRequest request = Instancio.create(UpdatePasswordRequest.class);
            request.setOldPassword("OldP@ssw0rd123.");
            request.setPassword("NewP@ssw0rd123.");
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
            when(userRepository.findById(UUID.fromString(jwtUserDetails.getId()))).thenReturn(Optional.of(user));
            // When
            Executable executable = () -> userService.updatePassword(Instancio.create(UpdatePasswordRequest.class));
            // Then
            assertThrows(BindException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for reset password scenarios")
    class ResetPasswordTest {
        private final ResetPasswordRequest request = Instancio.create(ResetPasswordRequest.class);

        @Test
        @DisplayName("Happy path")
        void given_whenResetPassword_thenAssertBody() {
            // Given
            when(passwordResetTokenService.getUserByToken(any(String.class))).thenReturn(user);
            when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            doNothing().when(passwordResetTokenService).deleteByUserId(any(UUID.class));
            // When
            String token = "token";
            userService.resetPassword(token, request);
            // Then
            verify(passwordResetTokenService, Mockito.times(1)).deleteByUserId(user.getId());
        }
    }

    @Nested
    @DisplayName("Test class for resend e-mail verification scenarios")
    class ResentEmailVerificationMailTest {
        @Test
        @DisplayName("Happy path")
        void given_whenResendEmailVerificationMail_thenAssertBody() {
            // Given
            user.setEmailVerifiedAt(null);
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            doNothing().when(eventPublisher).publishEvent(any(UserEmailVerificationSendEvent.class));
            // When
            userService.resendEmailVerificationMail();
            // Then
            verify(eventPublisher, Mockito.times(1))
                .publishEvent(any(UserEmailVerificationSendEvent.class));
        }

        @Test
        @DisplayName("Not authenticated test")
        void given_whenResendEmailVerificationMail_thenAssertBadCredentialsException() {
            // Given
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            // When
            Executable executable = () -> userService.resendEmailVerificationMail();
            // Then
            assertThrows(BadCredentialsException.class, executable);
        }

        @Test
        @DisplayName("E-mail already verified test")
        void given_whenResendEmailVerificationMail_thenAssertBadRequestException() {
            // Given
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            // When
            Executable executable = () -> userService.resendEmailVerificationMail();
            // Then
            assertThrows(BadRequestException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for verify e-mail scenarios")
    class VerifyEmail {
        private final String token = "token";

        @Test
        @DisplayName("Happy path")
        void given_whenVerifyEmail_thenAssertBody() {
            // Given
            when(emailVerificationTokenService.getUserByToken(token)).thenReturn(user);
            user.setEmailVerifiedAt(LocalDateTime.now());
            when(userRepository.save(user)).thenReturn(user);
            doNothing().when(emailVerificationTokenService).deleteByUserId(user.getId());
            // When
            userService.verifyEmail(token);
            // Then
            verify(userRepository, Mockito.times(1)).save(user);
            verify(emailVerificationTokenService, Mockito.times(1)).deleteByUserId(user.getId());
        }

        @Test
        @DisplayName("Token not found test")
        void given_whenVerifyEmail_thenShouldThrowNotFoundException() {
            // Given
            when(emailVerificationTokenService.getUserByToken(token)).thenThrow(NotFoundException.class);
            // When
            Executable executable = () -> userService.verifyEmail(token);
            // Then
            assertThrows(NotFoundException.class, executable);
        }

        @Test
        @DisplayName("Token expired test")
        void given_whenVerifyEmail_thenShouldThrowBadRequestException() {
            // Given
            when(emailVerificationTokenService.getUserByToken(token)).thenThrow(BadRequestException.class);
            // When
            Executable executable = () -> userService.verifyEmail(token);
            // Then
            assertThrows(BadRequestException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for send password reset e-mail scenarios")
    class SendEmailPasswordResetMailTest {
        private final PasswordResetToken passwordResetToken = Instancio.create(PasswordResetToken.class);

        @BeforeEach
        void setUp() {
            passwordResetToken.setUser(user);
        }

        @Test
        @DisplayName("Happy path")
        void given_whenSendEmailPasswordResetMail_thenAssertBody() {
            // Given
            when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
            doNothing().when(eventPublisher).publishEvent(any(UserPasswordResetSendEvent.class));
            when(passwordResetTokenService.create(any(User.class))).thenReturn(user.getPasswordResetToken());
            // When
            userService.sendEmailPasswordResetMail(user.getEmail());
            // Then
            verify(eventPublisher, Mockito.times(1))
                .publishEvent(any(UserPasswordResetSendEvent.class));
        }

        @Test
        @DisplayName("User not found test")
        void given_whenSendEmailPasswordResetMail_thenShouldThrowNotFoundException() {
            // Given
            when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
            // When
            Executable executable = () -> userService.sendEmailPasswordResetMail(user.getEmail());
            // Then
            assertThrows(NotFoundException.class, executable);
        }
    }

    @Nested
    @DisplayName("Test class for delete scenarios")
    class DeleteTest {
        @Test
        @DisplayName("Happy path")
        void given_whenDelete_thenAssertBody() {
            // Given
            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
            // When
            userService.delete(user.getId().toString());
            // Then
            verify(userRepository, Mockito.times(1)).delete(user);
        }

        @Test
        @DisplayName("When user not found")
        void given_whenDelete_thenAssertNotFound() {
            // When
            Executable executable = () -> userService.delete(user.getId().toString());
            // Then
            assertThrows(NotFoundException.class, executable);
            assertEquals(messageSourceService.get("not_found_with_param",
                    new String[]{messageSourceService.get("user")}),
                assertThrows(NotFoundException.class, executable).getMessage());
        }
    }
}
