package com.mewebstudio.javaspringbootboilerplate.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WebsocketIdentifier;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WsRequestBody;
import com.mewebstudio.javaspringbootboilerplate.security.JwtTokenProvider;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for WebsocketChannelHandler")
class WebsocketChannelHandlerTest {
    @InjectMocks private WebsocketChannelHandler websocketChannelHandler;
    @Mock private WebSocketCacheService webSocketCacheService;
    @Mock private ObjectMapper objectMapper;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private WebSocketSession webSocketSession;

    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        uri = new URI("http://localhost:8080/ws/token-is-here");
        lenient().when(webSocketSession.getUri()).thenReturn(uri);
    }

    @Nested
    class AfterConnectionEstablished {
        @Test
        void givenWebSocketSession_whenAfterConnectionEstablishedWithNullUri_thenAssertResult() throws IOException {
            // Given
            when(webSocketSession.getUri()).thenReturn(null);
            // When
            websocketChannelHandler.afterConnectionEstablished(webSocketSession);
            // Then
            verify(webSocketSession).close(CloseStatus.BAD_DATA);
        }

        @Test
        void givenWebSocketSession_whenAfterConnectionEstablishedWithNullWebsocketIdentifier_thenAssertResult() throws IOException, URISyntaxException {
            // Given
            uri = new URI("http://localhost:8080/ws");
            lenient().when(webSocketSession.getUri()).thenReturn(uri);
            // When
            websocketChannelHandler.afterConnectionEstablished(webSocketSession);
            // Then
            verify(webSocketSession).close(CloseStatus.NOT_ACCEPTABLE);
        }

        @Test
        void givenWebSocketSession_whenAfterConnectionEstablished_thenAssertResult() throws IOException{
            // Given
            when(jwtTokenProvider.getUserIdFromToken("token-is-here")).thenReturn("user-id");
            // When
            websocketChannelHandler.afterConnectionEstablished(webSocketSession);
            // Then
            verify(webSocketCacheService).put(any(WebsocketIdentifier.class));
        }
    }

    @Nested
    class AfterConnectionClosed {
        @Test
        void givenWebSocketSessionAndCloseStatus_whenAfterConnectionClosedWithNullUri_thenAssertResult() throws IOException {
            // Given
            when(webSocketSession.getUri()).thenReturn(null);
            // When
            websocketChannelHandler.afterConnectionClosed(webSocketSession, CloseStatus.BAD_DATA);
            // Then
            verify(webSocketSession).close(CloseStatus.BAD_DATA);
        }

        @Test
        void givenWebSocketSessionAndCloseStatus_whenAfterConnectionClosedWithNullWebsocketIdentifier_thenAssertResult() throws IOException, URISyntaxException {
            // Given
            uri = new URI("http://localhost:8080/ws");
            lenient().when(webSocketSession.getUri()).thenReturn(uri);
            // When
            websocketChannelHandler.afterConnectionClosed(webSocketSession, CloseStatus.BAD_DATA);
            // Then
            verify(webSocketSession).close(CloseStatus.NOT_ACCEPTABLE);
        }

        @Test
        void givenWebSocketSessionAndCloseStatus_whenAfterConnectionClosed_thenAssertResult() throws IOException{
            // Given
            when(jwtTokenProvider.getUserIdFromToken("token-is-here")).thenReturn("user-id");
            // When
            websocketChannelHandler.afterConnectionClosed(webSocketSession, CloseStatus.BAD_DATA);
            // Then
            verify(webSocketCacheService).deleteSession("user-id");
        }
    }

    @Nested
    class HandleTextMessage {
        private final TextMessage textMessage = Instancio.create(TextMessage.class);
        private final WsRequestBody wsRequestBody = Instancio.create(WsRequestBody.class);

        @Test
        void givenWebSocketSessionAndTextMessage_whenHandleTextMessageWithNullUri_thenAssertResult() throws IOException {
            // Given
            when(webSocketSession.getUri()).thenReturn(null);
            // When
            websocketChannelHandler.handleTextMessage(webSocketSession, textMessage);
            // Then
            verify(webSocketSession).close(CloseStatus.BAD_DATA);
        }

        @Test
        void givenWebSocketSessionAndTextMessage_whenAfterConnectionClosedWithNullWebsocketIdentifier_thenAssertResult() throws IOException, URISyntaxException {
            // Given
            uri = new URI("http://localhost:8080/ws");
            lenient().when(webSocketSession.getUri()).thenReturn(uri);
            // When
            websocketChannelHandler.handleTextMessage(webSocketSession, textMessage);
            // Then
            verify(webSocketSession).close(CloseStatus.NOT_ACCEPTABLE);
        }

        @Test
        void givenWebSocketSessionAndTextMessage_whenAfterConnectionClosedWithTypeIsNull_thenAssertResult() throws IOException{
            // Given
            when(jwtTokenProvider.getUserIdFromToken("token-is-here")).thenReturn("user-id");
            wsRequestBody.setType(null);
            when(objectMapper.readValue(textMessage.getPayload(), WsRequestBody.class)).thenReturn(wsRequestBody);
            // When
            websocketChannelHandler.handleTextMessage(webSocketSession, textMessage);
            // Then
            verify(webSocketSession).sendMessage(any(TextMessage.class));
        }

        @Test
        void givenWebSocketSessionAndTextMessage_whenAfterConnectionClosedWithTypeIsNotPrivate_thenAssertResult() throws IOException{
            // Given
            when(jwtTokenProvider.getUserIdFromToken("token-is-here")).thenReturn("user-id");
            when(objectMapper.readValue(textMessage.getPayload(), WsRequestBody.class)).thenReturn(wsRequestBody);
            // When
            websocketChannelHandler.handleTextMessage(webSocketSession, textMessage);
            // Then
            verify(webSocketSession).sendMessage(any(TextMessage.class));
        }

        @Test
        void givenWebSocketSessionAndTextMessage_whenAfterConnectionClosedWithTypeIsPrivate_thenAssertResult() throws IOException{
            // Given
            when(jwtTokenProvider.getUserIdFromToken("token-is-here")).thenReturn("user-id");
            wsRequestBody.setType("private");
            when(objectMapper.readValue(textMessage.getPayload(), WsRequestBody.class)).thenReturn(wsRequestBody);
            // When
            websocketChannelHandler.handleTextMessage(webSocketSession, textMessage);
            // Then
            verify(webSocketCacheService).sendPrivateMessage(wsRequestBody);
        }
    }

}