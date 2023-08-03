package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.Role;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.RoleRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for RoleService")
class RoleServiceTest {
    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MessageSourceService messageSourceService;

    private final Role role = Instancio.create(Role.class);

    @Nested
    @DisplayName("Test class for count scenarios")
    class CountTest {
        @Test
        @DisplayName("Happy path")
        void given_whenCount_thenAssertBody() {
            // Given
            when(roleRepository.count()).thenReturn(1L);
            // When
            Long count = roleService.count();
            // Then
            assertEquals(1L, count);
        }
    }

    @Nested
    @DisplayName("Test class for findByName scenarios")
    class FindAllByNameTest {
        @Test
        @DisplayName("Happy path")
        void given_whenFindByName_thenAssertBody() {
            // Given
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
            // When
            Role result = roleService.findByName(role.getName());
            // Then
            assertNotNull(result);
            assertEquals(role.getId(), result.getId());
            assertEquals(role.getName(), result.getName());
        }

        @Test
        @DisplayName("Not found role path")
        void given_whenFindByName_thenThrowNotFoundException() {
            // Given
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());
            // When
            Executable executable = () -> roleService.findByName(role.getName());
            // Then
            NotFoundException notFoundException = assertThrows(NotFoundException.class, executable);
            assertEquals(messageSourceService.get("role_not_found"), notFoundException.getMessage());
        }
    }

    @Nested
    @DisplayName("Test class for create scenarios")
    class CreateTest {
        @Test
        @DisplayName("Happy path")
        void given_whenCreate_thenAssertBody() {
            // Given
            when(roleRepository.save(role)).thenReturn(role);
            // When
            Role result = roleService.create(role);
            // Then
            assertNotNull(result);
            assertEquals(role.getId(), result.getId());
            assertEquals(role.getName(), result.getName());
        }
    }

    @Nested
    @DisplayName("Test class for save list scenarios")
    class SaveListTest {
        @Test
        @DisplayName("Happy path")
        void given_whenSaveList_thenAssertBody() {
            // Given
            List<Role> roles = List.of(role);
            when(roleRepository.saveAll(roles)).thenReturn(roles);
            // When
            List<Role> result = roleService.saveList(List.of(role));
            // Then
            assertNotNull(result);
            assertEquals(role.getId(), result.get(0).getId());
            assertEquals(role.getName(), result.get(0).getName());
        }
    }
}
