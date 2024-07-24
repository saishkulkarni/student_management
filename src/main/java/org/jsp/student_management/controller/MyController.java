package org.jsp.student_management.controller;

import java.util.Random;

import org.jsp.student_management.dto.MyUser;
import org.jsp.student_management.helper.AES;
import org.jsp.student_management.helper.HelperForSendingMail;
import org.jsp.student_management.repository.MyUserRerpository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
public class MyController {

    @Autowired
    MyUserRerpository userRerpository;

    @Autowired
    HelperForSendingMail mailHelper;

    @GetMapping("/")
    public String loadHome() {
        return "home.html";
    }

    @GetMapping("/signup")
    public String loadSignup(ModelMap map) {
        map.put("myUser", new MyUser());
        return "signup.html";
    }

    @GetMapping("/login")
    public String loadLogin() {
        return "login.html";
    }

    @PostMapping("/signup")
    public String signup(@Valid MyUser myUser, BindingResult result, ModelMap map) {
        if (userRerpository.existsByEmail(myUser.getEmail()))
            result.rejectValue("email", "error.email", "* Email Should be Unique");

        if (result.hasErrors())
            return "signup.html";
        else {
            int otp = new Random().nextInt(100000, 1000000);
            myUser.setOtp(otp);
            myUser.setPassword(AES.encrypt(myUser.getPassword(), "123"));
            System.out.println(myUser.getOtp());
            mailHelper.sendEmail(myUser);
            userRerpository.save(myUser);
            map.put("success", "Otp Sent Success, Check your Email");
            map.put("id", myUser.getId());
            return "enter-otp.html";
        }
    }

    @PostMapping("/verify-otp")
    public String verify(@RequestParam int id, @RequestParam int otp, ModelMap map) {
        MyUser myUser = userRerpository.findById(id).orElseThrow();
        if (myUser.getOtp() == otp) {
            myUser.setVerified(true);
            userRerpository.save(myUser);
            map.put("success", "Account Created successfully");
            return "home.html";
        } else {
            map.put("failure", "Invalid OTP, Try Again");
            map.put("id", myUser.getId());
            return "enter-otp.html";
        }
    }
}
