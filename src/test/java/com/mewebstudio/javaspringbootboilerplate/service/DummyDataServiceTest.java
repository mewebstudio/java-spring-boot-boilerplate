package com.mewebstudio.javaspringbootboilerplate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Unit tests for DummyDataService class")
class DummyDataServiceTest {
    @InjectMocks
    private DummyDataService dummyDataService;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test run method when no roles and users exist")
    void given_whenRunNoRolesAndUsers_thenAssertBody() throws Exception {
        // Given
        when(roleService.count()).thenReturn(0L);
        when(userService.count()).thenReturn(0L);
        // When
        dummyDataService.run();
        // Then
        verify(roleService, times(1)).count();
        verify(roleService, times(1)).saveList(anyList());
        verify(userService, times(1)).count();
        verify(userService, times(2)).create(any());
    }

    @Test
    @DisplayName("Test run method when roles and users exist")
    void given_whenRunRolesAndUsersExist_thenAssertBody() throws Exception {
        // Given
        when(roleService.count()).thenReturn(2L);
        when(userService.count()).thenReturn(2L);
        // When
        dummyDataService.run();
        // Then
        verify(roleService, times(1)).count();
        verify(roleService, never()).saveList(anyList());
        verify(userService, times(1)).count();
        verify(userService, never()).create(any());
    }
}
