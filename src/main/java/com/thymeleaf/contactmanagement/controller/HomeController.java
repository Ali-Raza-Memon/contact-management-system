package com.thymeleaf.contactmanagement.controller;
import com.thymeleaf.contactmanagement.dao.UserRepository;
import com.thymeleaf.contactmanagement.entities.User;
import com.thymeleaf.contactmanagement.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/" )
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    //handler for register user
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,
                               @RequestParam(value = "agreement",defaultValue = "false")boolean agreement,
                               Model model,
                                 HttpSession session){

        try{
            if(!agreement){
                System.out.println("You have not agreed the terms and conditions");
                throw new Exception("You have not agreed the terms and conditions");
            }

            if(result1.hasErrors()){
                System.out.println("ERROR "+result1.toString());
                model.addAttribute("user",user);
                return "signup";
            }


            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement :"+agreement);
            System.out.println("USER :"+user);

            User result = this.userRepository.save(user);
            model.addAttribute("user",new User());
            session.setAttribute("message", new Message("Successfully registered", "alert-success"));
            return "signup";

        }catch (Exception e){

            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong "+e.getMessage(),"alert-danger"));
            return "signup";
        }


    }

    @GetMapping("/signin")
    public String login(Model model){
        model.addAttribute("title", "Login -Smart Contact Management");
        return "login";
    }





}
