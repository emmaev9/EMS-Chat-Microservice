package com.project.chat.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chat.dto.IdDTO;
import com.project.chat.dto.UserDTO;
import com.project.chat.service.PersonService;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {
    private final PersonService personService;

    public UserConsumer(PersonService personService) {
        this.personService = personService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(String message) {
        JSONObject jsonMessage = new JSONObject(message);
        String action = jsonMessage.getString("action");

        if ("insert".equals(action)) {
            JSONObject userJson = jsonMessage.getJSONObject("user");
            UserDTO userEntry = null;
            try {
                userEntry = new ObjectMapper().readValue(userJson.toString(), UserDTO.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println("Received User Entry: " + userEntry.toString());
            personService.insert(userEntry);
        } else if("delete".equals(action)) {
            JSONObject userJson = jsonMessage.getJSONObject("user_id");
            IdDTO simpleDTO = null;
            try {
                simpleDTO = new ObjectMapper().readValue(userJson.toString(), IdDTO.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println("Received id: " + simpleDTO.getId());
            personService.delete(simpleDTO.getId());
        } else if("update".equals(action)){
            JSONObject userJson = jsonMessage.getJSONObject("user");
            UserDTO userEntry = null;
            try {
                userEntry = new ObjectMapper().readValue(userJson.toString(), UserDTO.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println("Received User Entry: " + userEntry.toString());
            personService.update(userEntry);
        }
    }

}
