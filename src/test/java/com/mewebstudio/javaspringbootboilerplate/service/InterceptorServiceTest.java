package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.TestController;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.security.JwtUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Unit tests for InterceptorService class")
class InterceptorServiceTest {
    @InjectMocks
    private InterceptorService interceptorService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    private final User user = Instancio.create(User.class);

    private final JwtUserDetails jwtUserDetails = JwtUserDetails.create(user);

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        MockitoAnnotations.openMocks(this);
        handlerMethod = new HandlerMethod(new TestController(), "validMethodName");
    }

    @Test
    @DisplayName("Test preHandle method with valid authorization")
    void given_whenPreHandleValidAuthorization_thenAssertBody() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(authenticationService.getPrincipal()).thenReturn(jwtUserDetails);
        // When
        boolean result = interceptorService.preHandle(request, response, handlerMethod);
        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Test preHandle method with invalid handler")
    void given_whenPreHandleInvalidHandler_thenAssertBody() {
        // Given
        Object invalidHandler = new Object();
        // When
        boolean result = interceptorService.preHandle(request, response, invalidHandler);
        // Then
        assertTrue(result);
        verify(authenticationService, never()).getPrincipal();
        verify(authenticationService, never()).isAuthorized(anyString());
    }
}
