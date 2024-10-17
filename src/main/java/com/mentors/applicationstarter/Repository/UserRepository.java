package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    User findByUUID(UUID userId);

}
