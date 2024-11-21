package com.example.reservation_system.services;

import com.example.reservation_system.models.PasswordChangeRequest;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Transactional
    public Map<String, String> updateUser(Long id, User userDetails) {
        Map<String, String> errors = new HashMap<>();

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            errors.put("error", "User not found.");
            return errors;
        }

        User existingUser = existingUserOpt.get();

        // Custom Validation, because model level @Validated validation reguires full user in requestBody
        // Username: Check if provided and if it's unique
        if (StringUtils.hasText(userDetails.getUsername()) &&
                !userDetails.getUsername().equals(existingUser.getUsername())) {

            if (usernameExists(userDetails.getUsername())) {
                errors.put("username", "Uživatelské jméno je již obsazené.");
            } else {
                existingUser.setUsername(userDetails.getUsername());
            }
        }
        // Email: Validate format and uniqueness if updated
        if (StringUtils.hasText(userDetails.getEmail()) &&
                !userDetails.getEmail().equals(existingUser.getEmail())) {
            if (!userDetails.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errors.put("email", "Neplatný formát emailu.");
            } else if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                errors.put("email", "Email je již registrován.");
            } else {
                existingUser.setEmail(userDetails.getEmail());
            }
        }
        // First Name: Check format
        if (StringUtils.hasText(userDetails.getFirstName())) {
            if (!userDetails.getFirstName().matches("^[A-Z][a-z]*$")) {
                errors.put("firstName", "Jméno musí začínat velkým písmenem a obsahovat pouze písmena.");
            } else {
                existingUser.setFirstName(userDetails.getFirstName());
            }
        }
        // Surname: Check format
        if (StringUtils.hasText(userDetails.getSurname())) {
            if (!userDetails.getSurname().matches("^[A-Z][a-z]*$")) {
                errors.put("surname", "Příjmení musí začínat velkým písmenem a obsahovat pouze písmena.");
            } else {
                existingUser.setSurname(userDetails.getSurname());
            }
        }
        // Phone Number: Ensure it's exactly 9 digits
        if (StringUtils.hasText(userDetails.getPhoneNumber())) {
            if (!userDetails.getPhoneNumber().matches("^[0-9]{9}$")) {
                errors.put("phoneNumber", "Telefonní číslo musí obsahovat přesně 9 číslic.");
            } else {
                existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            }
        }
        // Role: Only allow certain roles
        if (StringUtils.hasText(userDetails.getRole())) {
            if (!userDetails.getRole().matches("^(USER|MANAGER|ADMIN)$")) {
                errors.put("role", "Neplatná role.");
            } else {
                existingUser.setRole(userDetails.getRole());
            }
        }
        // Save user if there are no validation errors
        if (errors.isEmpty()) {
            userRepository.save(existingUser);
        }
        return errors;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public String changePassword(Long userId, @Valid PasswordChangeRequest passwordChangeRequest, String authenticatedUsername) {
        // Validate if the authenticated user matches the userId
        User authenticatedUser = findByUsername(authenticatedUsername);
        if (!authenticatedUser.getId().equals(userId)) {
            throw new IllegalStateException("Neoprávněný pokus o změnu hesla.");
        }

        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Uživatel nenalezen."));

        // Verify the old password
        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Staré heslo je nesprávné.");
        }

        // Encode and set the new password
        String encodedNewPassword = passwordEncoder.encode(passwordChangeRequest.getNewPassword());
        user.setPassword(encodedNewPassword);

        // Save the updated user
        userRepository.save(user);

        return "Heslo úspěšně změněno.";
    }


    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
