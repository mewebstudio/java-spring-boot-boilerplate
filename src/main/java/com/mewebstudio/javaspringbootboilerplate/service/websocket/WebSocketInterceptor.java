package com.mewebstudio.javaspringbootboilerplate.service.websocket;

import com.mewebstudio.javaspringbootboilerplate.security.JwtTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.Map;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.getTokenFromPath;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        log.debug("Received an incoming websocket channel request");
        String token = getTokenFromPath(request.getURI().getPath());
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.error("Invalid token or token not present in WebSocket request");
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {
        log.info("afterHandshake");
    }
}
