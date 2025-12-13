package com.grimore.service;

import com.grimore.dto.request.LoginRequestDTO;
import com.grimore.dto.response.LoginResponseDTO;
import com.grimore.model.Student;
import com.grimore.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public LoginResponseDTO authenticate(LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

        Authentication authentication = authenticationManager.authenticate(authToken);

        Student student = (Student) authentication.getPrincipal();
        String token = tokenService.generateToken(student);

        return new LoginResponseDTO(
                token,
                student.getFullName(),
                student.getEmail()
        );
    }
}