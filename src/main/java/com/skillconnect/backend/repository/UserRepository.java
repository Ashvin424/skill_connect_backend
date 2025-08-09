package com.skillconnect.backend.repository;

import com.skillconnect.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


    // This interface will automatically inherit methods for CRUD operations
    // and can be extended with custom query methods if needed.
}
