package com.tradingbot.dotty.configurations;

import com.tradingbot.dotty.utils.WebSocketsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.tradingbot.dotty.utils.Constants.WEBSOCKET_SESSION_ATTRIBUTE_USER;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private WebSocketsHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        HandshakeInterceptor authInterceptor = new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    if (authentication instanceof JwtAuthenticationToken jwtToken) {
                        String userId = jwtToken.getName();
                        attributes.put(WEBSOCKET_SESSION_ATTRIBUTE_USER, userId);
                        return true;
                    } else if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                        String userId = oauthToken.getPrincipal().getAttribute("sub");
                        attributes.put(WEBSOCKET_SESSION_ATTRIBUTE_USER, userId);
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };


        registry.addHandler(webSocketHandler, "dotty")
                .setAllowedOrigins("*")
                .addInterceptors(authInterceptor);
        registry.addHandler(webSocketHandler, "dotty")
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*")
                .withSockJS();
    }

}

/* @Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/dotty");                                                                         // .setAllowedOrigins("http://localhost:8081");
        registry.addEndpoint("/dotty").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
        registry.setPreservePublishOrder(true);
    }

}
*/