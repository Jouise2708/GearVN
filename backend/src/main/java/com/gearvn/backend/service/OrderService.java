package com.gearvn.backend.service;

import com.gearvn.backend.entity.Order;
import com.gearvn.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repo;

    public Order create(Long userId, String total) {
        Order o = new Order();
        o.setUserId(userId);
        o.setTotal(Double.parseDouble(total));
        o.setStatus("PENDING");
        return repo.save(o);
    }
}