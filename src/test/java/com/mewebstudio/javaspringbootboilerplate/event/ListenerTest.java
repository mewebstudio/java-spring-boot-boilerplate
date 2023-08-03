package com.mewebstudio.javaspringbootboilerplate.event;

import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.service.MailSenderService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Unit tests for Listener class")
class ListenerTest {
    @InjectMocks
    private Listener listener;

    @Mock
    private MailSenderService mailSenderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test onUserEmailVerificationSendEvent method")
    void given_whenOnUserEmailVerificationSendEvent_thenAssertBody() {
        // Given
        User user = Instancio.create(User.class);
        UserEmailVerificationSendEvent event = new UserEmailVerificationSendEvent(this, user);
        // When
        listener.onUserEmailVerificationSendEvent(event);
        // Then
        verify(mailSenderService, times(1)).sendUserEmailVerification(user);
    }
}
