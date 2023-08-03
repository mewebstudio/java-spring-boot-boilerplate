package com.mewebstudio.javaspringbootboilerplate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@Tag("context")
@SpringBootTest
@DisplayName("Context test for MainApplication")
class MainApplicationTest {
    @Test
    @DisplayName("Context loads successfully")
    public void applicationContextLoadedTest() {
        try (ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class)) {
            // Given
            MockedStatic<SpringApplication> utilities = mockStatic(SpringApplication.class);
            utilities.when((MockedStatic.Verification) SpringApplication.run(MainApplication.class, new String[]{}))
                .thenReturn(null);
            // When
            MainApplication.main(new String[]{});
            // Then
            assertThat(SpringApplication.run(MainApplication.class)).isEqualTo(null);
        }
    }
}
