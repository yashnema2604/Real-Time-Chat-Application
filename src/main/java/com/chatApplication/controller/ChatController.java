package com.chatApplication.controller;

import com.chatApplication.model.ChatMessage;
import com.chatApplication.repository.ChatMessageRepository;
import com.chatApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //WEBSOCKET DESTINATION
    @MessageMapping("/chat.addUser")
    //CHANNELS
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        if(userService.userExist(chatMessage.getSender())){

            //store username in session

            headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());
            userService.setUserOnlineStatus(chatMessage.getSender(),true);

            System.out.println("user added successfully "+chatMessage.getSender()+" with session ID "+headerAccessor.getSessionId());

            chatMessage.setTimestamp(LocalDateTime.now());
            if(chatMessage.getContent()==null){
                chatMessage.setContent("");
            }

            return chatMessageRepository.save(chatMessage);
        }
        return null;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        if (userService.userExist(chatMessage.getSender())){
            if(chatMessage.getTimestamp()==null){
                chatMessage.setTimestamp(LocalDateTime.now());
            }

            if(chatMessage.getContent()==null){
                chatMessage.setContent("");
            }

            return chatMessageRepository.save(chatMessage);
        }

        return null;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor headerAccessor){
        if(userService.userExist(chatMessage.getSender()) && userService.userExist(chatMessage.getRecipient())){

            if(chatMessage.getTimestamp()==null){
                chatMessage.setTimestamp(LocalDateTime.now());
            }

            if(chatMessage.getContent()==null){
                chatMessage.setContent("");
            }

            chatMessage.setType(ChatMessage.MessageType.PRIVATE_MESSAGE);
            ChatMessage savedMessage=chatMessageRepository.save(chatMessage);
            System.out.println("Message saved successfully with Id "+savedMessage.getId());

            try {
                String recipientDestination="/user"+chatMessage.getRecipient()+"/queue/private";
                System.out.println("Sending message to recipient destination "+recipientDestination);
                messagingTemplate.convertAndSend(recipientDestination,savedMessage);

                String senderDestination="user/"+chatMessage.getSender()+"/queue/private";
                System.out.println("Sending message to sender destination "+senderDestination);
                messagingTemplate.convertAndSend(senderDestination,savedMessage);
            }catch (Exception ex){
                System.out.println("ERROR occured while sending the message "+ex.getMessage());
                ex.printStackTrace();
            }
        }else{
            System.out.println("ERROR: Sender "+chatMessage.getSender()+" or recipient "+chatMessage.getRecipient()+" doesn't exist");
        }
    }


}
