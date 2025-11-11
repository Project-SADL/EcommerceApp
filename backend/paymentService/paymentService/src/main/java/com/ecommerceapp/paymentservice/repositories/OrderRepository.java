package com.ecommerceapp.paymentservice.repositories;

import com.ecommerceapp.paymentservice.models.carts_orders.OrderStatus;
import com.ecommerceapp.paymentservice.models.carts_orders.Orders;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByIdAndStatus(Long id, OrderStatus status);

    List<Orders> findByUserIdAndStatus(Long userId, OrderStatus status);

    @Query(value = "SELECT * FROM orders o WHERE " +
                                    "o.user_id = :userId " +
                                    "AND o.status = :status " +
                                    "AND o.total_price > 0 " +
                                    "LIMIT 1", nativeQuery = true)
    Orders findByUserIdAndStatusWithPositiveTotalPrice(@Param("userId") Long userId,
                                                       @Param("status") OrderStatus status);
}
