package com.example.reservation_system.tests.controllers;

import com.example.reservation_system.controllers.AuthController;
import com.example.reservation_system.models.PasswordChangeRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

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

    @Mock
    private Authentication authentication;

    private PasswordChangeRequest passwordChangeRequest;
    private User user;

    @BeforeEach
    public void setUp() throws Exception{
        // Initialize test user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("correctpassword");

        userService = new UserService(userRepository, passwordEncoder);


        authController = new AuthController(userService, authenticationManager, jwtTokenUtil,userDetailsService);

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

        passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setOldPassword("oldPassword");
        passwordChangeRequest.setNewPassword("newPassword");

        lenient().when(authentication.getName()).thenReturn("testuser");
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


    @Test
    void testUnauthorizedAccess() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);  // Different user ID
        when(userService.findByUsername("testuser")).thenReturn(user);

        // Act
        ResponseEntity<?> response = authController.changePassword(2L, passwordChangeRequest, authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Neoprávněný pokus o změnu hesla.", response.getBody());
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void testSuccessfulPasswordChange() {
        // Arrange
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setOldPassword("correctpassword");
        passwordChangeRequest.setNewPassword("newpassword");

        // Mock the authenticated user's context
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctpassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        // Act
        ResponseEntity<?> response = authController.changePassword(1L, passwordChangeRequest, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Heslo úspěšně změněno.", response.getBody());

        // Verify that the password was updated and the user saved
        verify(userRepository).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }


    @Test
    void testInvalidOldPassword() {
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("wrongpassword");
        request.setNewPassword("newpassword");

        ResponseEntity<?> response = authController.changePassword(1L, request, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Staré heslo je nesprávné.", response.getBody());
    }


    @Test
    void testUnauthorizedPasswordChange() {
        // Mock the authenticated user as "anotheruser"
        when(authentication.getName()).thenReturn("anotheruser");

        // Mock the repository to return a user with the ID 1L associated with "testuser"
        User actualUser = new User();
        actualUser.setId(1L);
        actualUser.setUsername("testuser");
        actualUser.setPassword("correctpassword");

        // Mock repository to return the user for findById
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(actualUser));

        // Prepare the password change request
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("correctpassword");
        request.setNewPassword("newpassword");

        // Perform the change password action
        ResponseEntity<?> response = authController.changePassword(1L, request, authentication);

        // Assert that unauthorized access is correctly handled
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Neoprávněný pokus o změnu hesla.", response.getBody());

        // Verify no password update is attempted
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }





}
