package com.project.chat.service;

import com.project.chat.dto.UserDTO;
import com.project.chat.dto.builder.UserBuilder;
import com.project.chat.model.Person;
import com.project.chat.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public UUID insert(UserDTO userDTO){
        Person newPerson = UserBuilder.fromDtoToUser(userDTO);
        if(newPerson.getRole() == "ADMIN"){
            newPerson.setStatus("ONLINE");
        }else{
            newPerson.setStatus("OFFLINE");
        }
        newPerson = personRepository.save(newPerson);
        return newPerson.getId();
    }

    @Transactional
    public void delete(UUID id){
        Optional<Person> optionalUser = personRepository.findById(id);
        optionalUser.ifPresent(personRepository::delete);
    }

    @Transactional
    public Person update(UserDTO userDTO){
        Optional<Person> optionalUser = personRepository.findById(userDTO.getId());
       if(!optionalUser.isPresent()){
           LOGGER.error("User to update not found!!!");
       }
        System.out.println("USer role to update: "+ userDTO.getRole());
        Person newPerson = UserBuilder.fromDtoToUser(userDTO);
        newPerson.setStatus(optionalUser.get().getStatus());
        newPerson = personRepository.save(newPerson);
        return newPerson;
    }

    @Transactional
    public void disconnect(Person user) {
        System.out.println("About to disconnect the user");
        Optional<Person> optionalPerson = personRepository.findById(user.getId());
        if(optionalPerson.isPresent()){
            Person personToDisconnect = optionalPerson.get();
            personToDisconnect.setStatus("OFFLINE");
            personRepository.save(personToDisconnect);
        } else {
            LOGGER.error("Person to disconnect was not found");
        }
    }
    @Transactional
    public void connect(Person user) {
        System.out.println("About to connect the user");
        Optional<Person> optionalPerson = personRepository.findById(user.getId());
        if(optionalPerson.isPresent()){
            Person personToConnect = optionalPerson.get();
            personToConnect.setStatus("ONLINE");
            personRepository.save(personToConnect);
        } else {
            LOGGER.error("Person to connect was not found");
        }
    }

    public List<Person> findConnectedUsers(){
        return personRepository.findAll();
    }
}
