package com.thymeleaf.contactmanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="contact")
@Setter
@Getter
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer cId;
    private String name;
    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String image;
    @Column(length = 5000)
    private String description;
    @ManyToOne
    @JsonIgnore
    private User user;


    public boolean equals(Object object){
        return this.cId==((Contact)object).getCId();
    }


}
