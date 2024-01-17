package com.project.chat.service;

import com.project.chat.dto.MessageSeenDTO;
import com.project.chat.model.ChatMessage;
import com.project.chat.model.ChatNotification;
import com.project.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    //private static final Logger LOGGER = Logger.getLogger(ChatService.class.getName());

    public ChatMessage save(ChatMessage chatMessage) {
        System.out.println("IN CHAT MESSAGE SAVE!!!!!!!!!!!!!!!!!!!!!!!!");
        Optional<String> chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        chatMessage.setChatId(chatId.get());
        chatMessage.setSeen(false);
        chatMessage = chatRepository.save(chatMessage);
        return chatMessage;
    }
    public List<ChatMessage> findChatMessages(UUID senderId, UUID recipientId){
        Optional<String> chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(chatRepository::findByChatId).orElse(new ArrayList<>());
    }
    public void markAsSeen(UUID messageId){
        Optional<ChatMessage> messageOptional = chatRepository.findById(messageId);
        if(messageOptional.isPresent()){
            messageOptional.get().setSeen(true);
            chatRepository.save(messageOptional.get());

            String queue = "/topic/messageSeen";
            simpMessagingTemplate.convertAndSend( queue, new MessageSeenDTO(messageId));
            System.out.println("Message was seen");
        }
    }
}