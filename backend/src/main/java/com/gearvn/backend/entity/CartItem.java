package com.gearvn.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private int quantity;

    public CartItem() {}

    // ===== GETTER =====
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {   
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    // ===== SETTER =====
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProductId(Long productId) {   // 🔥 BẮT BUỘC
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}