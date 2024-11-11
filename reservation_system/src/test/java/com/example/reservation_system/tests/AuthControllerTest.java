package com.example.reservation_system.tests;

import com.example.reservation_system.controllers.AuthController;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.UserRepository;
import com.example.reservation_system.security.JwtUtil;
import com.example.reservation_system.services.MyUserDetailsService;
import com.example.reservation_system.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtTokenUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    public void setUp() throws Exception{
        // Initialize test user
        user = new User();
        user.setUsername("testuser");
        user.setPassword("correctpassword");

        userService = new UserService(userRepository, passwordEncoder);


        authController = new AuthController(userService, userRepository, authenticationManager, passwordEncoder,jwtTokenUtil,userDetailsService);

        userService = new UserService(userRepository, passwordEncoder);

        // Use reflection to inject jwtTokenUtil and userDetailsService
        injectPrivateField(authController, "userDetailsService", userDetailsService);
        injectPrivateField(userDetailsService, "userRepository", userRepository);
        // Use a real instance of JwtUtil
        jwtTokenUtil = new JwtUtil();
        injectPrivateField(authController, "jwtTokenUtil", jwtTokenUtil);

        // Mock behavior for userService.findByUsername
        User credentialsUser = new User();
        credentialsUser.setId(1L);
        credentialsUser.setRole("ROLE_USER");
        credentialsUser.setUsername("testuser");
        credentialsUser.setPassword("correctpassword");
        lenient().when(userService.findByUsername("testuser")).thenReturn(credentialsUser);
    }
    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testSuccessfulLogin() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // Mock authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));

        // Act
        ResponseEntity<Map<String, Object>> response = authController.loginUser(user);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.get("accessToken"));
        assertNotNull(responseBody.get("refreshToken"));
        assertEquals("ROLE_USER", responseBody.get("role"));
        assertEquals(1L, responseBody.get("userId"));
    }

    @Test
    public void testLoginWithIncorrectCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        ResponseEntity<Map<String, Object>> response = authController.loginUser(user);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Uživatelské jméno nebo heslo nejsou správné.", response.getBody().get("error"));
    }

    @Test
    public void testLoginUnexpectedError() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<Map<String, Object>> response = authController.loginUser(user);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Došlo k nečekané chybě. Zkuste to prosím znovu.", response.getBody().get("error"));
    }

    // Additional tests for other methods can be added here
}
