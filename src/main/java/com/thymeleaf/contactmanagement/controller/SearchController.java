package com.thymeleaf.contactmanagement.controller;

import com.thymeleaf.contactmanagement.dao.ContactRepository;
import com.thymeleaf.contactmanagement.dao.UserRepository;
import com.thymeleaf.contactmanagement.entities.Contact;
import com.thymeleaf.contactmanagement.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
//returns json
public class SearchController {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    //search handler

    @GetMapping("/search/{query}")
    public ResponseEntity<?> Search(@PathVariable("query") String query, Principal principal){

        System.out.println(query);

        User user = this.userRepository.getUserByUserName(principal.getName());
        List<Contact> contact = this.contactRepository.findByNameContainingAndUser(query,user);
        return ResponseEntity.status(HttpStatus.OK).body(contact);
    }

}
