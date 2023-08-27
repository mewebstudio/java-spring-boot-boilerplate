package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class MailSenderService {
    private static final String NAME = "name";

    private static final String LAST_NAME = "lastName";

    private static final String URL = "url";

    private final String appName;

    private final String appUrl;

    private final String frontendUrl;

    private final String senderAddress;

    private final MessageSourceService messageSourceService;

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    /**
     * Mail sender service constructor.
     *
     * @param appName              String name of the application name
     * @param appUrl               String url of the application url
     * @param frontendUrl          String url of the frontend url
     * @param senderAddress        String email address of the sender
     * @param messageSourceService MessageSourceService
     * @param mailSender           JavaMailSender
     * @param templateEngine       SpringTemplateEngine
     */
    public MailSenderService(
        @Value("${spring.application.name}") String appName,
        @Value("${app.url}") String appUrl,
        @Value("${app.frontend-url}") String frontendUrl,
        @Value("${spring.mail.username}") String senderAddress,
        MessageSourceService messageSourceService,
        JavaMailSender mailSender,
        SpringTemplateEngine templateEngine
    ) {
        this.appName = appName;
        this.appUrl = appUrl;
        this.frontendUrl = frontendUrl;
        this.senderAddress = senderAddress;
        this.messageSourceService = messageSourceService;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send user email verification link.
     *
     * @param user User
     */
    public void sendUserEmailVerification(User user) {
        try {
            log.info(String.format("[EmailService] Sending verification e-mail: %s - %s - %s",
                user.getId(), user.getEmail(), user.getEmailVerificationToken().getToken()));

            String url = String.format("%s/auth/email-verification/%s", frontendUrl,
                user.getEmailVerificationToken().getToken());

            final Context ctx = createContext();
            ctx.setVariable(NAME, user.getName());
            ctx.setVariable(LAST_NAME, user.getLastName());
            ctx.setVariable("fullName", user.getFullName());
            ctx.setVariable(URL, url);

            String subject = messageSourceService.get("email_verification");
            send(new InternetAddress(senderAddress, appName), new InternetAddress(user.getEmail(), user.getName()),
                subject, templateEngine.process("mail/user-email-verification", ctx));

            log.info(String.format("[EmailService] Sent verification e-mail: %s - %s",
                user.getId(), user.getEmail()));
        } catch (UnsupportedEncodingException | MessagingException | MailException e) {
            log.error(String.format("[EmailService] Failed to send verification e-mail: %s", e.getMessage()));
        }
    }

    /**
     * Send user password reset link.
     *
     * @param user User
     */
    public void sendUserPasswordReset(User user) {
        try {
            log.info(String.format("[EmailService] Sending reset password e-mail: %s - %s - %s",
                user.getId(), user.getEmail(), user.getPasswordResetToken().getToken()));

            String url = String.format("%s/auth/password/%s", frontendUrl,
                user.getPasswordResetToken().getToken());

            final Context ctx = createContext();
            ctx.setVariable(NAME, user.getName());
            ctx.setVariable(LAST_NAME, user.getLastName());
            ctx.setVariable("fullName", user.getFullName());
            ctx.setVariable(URL, url);

            String subject = messageSourceService.get("password_reset");
            send(new InternetAddress(senderAddress, appName), new InternetAddress(user.getEmail(), user.getName()),
                subject, templateEngine.process("mail/user-reset-password", ctx));

            log.info(String.format("[EmailService] Sent reset password e-mail: %s - %s",
                user.getId(), user.getEmail()));
        } catch (UnsupportedEncodingException | MessagingException | MailException e) {
            log.error(String.format("[EmailService] Failed to send reset password e-mail: %s", e.getMessage()));
        }
    }

    /**
     * Create context for template engine.
     *
     * @return Context
     */
    private Context createContext() {
        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("SENDER_ADDRESS", senderAddress);
        ctx.setVariable("APP_NAME", appName);
        ctx.setVariable("APP_URL", appUrl);
        ctx.setVariable("FRONTEND_URL", frontendUrl);

        return ctx;
    }

    /**
     * Send an e-mail to the specified address.
     *
     * @param from    Address who sent
     * @param to      Address who receive
     * @param subject String subject
     * @param text    String message
     * @throws MessagingException when sending fails
     */
    private void send(InternetAddress from,
                      InternetAddress to,
                      String subject,
                      String text) throws MessagingException, MailException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, true);

        mailSender.send(mimeMessage);
    }
}
