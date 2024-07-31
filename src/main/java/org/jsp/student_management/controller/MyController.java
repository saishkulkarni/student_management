package org.jsp.student_management.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsp.student_management.dto.MyUser;
import org.jsp.student_management.dto.Student;
import org.jsp.student_management.helper.AES;
import org.jsp.student_management.helper.HelperForSendingMail;
import org.jsp.student_management.repository.MyUserRerpository;
import org.jsp.student_management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@MultipartConfig
public class MyController {

    @Autowired
    MyUserRerpository userRerpository;

    @Autowired
    StudentRepository studentRepository;

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
            // mailHelper.sendEmail(myUser);
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

    @PostMapping("/login")
    public String login(HttpSession session, @RequestParam String email, @RequestParam String password, ModelMap map) {
        MyUser myUser = userRerpository.findByEmail(email);
        if (myUser == null) {
            map.put("failure", "Invalid email address");
            return "login.html";
        } else {
            if (password.equals(AES.decrypt(myUser.getPassword(), "123"))) {
                if (myUser.isVerified()) {
                    session.setAttribute("user", myUser);
                    map.put("success", "Login Success");
                    return "home.html";
                } else {
                    int otp = new Random().nextInt(100000, 1000000);
                    myUser.setOtp(otp);
                    System.out.println(otp);
                    // mailHelper.sendEmail(myUser);
                    userRerpository.save(myUser);
                    map.put("success", "Otp Sent Success, Check your Email");
                    map.put("id", myUser.getId());
                    return "enter-otp.html";
                }
            } else {
                map.put("failure", "Invalid Password");
                return "login.html";
            }

        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, ModelMap map) {
        session.removeAttribute("user");
        map.put("success", "Logout Success");
        return "home.html";
    }

    @GetMapping("/insert")
    public String insert(HttpSession session, ModelMap map) {
        if (session.getAttribute("user") != null) {
            return "insert.html";
        } else {
            map.put("failure", "Invalid session");
            return "login.html";
        }
    }

    @PostMapping("/insert")
    public String insert(Student student, HttpSession session, ModelMap map, @RequestParam MultipartFile image) {
        if (session.getAttribute("user") != null) {
            student.setPicture(addToCloudinary(image));
            studentRepository.save(student);
            map.put("success", "Record Saved Successfully");
            return "home.html";
        } else {
            map.put("failure", "Invalid session");
            return "login.html";
        }
    }

    public String addToCloudinary(MultipartFile image) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "djkyoabl5", "api_key",
                "297695696273364", "api_secret", "4bQWA8ZVWVftu83HUe57moGk5Q4", "secure", true));

        Map resume = null;
        try {
            Map<String, Object> uploadOptions = new HashMap<String, Object>();
            uploadOptions.put("folder", "Student Pictures");
            resume = cloudinary.uploader().upload(image.getBytes(), uploadOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (String) resume.get("url");
    }

    @GetMapping("/fetch")
    public String fetchAll(HttpSession session,ModelMap map){
        if (session.getAttribute("user") != null) {
            List<Student> list=studentRepository.findAll();
            if(list.isEmpty()){
                map.put("failure", "No Data Found");
                return "home.html";
            }
            else{
                map.put("list", list);
                return "fetch.html";
            }
        } else {
            map.put("failure", "Invalid session");
            return "login.html";
        }
    }

}
