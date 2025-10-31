package com.ecommerceapp.userservice.repositories;

import com.ecommerceapp.userservice.models.Session;
import com.ecommerceapp.userservice.models.SessionStatus;
import com.ecommerceapp.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session save(Session session);

    @Override
    <S extends Session> List<S> saveAll(Iterable<S> entities);

    Optional<Session> findById(Long id);

    Optional<Session> findByToken(String token);

    long countByUserAndStatus(User user, SessionStatus status);

    List<Session> findAllByUserAndStatus(User user, SessionStatus status);

//    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
//    Long countByUser(Long userId);

}
