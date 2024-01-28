package com.mewebstudio.javaspringbootboilerplate.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WebsocketIdentifier;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.WsRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketCacheService {
    private static final Map<String, WebsocketIdentifier> USER_SESSION_CACHE = new ConcurrentHashMap<>();

    private static final String EXCEPTION_MESSAGE = "Exception while sending message: {}";

    private final ObjectMapper objectMapper;

    /**
     * Get all websocket session cache.
     * @return map of websocket session cache.
     */
    public Map<String, WebsocketIdentifier> getAllWebSocketSession() {
        return USER_SESSION_CACHE;
    }

    /**
     * Add websocket session cache.
     * @param data websocket session cache.
     */
    public void put(WebsocketIdentifier data) {
        USER_SESSION_CACHE.put(data.getUserId(), data);
        broadCastMessage(data.getUserId(), "login");
        broadCastAllUserList(data.getUserId());
    }

    /**
     * Get or default websocket session cache.
     * @param key key of websocket session cache.
     * @return websocket session cache.
     */
    public WebsocketIdentifier getOrDefault(String key) {
        return USER_SESSION_CACHE.getOrDefault(key, null);
    }

    /**
     * Remove websocket session cache.
     * @param key key of websocket session cache.
     */
    public void deleteSession(String key) {
        WebsocketIdentifier websocketIdentifier = getOrDefault(key);
        if (websocketIdentifier == null || websocketIdentifier.getSession() == null){
            log.error("Unable to remove the websocket session; serious error!");
            return;
        }
        USER_SESSION_CACHE.remove(key);
        broadCastMessage(websocketIdentifier.getUserId(), "logout");
    }

    /**
     * Broadcast message to all websocket session cache.
     * @param message message to broadcast.
     */
    private void broadCastMessage(String message, String type) {
        WsRequestBody wsRequestBody = new WsRequestBody();
        wsRequestBody.setContent(message);
        wsRequestBody.setDate(Instant.now().toEpochMilli());
        wsRequestBody.setType(type);
        Map<String, WebsocketIdentifier> allWebSocketSession = getAllWebSocketSession();
        for (Map.Entry<String, WebsocketIdentifier> entry : allWebSocketSession.entrySet()) {
            try {
                entry.getValue().getSession()
                        .sendMessage(new TextMessage(objectMapper.writeValueAsString(wsRequestBody)));
            } catch (Exception e) {
                log.error("Exception while broadcasting: {}", ExceptionUtils.getMessage(e));
            }
        }
    }

    /**
     * Broadcast message to specific websocket session cache.
     * @param requestBody message to send.
     */
    @Transactional
    public void sendPrivateMessage(WsRequestBody requestBody) {
        WebsocketIdentifier userTo = getOrDefault(requestBody.getTo());
        if (userTo == null) {
            log.error("User or Session not found in cache for user: {}, returning...", requestBody.getTo());
            return;
        }
        requestBody.setType("private");
        Instant now = Instant.now();
        requestBody.setDate(now.toEpochMilli());
        String payload;
        try {
            payload = objectMapper.writeValueAsString(requestBody);
            if (userTo.getSession() != null) {
                userTo.getSession().sendMessage(new TextMessage(payload));
                log.info("Message successfully send to {}", userTo);
            }
        } catch (Exception e) {
            log.error(EXCEPTION_MESSAGE, ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Broadcast message to specific websocket session cache.
     * @param from from user.
     * @param payload message to send.
     */
    public void sendMessage(final String from, final String to, final String type, final String payload) {
        WebsocketIdentifier userTo = getOrDefault(to);
        if (userTo == null || userTo.getSession() == null) {
            log.error("User or Session not found in cache for user: {}, returning...", to);
            return;
        }
        WsRequestBody requestBody = new WsRequestBody();
        requestBody.setFrom(from);
        requestBody.setTo(to);
        requestBody.setDate(Instant.now().toEpochMilli());
        requestBody.setContent(payload);
        requestBody.setType(type);
        try {
            userTo.getSession().sendMessage(new TextMessage(objectMapper.writeValueAsString(requestBody)));
        } catch (IOException e) {
            log.error(EXCEPTION_MESSAGE, ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Broadcast message to all websocket session cache.
     * @param user user to broadcast.
     */
    private void broadCastAllUserList(final String user) {
        sendMessage("server", user, "online", StringUtils.join(USER_SESSION_CACHE.keySet(), ','));
    }
}
