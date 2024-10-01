package com.example.reservation_system.services;

import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public int createUser(User user) {
        // Zakóduje heslo před uložením
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public int updateUser(Long id, User user) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Update only the fields that are not null
            if (user.getUsername() != null) {
                existingUser.setUsername(user.getUsername());
            }
            if (user.getPassword() != null) {
                existingUser.setPassword(user.getPassword());
            }
            if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }
            if (user.getEmail() != null) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getFirstName() != null) {
                existingUser.setFirstName(user.getFirstName());
            }
            if (user.getSurname() != null) {
                existingUser.setSurname(user.getSurname());
            }
            if (user.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(user.getPhoneNumber());
            }

            userRepository.update(existingUser);
            return 1;
        } else {
            return 0; // User not found, nothing to update
        }
    }


    public int deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Method to change the user password
    public void changeUserPassword(Long userId, String newPassword) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("User not found.");
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}
