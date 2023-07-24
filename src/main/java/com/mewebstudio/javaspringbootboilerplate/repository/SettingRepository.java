package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettingRepository extends JpaRepository<Setting, UUID> {
}
