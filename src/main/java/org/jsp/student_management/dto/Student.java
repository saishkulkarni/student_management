package org.jsp.student_management.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Student {

    private String name;
    private String standard;
    private LocalDate dob;
    private long mobile;
    private int subject1;
    private int subject2;
    private int subject3;
    private int subject4;
    private int subject5;
    private int subject6;
    private String picture;
}
