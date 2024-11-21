package com.example.reservation_system.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "\"user\"", indexes = {
        @Index(name = "idx_user_first_name", columnList = "firstName"),
        @Index(name = "idx_user_surname", columnList = "surname")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Uživatelské jméno je povinné.")
    private String username;

    @NotBlank(message = "Heslo je povinné.")
    @Size(min = 8, message = "Heslo musí být alespoň 8 znaků dlouhé.")
    private String password;

    @NotBlank(message = "Email je povinný.")
    @Email(message = "Neplatný formát emailu.")
    private String email;

    @NotBlank(message = "Role je povinná.")
    private String role;

    @NotBlank(message = "Jméno je povinné.")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Jméno musí začínat velkým písmenem a obsahovat pouze písmena.")
    @Column(nullable = false) // Add nullable constraint for consistency
    private String firstName;

    @NotBlank(message = "Příjmení je povinné.")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Příjmení musí začínat velkým písmenem a obsahovat pouze písmena.")
    @Column(nullable = false) // Add nullable constraint for consistency
    private String surname;

    @NotBlank(message = "Telefonní číslo je povinné.")
    @Pattern(regexp = "^[0-9]{9}$", message = "Telefonní číslo musí obsahovat přesně 9 číslic.")
    private String phoneNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    // Default Constructor
    public User() {}

    // Constructor with all fields including id
    public User(Long id, String username, String password, String email, String role, String firstName, String surname, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }

    public User(long id) {
        this.id = id;
    }

    // Gettery a Settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

