package org.jsp.student_management.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Entity
@Data
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int otp;
    private boolean verified;
    @Size(min = 3, max = 20, message = "* Enter between 3 and 20 characters")
    private String name;
    @Email(message = "* Enter Proper Email Format")
    @NotEmpty(message = "* This is Required")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
     message = "* Enter more than 8 characters Consisting of One Upper Case, One Lower Case, One Special Charecter, One Number")
    private String password;
}
