package com.gearvn.backend.service;

import com.gearvn.backend.entity.Product;
import com.gearvn.backend.entity.CartItem;
import com.gearvn.backend.repository.CartRepository;
import com.gearvn.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;

    public CartService(CartRepository cartRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    // ===== ADD =====
    public void add(Long userId, Long productId) {

        if (userId == null || productId == null) return;

        Product p = productRepo.findById(productId).orElse(null);
        if (p == null) return;

        CartItem existing = cartRepo.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            existing = new CartItem();
            existing.setUserId(userId);
            existing.setProductId(productId);
            existing.setQuantity(1);
        }

        cartRepo.save(existing);
    }

    // ===== ADD BY NAME =====
    public void addByName(String name, String priceStr) {

        if (name == null || priceStr == null) return;

        double price;

        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            throw new RuntimeException("Price không hợp lệ");
        }

        Product p = productRepo.findByName(name);

        if (p == null) {
            p = new Product();
            p.setName(name);
            p.setPrice(price);
            productRepo.save(p);
        }

        CartItem item = new CartItem();
        item.setUserId(1L);
        item.setProductId(p.getId());
        item.setQuantity(1);

        cartRepo.save(item);
    }

    // ===== GET CART =====
    public List<Map<String, Object>> getCart(Long userId) {

        List<Map<String, Object>> result = new ArrayList<>();

        if (userId == null) return result;

        List<CartItem> items = cartRepo.findByUserId(userId);

        for (CartItem item : items) {

            Product p = productRepo.findById(item.getProductId()).orElse(null);
            if (p == null) continue;

            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("productId", p.getId());
            map.put("name", p.getName());
            map.put("price", p.getPrice());
            map.put("quantity", item.getQuantity());

            result.add(map);
        }

        return result;
    }

    public void update(Long id, int quantity) {
        if (id == null || quantity < 0) return;

        cartRepo.findById(id).ifPresent(item -> {
            item.setQuantity(quantity);
            cartRepo.save(item);
        });
    }

    public void delete(Long id) {
        if (id != null) cartRepo.deleteById(id);
    }

    public void clear(Long userId) {
        if (userId == null) return;

        List<CartItem> items = cartRepo.findByUserId(userId);
        cartRepo.deleteAll(items);
    }
}