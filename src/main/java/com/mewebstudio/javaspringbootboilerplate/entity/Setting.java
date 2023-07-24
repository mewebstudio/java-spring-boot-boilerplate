package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"key"}, name = "uk_settings_key")
}, indexes = {
    @Index(columnList = "value", name = "idx_settings_value")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting extends AbstractBaseEntity {
    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value", columnDefinition = "text")
    private String value;
}
