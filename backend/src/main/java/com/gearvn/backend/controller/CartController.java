package com.gearvn.backend.controller;

import com.gearvn.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired
    private CartService cartService;

    // ===== GET CART =====
    @GetMapping("/{userId}")
    public List<Map<String, Object>> getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    // ===== ADD =====
    @PostMapping("/add")
    public String add(@RequestBody Map<String, String> req) {

        String name = req.get("productName");
        String price = req.get("price");

        if (name == null || price == null) {
            return "Missing data";
        }

        cartService.addByName(name, price);
        return "OK";
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam int quantity) {

        cartService.update(id, quantity);
        return "Updated";
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        cartService.delete(id);
        return "Deleted";
    }

    // ===== CLEAR =====
    @DeleteMapping("/clear/{userId}")
    public String clear(@PathVariable Long userId) {
        cartService.clear(userId);
        return "Cleared";
    }
}