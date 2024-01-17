package com.project.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chat.security.JwtUtils;
import com.project.chat.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

     @Override
     public void configureMessageBroker(MessageBrokerRegistry registry){
         registry.enableSimpleBroker("/user", "/topic");
         registry.setApplicationDestinationPrefixes("/app");
         registry.setUserDestinationPrefix("/user");
     }

     @Override
     public void registerStompEndpoints(StompEndpointRegistry registry){
         registry.addEndpoint("/ws")
                 .setAllowedOrigins("http://localhost:3030").withSockJS();
     }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("authToken in: " + authToken );
                    if (authToken != null && jwtUtils.validateJwtToken(authToken)) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(authToken);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                return message;
            }
        });
    }

     @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters){
         DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
         resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
         MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
         converter.setObjectMapper(new ObjectMapper());
         converter.setContentTypeResolver(resolver);
         messageConverters.add(converter);

         return false;
     }
}
