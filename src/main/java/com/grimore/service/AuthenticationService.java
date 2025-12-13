package com.grimore.service;

import com.grimore.dto.request.LoginRequestDTO;
import com.grimore.dto.request.RefreshTokenRequestDTO;
import com.grimore.dto.response.LoginResponseDTO;
import com.grimore.dto.response.TokenResponseDTO;
import com.grimore.model.RefreshToken;
import com.grimore.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponseDTO authenticate(LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

        Authentication authentication = authenticationManager.authenticate(authToken);

        Student student = (Student) authentication.getPrincipal();
        String accessToken = tokenService.generateToken(student);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(student);

        return new LoginResponseDTO(
                accessToken,
                refreshToken.getToken(),
                student.getFullName(),
                student.getEmail(),
                tokenService.getExpirationSeconds()
        );
    }

    @Transactional
    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO dto) {
        RefreshToken refreshToken = refreshTokenService.findByToken(dto.refreshToken());

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token foi revogado");
        }

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        Student student = refreshToken.getStudent();

        String newAccessToken = tokenService.generateToken(student);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(student);

        return new TokenResponseDTO(
                newAccessToken,
                newRefreshToken.getToken(),
                tokenService.getExpirationSeconds()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }
}