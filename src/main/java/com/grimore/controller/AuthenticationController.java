package com.grimore.controller;

import com.grimore.dto.request.LoginRequestDTO;
import com.grimore.dto.request.RefreshTokenRequestDTO;
import com.grimore.dto.response.LoginResponseDTO;
import com.grimore.dto.response.TokenResponseDTO;
import com.grimore.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<@NonNull LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authenticationService.authenticate(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        TokenResponseDTO response = authenticationService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        authenticationService.logout(dto.refreshToken());
        return ResponseEntity.noContent().build();
    }

    // Forget Password
    // Reset Password
}
