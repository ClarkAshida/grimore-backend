package com.grimore.security;

import com.grimore.model.Student;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Student getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Student) {
            return (Student) authentication.getPrincipal();
        }
        throw new IllegalStateException("Usuário não autenticado");
    }

    public static Integer getCurrentStudentId() {
        return getCurrentStudent().getId();
    }
}