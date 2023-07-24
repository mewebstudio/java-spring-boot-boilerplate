package com.mewebstudio.javaspringbootboilerplate.entity;

import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"}, name = "uk_roles_name")
}, indexes = {
    @Index(columnList = "name", name = "idx_roles_name")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AbstractBaseEntity {
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(
                name = "fk_user_roles_role_id",
                foreignKeyDefinition = "FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE"
            ),
            nullable = false
        ),
        inverseJoinColumns = @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(
                name = "fk_user_roles_user_id",
                foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"
            ),
            nullable = false
        ),
        uniqueConstraints = {
            @UniqueConstraint(
                columnNames = {"user_id", "role_id"},
                name = "uk_user_roles_user_id_role_id"
            )
        }
    )
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 16)
    @NaturalId
    private Constants.RoleEnum name;

    public Role(final Constants.RoleEnum name) {
        this.name = name;
    }
}
