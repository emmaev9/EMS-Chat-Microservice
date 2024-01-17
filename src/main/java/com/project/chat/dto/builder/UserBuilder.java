package com.project.chat.dto.builder;

import com.project.chat.dto.UserDTO;
import com.project.chat.model.Person;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserBuilder {
    public static Person fromDtoToUser(UserDTO userDTO){
        Person newPerson = new Person();
        newPerson.setAddress(userDTO.getAddress());
        newPerson.setAge(userDTO.getAge());
        newPerson.setId(userDTO.getId());
        newPerson.setName(userDTO.getName());
        newPerson.setEmail(userDTO.getEmail());
        newPerson.setPassword(userDTO.getPassword());
        newPerson.setRole(userDTO.getRole());
        return newPerson;
    }
}
