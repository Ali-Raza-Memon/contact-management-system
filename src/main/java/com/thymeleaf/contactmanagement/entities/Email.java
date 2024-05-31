package com.thymeleaf.contactmanagement.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Email {
    private String to;
    private String subject;
    private String message;

}
