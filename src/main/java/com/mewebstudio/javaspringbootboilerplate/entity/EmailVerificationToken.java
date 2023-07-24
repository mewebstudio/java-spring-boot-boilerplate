package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.EMAIL_VERIFICATION_TOKEN_LENGTH;

@Entity
@Table(name = "email_verification_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"token"}, name = "uk_email_verification_tokens_token")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken extends AbstractBaseEntity {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_email_verification_tokens_user_id")
    )
    private User user;

    @Column(name = "token", nullable = false, length = EMAIL_VERIFICATION_TOKEN_LENGTH)
    private String token;

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
}
