package com.example.transportionservice.dao;

import com.example.transportionservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemDao extends JpaRepository<CartItem, Integer> {
}
