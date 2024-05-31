package com.thymeleaf.contactmanagement.controller;

import com.thymeleaf.contactmanagement.dao.UserRepository;
import com.thymeleaf.contactmanagement.entities.User;
import com.thymeleaf.contactmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class ForgetController {
//    private Integer otp = ThreadLocalRandom.current().nextInt(1000, 100000);

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    Random random = new Random(1000);

    @GetMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email")String email, HttpSession session){

        System.out.println("Email "+email);
        //generating otp of 4 digit

        Integer otp = random.nextInt(99999);

        System.out.println("OTP : "+otp);
        //write code for send otp to email

        String subject = "OTP from Smart Contact Manager";
        String message = ""

                    +"<div style='border:1px solid #e2e2e2; padding:20px' >"
                    +"<h1>"
                    +"OTP is :"
                    +"<b>"+otp
                    +"</b>"
                    +"</h1>"
                    +"</div>"

                ;
        String to =email;

        Boolean flag = this.emailService.sendEmail(subject,message,to);
        if(flag){
            session.setAttribute("myOtp",otp);
            session.setAttribute("email",email);

            return "normal/verify_otp";
        }else{
            session.setAttribute("message","Please check your email id!");
            return "forgot_email_form";
        }

    }


    //verify otp

    @PostMapping("/verify_otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session){

        int myOtp =(int)session.getAttribute("myOtp");
        String email  =(String)session.getAttribute("email");
        if(myOtp == otp){

            //password change form

            User user =this.userRepository.getUserByUserName(email);

            if(user == null){
                // send error message
                session.setAttribute("message","User does not exist with this email!");
                return "forgot_email_form";

            }else{
                //send change password form


            }


            return "change_password";

        }else{
            session.setAttribute("message","You have entered wrong OTP");
            return "normal/verify_otp";
        }
    }


    //change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPasword,HttpSession session){
        String email =(String)session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPasword));
        this.userRepository.save(user);


        return "redirect:/signin?change=password change successfully";


    }




}
