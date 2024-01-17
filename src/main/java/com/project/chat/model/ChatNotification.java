package com.project.chat.model;


import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotification {
    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private String content;
}
