package nus.iss.wellness.backend.security;

import io.jsonwebtoken.Claims;
import nus.iss.wellness.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

//author: Junior

@SpringBootTest
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Test
    void shouldGenerateToken() {

        String token =
                jwtService.generateToken("Junior", "USER");

        Claims claims =
                jwtService.authenticateToken(token);

        assertEquals("Junior", claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }
}