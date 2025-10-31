package com.ecommerceapp.userservice.repositories;

import com.ecommerceapp.userservice.models.Role;
import com.ecommerceapp.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Override
    <S extends Role> List<S> saveAll(Iterable<S> entities);

    @Override
    <S extends Role> S save(S entity);
    
    Optional<Role> findByRole(String role);
}
