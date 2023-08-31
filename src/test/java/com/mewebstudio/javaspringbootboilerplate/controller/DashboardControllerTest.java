package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for DashboardController")
class DashboardControllerTest {
    @InjectMocks
    private DashboardController dashboardController;

    @Mock
    private MessageSourceService messageSourceService;

    @Test
    @DisplayName("Should return dashboard")
    void given_whenDashboard_thenAssertBody() {
        // Given
        String message = "Hi";
        when(messageSourceService.get("hi")).thenReturn(message);
        // When
        ResponseEntity<String> response = dashboardController.dashboard();
        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
