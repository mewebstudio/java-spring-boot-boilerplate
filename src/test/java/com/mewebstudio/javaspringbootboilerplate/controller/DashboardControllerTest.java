package com.mewebstudio.javaspringbootboilerplate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for DashboardController")
class DashboardControllerTest {
    @InjectMocks
    private DashboardController dashboardController;

    @Test
    @DisplayName("Should return dashboard")
    void given_whenDashboard_thenAssertBody() {
        // When
        ResponseEntity<String> response = dashboardController.dashboard();
        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
