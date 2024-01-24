package com.mewebstudio.javaspringbootboilerplate.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WebsocketIdentifier;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WsRequestBody;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.security.JwtTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import java.net.URI;
import static com.mewebstudio.javaspringbootboilerplate.util.Constants.getTokenFromPath;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebsocketChannelHandler extends AbstractWebSocketHandler {
    private final WebSocketCacheService webSocketCacheService;

    private final ObjectMapper objectMapper;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * A method that is called when a new WebSocket session is created.
     * @param session The new WebSocket session.
     */
    @Override
    public void afterConnectionEstablished(@NonNull final WebSocketSession session) {
        try {
            final String uri = getUri(session);
            if (uri == null) {
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            final WebsocketIdentifier websocketIdentifier = generateWebsocketIdentifier(uri);
            if (websocketIdentifier == null) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            websocketIdentifier.setSession(session);
            webSocketCacheService.put(websocketIdentifier);
            log.info("Websocket session established: {}", websocketIdentifier);
        } catch (Throwable ex) {
            log.error("A serious error has occurred with websocket post-connection handling. Exception is: {}", ex.getMessage());
        }
    }

    /**
     * A method that is called when a WebSocket session is closed.
     * @param session The WebSocket session that is closed.
     * @param status The status of the close.
     */
    @Override
    public void afterConnectionClosed(@NonNull final WebSocketSession session, @NonNull final CloseStatus status) {
        try {
            final String uri = getUri(session);
            if (uri == null) {
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            final WebsocketIdentifier websocketIdentifier = generateWebsocketIdentifier(uri);
            if (websocketIdentifier == null) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            webSocketCacheService.deleteSession(websocketIdentifier.getUserId());
            log.info("Websocket channel {} has been closed", websocketIdentifier);
        } catch (Throwable ex) {
            log.error("Error occurred while closing websocket channel:{}", ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * A method that is called when a WebSocket session receives a message.
     * @param session The WebSocket session that received the message.
     * @param message The message received.
     */
    @Override
    public void handleTextMessage(@NonNull final WebSocketSession session, @NonNull final TextMessage message) {
        try {
            final String uri = getUri(session);
            if (uri == null) {
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            final WebsocketIdentifier websocketIdentifier = generateWebsocketIdentifier(uri);
            if (websocketIdentifier == null) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            WsRequestBody requestBody = objectMapper.readValue(message.getPayload(), WsRequestBody.class);
            requestBody.setFrom(websocketIdentifier.getUserId());
            if (requestBody.getType().equals("private")) {
                webSocketCacheService.sendPrivateMessage(requestBody);
            } else {
                log.error("Invalid ws message type: {}", requestBody.getType());
                throw new BadRequestException("invalid type");
            }
            log.info("Websocket message sent: {}", message.getPayload());
        } catch (Throwable ex) {
            log.error("A serious error has occurred with incoming websocket text message handling. Exception is: ", ex);
        }
    }

    private String getUri(final WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            log.error("Unable to retrieve the websocket session; serious error!");
            return null;
        }
        return uri.getPath();
    }

    private WebsocketIdentifier generateWebsocketIdentifier(final String uri) {
        final WebsocketIdentifier websocketIdentifier = new WebsocketIdentifier();
        final String token = getTokenFromPath(uri);
        websocketIdentifier.setToken(token);
        if (token == null) {
            log.error("Unable to extract the websocketIdentifier; serious error!");
            return null;
        }
        websocketIdentifier.setUserId(jwtTokenProvider.getUserIdFromToken(token));
        return websocketIdentifier;
    }
}