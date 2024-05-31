package com.thymeleaf.contactmanagement.controller;

import com.thymeleaf.contactmanagement.dao.ContactRepository;
import com.thymeleaf.contactmanagement.dao.UserRepository;
import com.thymeleaf.contactmanagement.entities.Contact;
import com.thymeleaf.contactmanagement.entities.User;
import com.thymeleaf.contactmanagement.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.awt.print.Printable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    //method for adding common data to response
    @ModelAttribute
    public void addCommonDate(Model model, Principal principal){

        String userName =  principal.getName();
        System.out.println("Username "+userName);
        User user = userRepository.getUserByUserName(userName);
        System.out.println("User "+user);
        model.addAttribute("user", user);

    }

    //dashboard home
    @GetMapping("/index")
    public String dashboard(Model model, Principal principal){

        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    // open add form handler

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }


//    processing add contact
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage")MultipartFile file
                                 , Principal principal,HttpSession session){

        try{
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            //processing and uploading file...

            if(file.isEmpty()) {
                System.out.println("File is empty");
                contact.setImage("contact.png");
            }
            else{
                //uploading file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepository.save(user);


            System.out.println("Data "+contact);
            System.out.println("Added to data base");

            //message success
            session.setAttribute("message",new Message("Your content is added ! add more", "success"));

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error "+e.getMessage());
            //message error
            session.setAttribute("message", new Message("Something went wrong. Try again","danger"));
        }

        return "normal/add_contact_form";

    }

    //show contacts handler
    //example (per page =5)
    //current page =0 [page]
    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal){

        model.addAttribute("title","Show User Contacts");

        //contact ki list ko send karna hai

//        String userName = principal.getName();
//        User user = this.userRepository.getUserByUserName(userName);
//        List<Contact> contacts =   user.getContacts();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page,5);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);

        model.addAttribute("contacts",contacts);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",contacts.getTotalPages());

        return "normal/show_contacts";
    }

    //showing particular contact details.

    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal){

        System.out.println("CID"+cId);

        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);
        Contact contact = this.contactRepository.findById(cId).get();

        if(user.getId()==contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title",contact.getName());
        }

        return "normal/contact_detail";
    }


    //delete contact handler

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,Model model, HttpSession session, Principal principal){

       Contact contact = this.contactRepository.findById(cId).get();
//
//       contact.setUser(null);
        //Check.. Assignment

        User user = this.userRepository.getUserByUserName(principal.getName());

        user.getContacts().remove(contact);
        this.userRepository.save(user);

        //remove image
        //content.getImage();

//       this.contactRepository.delete(contact);



       //contact deleted successfully

        // then image should also be deleted


        session.setAttribute("message", new Message("Contact deleted successfully", "success"));

       return "redirect:/user/show_contacts/0";
    }

    //open update form handler
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model model){

        model.addAttribute("title","Update Contact");

        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact",contact);
        return "normal/update_form";
    }

    //update contact handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
                                Model model, HttpSession session,Principal principal){

        try{

            Contact oldContactDetail =this.contactRepository.findById(contact.getCId()).get();

            if(!file.isEmpty()){
                //file work...
                //rewrite

                //delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile,oldContactDetail.getImage());
                file1.delete();



                //update new photo
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());


            }else{
                //set old pic
                contact.setImage(oldContactDetail.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message",new Message("Your contact is updated...","success"));
        }catch(Exception ex){

        }



        System.out.println("Contact Name :"+contact.getName());
        System.out.println("Contact Id :"  +contact.getCId());
        return "redirect:/user/"+contact.getCId()+"/contact";
    }



    //your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model){

        model.addAttribute("title","Profile Page");
        return "normal/profile";
    }


    //open settings handler
    @GetMapping("/settings")
    public String openSettings(){

        return "normal/settings";
    }


    //change password handler...
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword")String oldPassword,
                                 @RequestParam("newPassword")String newPassword,
                                 Principal principal,
                                 HttpSession session ){

        System.out.println("OLD PASSWORD"+oldPassword);
        System.out.println("NEW PASSWORD"+newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())){
            //change the password

            currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Your password is successfully changed","success"));

        }else{
            //error
            session.setAttribute("message", new Message("Please correct old password","danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/index";
    }


}
