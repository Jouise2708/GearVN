package com.gearvn.backend.controller;

import com.gearvn.backend.entity.User;
import com.gearvn.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public User login(@RequestBody User req) {

        if (req.getEmail() == null || req.getPassword() == null) {
            throw new RuntimeException("Thiếu email hoặc password");
        }

        return service.login(req.getEmail(), req.getPassword());
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        if (user.getEmail() == null || user.getPassword() == null) {
            throw new RuntimeException("Thiếu dữ liệu");
        }

        return service.register(user);
    }
}