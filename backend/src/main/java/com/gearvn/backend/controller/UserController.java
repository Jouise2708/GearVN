package com.gearvn.backend.controller;

import com.gearvn.backend.entity.User;
import com.gearvn.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository repo;

    @GetMapping("/test")
    public String test() {
        return "OK";
    }

    @GetMapping
    public List<User> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public User create(@RequestBody User user) {

        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new RuntimeException("Thiếu email hoặc password");
        }

        return repo.save(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id,
                       @RequestBody User user) {

        User existing = repo.findById(id).orElse(null);

        if (existing == null) {
            throw new RuntimeException("User không tồn tại");
        }

        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getPassword() != null) existing.setPassword(user.getPassword());
        if (user.getName() != null) existing.setName(user.getName());

        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        if (!repo.existsById(id)) {
            return "User không tồn tại";
        }

        repo.deleteById(id);
        return "Deleted";
    }
}