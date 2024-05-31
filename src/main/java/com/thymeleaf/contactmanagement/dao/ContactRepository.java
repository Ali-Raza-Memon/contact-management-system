package com.thymeleaf.contactmanagement.dao;

import com.thymeleaf.contactmanagement.entities.Contact;
import com.thymeleaf.contactmanagement.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer>{
    //pagination...
        //CurrentPage-page
        //Contact Page -5
    @Query("SELECT contact FROM Contact contact WHERE contact.user.id=?1")
    Page<Contact> findContactsByUser(int userId, Pageable pageable);

    //search
    List<Contact> findByNameContainingAndUser(String name, User user);





}
