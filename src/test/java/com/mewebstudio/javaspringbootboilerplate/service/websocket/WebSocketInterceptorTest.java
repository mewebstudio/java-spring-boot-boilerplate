package com.mewebstudio.javaspringbootboilerplate.service.websocket;

import com.mewebstudio.javaspringbootboilerplate.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for WebSocketInterceptor")
public class WebSocketInterceptorTest {
    @InjectMocks private WebSocketInterceptor webSocketInterceptor;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private ServerHttpRequest request;
    @Mock private ServerHttpResponse response;
    @Mock private WebSocketHandler wsHandler;

    @Nested
    @DisplayName("Test class for beforeHandshake scenarios")
    class BeforeHandshakeTest {
        @Test
        void givenServerHttpRequestAndServerHttpResponseAndWebSocketHandlerAndMapAttributes_whenBeforeHandshake_thenAssertBody() {
            // Given
            when(request.getURI()).thenReturn(URI.create("/websocket/someToken"));
            when(jwtTokenProvider.validateToken("someToken", false)).thenReturn(true);

            // When
            Map<String, Object> attributes = new HashMap<>();
            boolean result = webSocketInterceptor.beforeHandshake(request, response, wsHandler, attributes);

            // Then
            assertTrue(result);
        }

        @Test
        void givenServerHttpRequestAndServerHttpResponseAndWebSocketHandlerAndMapAttributes_whenBeforeHandshake_thenInValidToken() {
            // Given
            when(request.getURI()).thenReturn(URI.create("/websocket/invalidToken"));
            when(jwtTokenProvider.validateToken("invalidToken", false)).thenReturn(false);

            // When
            Map<String, Object> attributes = new HashMap<>();
            boolean result = webSocketInterceptor.beforeHandshake(request, response, wsHandler, attributes);

            // Then
            assertFalse(result);
        }

        @Test
        void givenServerHttpRequestAndServerHttpResponseAndWebSocketHandlerAndMapAttributes_whenBeforeHandshake_thenTokenNull() {
            // Given
            when(request.getURI()).thenReturn(URI.create("/websocket/"));

            // When
            Map<String, Object> attributes = new HashMap<>();
            boolean result = webSocketInterceptor.beforeHandshake(request, response, wsHandler, attributes);

            // Then
            assertFalse(result);
            assertTrue(attributes.isEmpty());
            verify(jwtTokenProvider, never()).validateToken(anyString());
        }
    }

    @Nested
    @DisplayName("Test class for afterHandshake scenarios")
    class AfterHandshakeTest {
        @Test
        void givenServerHttpRequestAndServerHttpResponseAndWebSocketHandlerAndException_whenAfterHandshake_thenAssertBody() {
            // Given
            Exception exception = mock(Exception.class);

            // When
            webSocketInterceptor.afterHandshake(request, response, wsHandler, exception);

            // Then
            verify(response, times(0)).setStatusCode(any());
            verifyNoMoreInteractions(response);
        }
    }
}
