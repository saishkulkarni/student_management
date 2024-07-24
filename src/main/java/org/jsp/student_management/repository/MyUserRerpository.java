package org.jsp.student_management.repository;

import org.jsp.student_management.dto.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserRerpository extends JpaRepository<MyUser, Integer> {

    boolean existsByEmail(String email);

}
