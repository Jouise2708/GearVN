package com.gearvn.backend.controller;

import com.gearvn.backend.entity.Order;
import com.gearvn.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping("/create")
    public Order create(@RequestParam Long userId,
                        @RequestParam String total) {
        return service.create(userId, total);
    }
}