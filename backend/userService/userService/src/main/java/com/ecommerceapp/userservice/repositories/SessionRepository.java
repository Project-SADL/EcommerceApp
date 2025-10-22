package com.ecommerceapp.userservice.repositories;

import com.ecommerceapp.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session save(Session session);
    Optional<Session> findById(Long id);


    Optional<Session> findByToken(String token);

}
