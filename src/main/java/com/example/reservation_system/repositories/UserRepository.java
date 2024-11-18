package com.example.reservation_system.repositories;

import com.example.reservation_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Custom method to update the password (use JPQL or native query as needed)
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("password") String newPassword);
}
