package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.Role;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(Constants.RoleEnum name);
}
