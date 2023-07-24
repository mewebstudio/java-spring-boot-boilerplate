package com.mewebstudio.javaspringbootboilerplate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController extends AbstractBaseController {
    @GetMapping
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Dashboard");
    }
}
