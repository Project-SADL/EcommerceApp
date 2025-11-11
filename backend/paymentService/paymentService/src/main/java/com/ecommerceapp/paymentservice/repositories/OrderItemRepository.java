package com.ecommerceapp.paymentservice.repositories;

import com.ecommerceapp.paymentservice.models.carts_orders.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
}
