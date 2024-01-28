package com.mewebstudio.javaspringbootboilerplate.service.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WebsocketIdentifier;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WsRequestBody;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for WebSocketCacheService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebSocketCacheServiceTest {
    @InjectMocks private WebSocketCacheService webSocketCacheService;
    @Mock private WebSocketSession webSocketSession;
    @Mock private ObjectMapper objectMapper;

    private final WebsocketIdentifier websocketIdentifier = Instancio.create(WebsocketIdentifier.class);

    @BeforeEach
    void setUp() throws JsonProcessingException {
        websocketIdentifier.setSession(webSocketSession);
        lenient().when(objectMapper.writeValueAsString(any(WsRequestBody.class))).thenReturn("writeValueAsString");
    }

    @Test
    @Order(1)
    void given_whenGetAllWebSocketSession_thenAssertResult() {
        // When
        Map<String, WebsocketIdentifier> result = webSocketCacheService.getAllWebSocketSession();
        // Then
        assertEquals(0, result.size());
    }

    @Test
    @Order(2)
    void given_whenPut_thenAssertResult() throws IOException {
        // Given
        // When
        webSocketCacheService.put(websocketIdentifier);
        // Then
        verify(webSocketSession, Mockito.times(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    @Order(3)
    void given_whenGetOrDefault_thenAssertResult() {
        // When
        WebsocketIdentifier result = webSocketCacheService.getOrDefault("key");
        // Then
        assertNull(result);
    }

    @Test
    @Order(4)
    void given_whenDeleteSession_thenAssertResult(){
        // When
        Executable result = () -> webSocketCacheService.deleteSession("key");
        // Then
        assertDoesNotThrow(result);
    }
}
