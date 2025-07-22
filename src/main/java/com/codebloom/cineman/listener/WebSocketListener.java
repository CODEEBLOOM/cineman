package com.codebloom.cineman.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
@Slf4j(topic = "WEB_SOCKET_LISTENER")
public class WebSocketListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void connect(SessionConnectEvent event) {
        System.out.println("connect()");
    }

    @EventListener
    public void clientConnected(SessionConnectedEvent event) {
        log.info("clientConnected()");
        System.out.println("connected()");
    }

    @EventListener
    public void disconnected(SessionDisconnectEvent event) {
        log.info("disconnected()");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

//        if (username != null) {
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(ChatMessage.MessageType.LEAVE);
//            chatMessage.setSender(username);
//            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
//        }
    }

    @EventListener
    public void subscribe(SessionSubscribeEvent event) {
        log.info("subscribe()");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // Lấy endpoint (destination) mà client đã đăng ký
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        log.info("Session [{}] subscribed to destination: {}", sessionId, destination);
    }

    @EventListener
    public void unsubscribe(SessionUnsubscribeEvent event) {
        log.info("unsubscribe()");
    }
}