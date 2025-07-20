//package com.codebloom.cineman.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
////@EnableWebSocketMessageBroker
//@RequiredArgsConstructor
//public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
//
////    @Autowired
////    AppHandshakeInterceptor interceptor;
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").setAllowedOrigins("*"); /*.addInterceptors(interceptor);*/
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/cineman/app");
//        registry.enableSimpleBroker("/topic");
//    }
//
//}
