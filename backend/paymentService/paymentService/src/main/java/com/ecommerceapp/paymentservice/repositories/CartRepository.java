package com.ecommerceapp.paymentservice.repositories;

import com.ecommerceapp.paymentservice.models.carts_orders.Cart;
import com.ecommerceapp.paymentservice.models.carts_orders.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
// Optional<List<Cart>> findByUserIdAndCartStatus(Long userId, CartStatus cartStatus);
    List<Cart> findByUserId(Long userId);

    Cart saveAndFlush(Cart cart);

    Cart save(Cart cart);
    // Optional<List<Cart>> findByUserId(Long userId);
}
