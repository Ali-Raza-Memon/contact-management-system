package com.thymeleaf.contactmanagement.controller;

//public class EmailController {
//}



import com.thymeleaf.contactmanagement.entities.Email;
import com.thymeleaf.contactmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.standard.expression.MessageExpression;

import javax.mail.MessagingException;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestBody Email request){
        System.out.println(request);
        emailService.sendEmail(request.getSubject(),request.getMessage(),request.getTo());
        return ResponseEntity.status(HttpStatus.OK).body("Done");
    }
}
