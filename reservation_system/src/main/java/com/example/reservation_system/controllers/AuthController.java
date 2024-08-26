package com.example.reservation_system.controllers;

import com.example.reservation_system.models.User;
import com.example.reservation_system.security.JwtUtil;
import com.example.reservation_system.services.MyUserDetailsService;
import com.example.reservation_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Přihlašovací jméno je vyžadováno");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Heslo je vyžadováno");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email je vyžadován");
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Jméno je vyžadováno.");
        }

        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Příjmeníje vyžadováno.");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Telefonní číslo je vyžadováno");
        }

        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email je již zabrán.");
        }
        userService.createUser(user);
        return ResponseEntity.ok("Uživatel zaregistrován úspěšně.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(jwt);
    }
}
