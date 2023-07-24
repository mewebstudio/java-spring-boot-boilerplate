package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.request.user.CreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.Role;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DummyDataService implements CommandLineRunner {
    private final RoleService roleService;

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (roleService.count() == 0) {
            log.info("Creating roles...");
            createRoles();
            log.info("Roles created.");
        }

        if (userService.count() == 0) {
            log.info("Creating users...");
            createUsers();
            log.info("Users created.");
        }
    }

    /**
     * Create roles.
     */
    private void createRoles() {
        List<Role> roleList = new ArrayList<>();
        roleList.add(Role.builder().name(Constants.RoleEnum.ADMIN).build());
        roleList.add(Role.builder().name(Constants.RoleEnum.USER).build());

        roleService.saveList(roleList);
    }

    /**
     * Create users.
     *
     * @throws BindException Bind exception
     */
    private void createUsers() throws BindException {
        List<String> roleList = new ArrayList<>();
        roleList.add(Constants.RoleEnum.ADMIN.getValue());
        roleList.add(Constants.RoleEnum.USER.getValue());
        String defaultPassword = "P@sswd123.";

        userService.create(CreateUserRequest.builder()
            .email("admin@example.com")
            .password(defaultPassword)
            .name("John")
            .lastName("DOE")
            .roles(roleList)
            .isEmailVerified(true)
            .isBlocked(false)
            .build());

        userService.create(CreateUserRequest.builder()
            .email("user@example.com")
            .password(defaultPassword)
            .name("Jane")
            .lastName("DOE")
            .roles(List.of(roleList.get(1)))
            .isEmailVerified(true)
            .isBlocked(false)
            .build());
    }
}
