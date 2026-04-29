package com.gearvn.backend.service;

import com.gearvn.backend.entity.User;
import com.gearvn.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    public User login(String email, String password) {

        if (email == null || password == null) {
            throw new RuntimeException("Thiếu dữ liệu");
        }

        email = email.trim();
        password = password.trim();

        System.out.println("EMAIL: " + email);
        System.out.println("PASSWORD: " + password);

        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null) {
            throw new RuntimeException("Không tìm thấy user");
        }

        if (user.getPassword() == null) {
            throw new RuntimeException("Password DB null");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Sai mật khẩu");
        }

        return user;
    }

    public User register(User user) {

        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new RuntimeException("Thiếu thông tin");
        }

        user.setEmail(user.getEmail().trim());
        user.setPassword(user.getPassword().trim());

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        return userRepo.save(user);
    }
}