package com.project.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDTO {
    private UUID id;
    private String name;
    private int age;
    private String address;
    private String email;
    private String role;
    private String password;
}
