package com.mewebstudio.javaspringbootboilerplate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("context")
@SpringBootTest
@DisplayName("Context test for MainApplication")
class MainApplicationTest {
    @Test
    @DisplayName("Context loads successfully")
    public void applicationContextLoadedTest() {
        MainApplication.main(new String[]{});
    }
}
