package com.thymeleaf.contactmanagement.service;

//public class EmailService {
//}


import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    public Boolean sendEmail(String subject,String message,String to){

        Boolean flag = false;

        String from = "alirazamemon.ads@gmail.com";
        String email = "alirazamemon.ads";
        String password = "yhfbvvdsdmxwyqbz";

        String host = "smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES"+properties);

        //setting important informationn to properties object
        //host set
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        //Step 1: to get the session object..
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);
            }
        });

        session.setDebug(true);

        //Step 2: Compose the message
        MimeMessage m = new MimeMessage(session);

        try{
            /*from email*/
            m.setFrom(from);
            /*adding recipient to message*/
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //adding subject to message
            m.setSubject(subject);
            //adding text to message
            /*m.setText(message);*/
            m.setContent(message,"text/html");
            //Step 3 : send the message using Transport class
            Transport.send(m);
            System.out.println("Send success...");
            flag = true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

}
