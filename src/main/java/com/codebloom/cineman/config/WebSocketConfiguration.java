    package com.codebloom.cineman.config;
    
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
    
    @Configuration
    @EnableWebSocketMessageBroker
    @RequiredArgsConstructor
    @Slf4j(topic = "WEBSOCKET-CONFIGURATION")
    public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    
    //    @Autowired
    //    AppHandshakeInterceptor interceptor;

        @Bean
        public ThreadPoolTaskScheduler myMessageBrokerScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadNamePrefix("my-ws-scheduler-");
            scheduler.setRemoveOnCancelPolicy(true);
            scheduler.initialize();
            return scheduler;
        }


        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            log.info("Đăng ký STOMP endpoint tại /cineman-ws");
            registry.addEndpoint("/cineman-ws")
                    .setAllowedOrigins("*");
            /*.addInterceptors(interceptor);*/
        }
    
        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            log.info("Cấu hình Message Broker /cineman/topic và /cineman/app");
            registry.setApplicationDestinationPrefixes("/cineman/app");
            registry.enableSimpleBroker("/cineman/topic");
        }
    
    }
