package com.grimore.service;

import com.grimore.dto.request.LoginRequestDTO;
import com.grimore.dto.request.RefreshTokenRequestDTO;
import com.grimore.dto.response.LoginResponseDTO;
import com.grimore.dto.response.TokenResponseDTO;
import com.grimore.exception.auth.InvalidCredentialsException;
import com.grimore.exception.auth.TokenExpiredException;
import com.grimore.exception.validation.BadRequestException;
import com.grimore.model.RefreshToken;
import com.grimore.model.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponseDTO authenticate(LoginRequestDTO dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BadRequestException("Email é obrigatório");
        }
        if (dto.password() == null || dto.password().isBlank()) {
            throw new BadRequestException("Senha é obrigatória");
        }

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

            Authentication authentication = authenticationManager.authenticate(authToken);

            Student student = (Student) authentication.getPrincipal();

            if (!student.getActive()) {
                throw new InvalidCredentialsException("Esta conta está inativa");
            }

            String accessToken = tokenService.generateToken(student);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(student);

            log.info("User authenticated successfully: {}", student.getEmail());

            return new LoginResponseDTO(
                    accessToken,
                    refreshToken.getToken(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getRole(),
                    tokenService.getExpirationSeconds()
            );
        } catch (BadCredentialsException ex) {
            log.warn("Failed authentication attempt for email: {}", dto.email());
            throw new InvalidCredentialsException("Email ou senha inválidos");
        }
    }


    @Transactional
    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO dto) {
        if (dto.refreshToken() == null || dto.refreshToken().isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(dto.refreshToken());

            if (refreshToken.isRevoked()) {
                log.warn("Attempt to use revoked refresh token");
                throw new TokenExpiredException("Refresh token has been revoked");
            }

            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            Student student = refreshToken.getStudent();

            if (!student.getActive()) {
                throw new InvalidCredentialsException();
            }

            String newAccessToken = tokenService.generateToken(student);
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(student);

            log.info("Token refreshed successfully for user: {}", student.getEmail());

            return new TokenResponseDTO(
                    newAccessToken,
                    newRefreshToken.getToken(),
                    tokenService.getExpirationSeconds()
            );
        } catch (TokenExpiredException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error refreshing token", ex);
            throw new BadRequestException("Failed to refresh token");
        }
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        try {
            refreshTokenService.revokeToken(refreshToken);
            log.info("User logged out successfully");
        } catch (Exception ex) {
            log.error("Error during logout", ex);
            throw new BadRequestException("Failed to logout");
        }
    }
}