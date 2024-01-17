package com.project.chat.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chat.dto.MessageSeenDTO;
import com.project.chat.dto.TypingMessageDTO;
import com.project.chat.model.ChatMessage;
import com.project.chat.model.ChatNotification;
import com.project.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage
    ){
        System.out.println("Chat message content: " + chatMessage.getContent());
        ChatMessage savedMessage = chatService.save(chatMessage);
        String recipientQueue = "/user/" + savedMessage.getRecipientId() + "/queue/messages";
        messagingTemplate.convertAndSend(recipientQueue,
                ChatNotification.builder()
                        .id(savedMessage.getId())
                        .senderId(savedMessage.getSenderId())
                        .recipientId(savedMessage.getRecipientId())
                        .content(savedMessage.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable("senderId") UUID senderId,
            @PathVariable("recipientId") UUID recipientId
    ){
        return ResponseEntity.ok(chatService.findChatMessages(senderId, recipientId));
    }

    @MessageMapping("/markSeen")
    public void markMessageAsSeen(@Payload MessageSeenDTO messageId){
        chatService.markAsSeen(messageId.getMessageId());
    }

    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingMessageDTO typingMessage){
        System.out.println("User typing......." + "senderId: " + typingMessage.getSenderId()
                + " recipientId: " + typingMessage.getRecipientId()
                + " typing status: " + typingMessage.isTyping());
        String topic = "/topic/typing";
        messagingTemplate.convertAndSend(topic, typingMessage);
    }
}
