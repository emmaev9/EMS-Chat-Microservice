package com.project.chat.controller;

import com.project.chat.model.Person;
import com.project.chat.service.PersonService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @SendTo("/user/topic")
    public Person addPerson(@Payload Person person){
        //personService.insert(person);
        return person;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public Person disconnect(@Payload Person person){
        personService.disconnect(person);
        System.out.println("User disconnected");
        return person;
    }

    @MessageMapping("/user.connectUser")
    @SendTo("/user/topic")
    public Person connect(@Payload Person person){
        personService.connect(person);
        System.out.println("User connected");
        return person;
    }

    @GetMapping("/onlineUsers")
    public ResponseEntity<List<Person>> findConnectedUsers(){
        return ResponseEntity.ok(personService.findConnectedUsers());
    }


}
