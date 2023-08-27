package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.EmailVerificationToken;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Unit tests for MailSenderService")
class MailSenderServiceTest {
    @InjectMocks
    private MailSenderService mailSenderService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MessageSourceService messageSourceService;

    private final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    private final MimeMessage mimeMessage = new MimeMessage((Session) null);

    private final User user = Instancio.create(User.class);

    private final EmailVerificationToken emailVerificationToken = Instancio.create(EmailVerificationToken.class);

    @BeforeEach
    void setUp() throws MessagingException {
        MockitoAnnotations.openMocks(this);
        user.setEmailVerificationToken(emailVerificationToken);

        javaMailSender.setHost("localhost");
        javaMailSender.setPort(25);

        mimeMessage.setSubject("Email Verification");
        mimeMessage.setText("Email content");
        mimeMessage.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());

        lenient().when(templateEngine.process(anyString(), any(Context.class))).thenReturn("Email content");
        lenient().when(messageSourceService.get("email_verification")).thenReturn("Email Verification");
    }

    @Nested
    @DisplayName("Test class for sendUserEmailVerification scenarios")
    class SendUserEmailVerificationTest {
        private final String subject = "Email Verification";

        @BeforeEach
        void setUp() {
            lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        }

        @Test
        @DisplayName("Test sendUserEmailVerification scenario")
        void given_whenSendUserEmailVerification_thenAssertBody() {
            // Given
            when(messageSourceService.get("email_verification")).thenReturn(subject);
            doNothing().when(mailSender).send(any(MimeMessage.class));
            // When
            mailSenderService.sendUserEmailVerification(user);
            // Then
            verify(templateEngine, times(1)).process(eq("mail/user-email-verification"),
                any(Context.class));
            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("Test sendUserEmailVerification throw exception scenario")
        void given_whenSendUserEmailVerification_thenVerifyErrorHandling() {
            // Given
            when(messageSourceService.get("email_verification")).thenReturn(subject);
            doThrow(new MailSendException("Mail send failed")).when(mailSender).send(mimeMessage);
            // When
            assertDoesNotThrow(() -> mailSenderService.sendUserEmailVerification(user));
            // Then
            // Verify that the error is handled gracefully in sendUserEmailVerification
        }
    }

    @Nested
    @DisplayName("Test class for sendUserPasswordReset scenarios")
    class SendUserPasswordResetTest {
        private final String subject = "Reset Password";

        @BeforeEach
        void setUp() {
            lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        }

        @Test
        @DisplayName("Test sendUserPasswordReset scenario")
        void given_whenSendUserPasswordReset_thenAssertBody() {
            // Given
            when(messageSourceService.get("password_reset")).thenReturn(subject);
            doNothing().when(mailSender).send(any(MimeMessage.class));
            // When
            mailSenderService.sendUserPasswordReset(user);
            // Then
            verify(templateEngine, times(1)).process(eq("mail/user-reset-password"),
                any(Context.class));
            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("Test sendUserPasswordReset throw exception scenario")
        void given_whenSendUserPasswordReset_thenVerifyErrorHandling() {
            // Given
            when(messageSourceService.get("password_reset")).thenReturn(subject);
            doThrow(new MailSendException("Mail send failed")).when(mailSender).send(mimeMessage);
            // When
            assertDoesNotThrow(() -> mailSenderService.sendUserPasswordReset(user));
            // Then
            // Verify that the error is handled gracefully in sendUserEmailVerification
        }
    }
}
