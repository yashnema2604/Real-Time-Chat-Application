package com.chatApplication.listener;


import com.chatApplication.model.ChatMessage;
import com.chatApplication.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketListener {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private static final Logger logger= LoggerFactory.getLogger(WebSocketListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        logger.info("Connected to websocket");
    }

    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username=headerAccessor.getSessionAttributes().get("username").toString();

        System.out.println("User Disconnected from websocket");
        userService.setUserOnlineStatus(username,false);
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender(username);
        messagingTemplate.convertAndSend("/topic/public",chatMessage);
    }
}
