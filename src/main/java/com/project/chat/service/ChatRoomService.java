package com.project.chat.service;

import com.project.chat.model.ChatRoom;
import com.project.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(UUID senderID, UUID recipientID, boolean createNewRoomIfNotExists){

        System.out.println("Trying to find chat room id");
        return chatRoomRepository.findBySenderIdAndRecipientId(senderID, recipientID)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        return Optional.of(createChat(senderID, recipientID));
                    }
                    return Optional.empty();
                });
    }

    private String createChat(UUID senderId, UUID recipientId){
        String chatId = String.format("%s_%s", senderId.toString(), recipientId.toString());
        System.out.println("Chat ID: "+ chatId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);
        return chatId;
    }
}
