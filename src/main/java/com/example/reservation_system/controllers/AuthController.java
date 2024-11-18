package com.example.reservation_system.controllers;

import com.example.reservation_system.models.PasswordChangeRequest;
import com.example.reservation_system.models.RefreshTokenRequest;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.UserRepository;
import com.example.reservation_system.security.JwtUtil;
import com.example.reservation_system.services.MyUserDetailsService;
import com.example.reservation_system.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtTokenUtil;
    private final MyUserDetailsService userDetailsService;

    public AuthController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtTokenUtil, MyUserDetailsService userDetailsService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }


    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        // Extract username from the authentication token
        String username = authentication.getName(); // This will get the username from JWT

        // Fetch user details based on username
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    // Get all users
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Transactional
    @PutMapping("user/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id,@Valid @RequestBody PasswordChangeRequest passwordChangeRequest, Authentication authentication) {
        // Validate if the user is authenticated and authorized to change their password
        User authenticatedUser = userService.findByUsername(authentication.getName());
        if (!authenticatedUser.getId().equals(id)) {
            return new ResponseEntity<>("Neoprávněný pokus o změnu hesla.", HttpStatus.FORBIDDEN);
        }
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Verify the old password
            if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Old password is incorrect.");
            }

            // Set new password
            String encodedNewPassword = passwordEncoder.encode(passwordChangeRequest.getNewPassword());
            userRepository.updatePassword(user.getId(), encodedNewPassword);

            return ResponseEntity.ok("Heslo úspěšně změněno.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Map<String, String> validationErrors = userService.updateUser(id, userDetails);
        if (validationErrors.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Uživatel byl úspěšně aktualizován."));
        } else {
            return ResponseEntity.badRequest().body(validationErrors);
        }
    }



    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return ResponseEntity.ok("Uživatel zaregistrován úspěšně.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            final String jwt = jwtTokenUtil.generateToken(userDetails);
            final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            User credentialsUser = userService.findByUsername(user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("refreshToken", refreshToken);

            response.put("role", credentialsUser.getRole());
            response.put("userId", credentialsUser.getId());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Uživatelské jméno nebo heslo nejsou správné."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Došlo k nečekané chybě. Zkuste to prosím znovu."));
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            // Validate the refresh token
            if (!validateRefreshToken(refreshTokenRequest.getRefreshToken())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Nevalidní refreshovací token.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Generate a new access token
            String newAccessToken = jwtTokenUtil.generateToken(getUserDetailsFromRefreshToken(refreshTokenRequest.getRefreshToken()));
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return ResponseEntity.ok(response);

        } catch (JwtException e) {
            // Handle JWT-specific exceptions
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid JWT token");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Log the error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error refreshing token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private boolean validateRefreshToken(String refreshToken) {
        try {
            // Verify the refresh token using the JwtUtil class
            jwtTokenUtil.extractAllClaims(refreshToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        // Extract the username from the refresh token
        String username = jwtTokenUtil.extractUsername(refreshToken);
        // Load the user details using the username
        return userDetailsService.loadUserByUsername(username);
    }
}

