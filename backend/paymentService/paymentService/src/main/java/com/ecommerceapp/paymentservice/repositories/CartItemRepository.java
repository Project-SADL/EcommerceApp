package com.ecommerceapp.paymentservice.repositories;

import com.ecommerceapp.paymentservice.models.carts_orders.Cart;
import com.ecommerceapp.paymentservice.models.carts_orders.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
   List<CartItem> findByCart(Cart cart);
//   CartItem save(CartItem cartItem);

    @Override
    <S extends CartItem> S save(S entity);

    List<CartItem> findByCartAndSku(Cart cart, String sku);

    @Override
    <S extends CartItem> List<S> saveAll(Iterable<S> entities);

    void deleteByCart(Cart cart);
}
