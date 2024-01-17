package com.project.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingMessageDTO {
    private UUID senderId;
    private UUID recipientId;
    private boolean typing;

}
