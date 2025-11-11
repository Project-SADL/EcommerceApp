package com.ecommerceapp.paymentservice.services;

import com.ecommerceapp.paymentservice.models.carts_orders.*;
import com.ecommerceapp.paymentservice.repositories.OrderItemRepository;
import com.ecommerceapp.paymentservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    // before create order, check if there is any pending order for this user..
    // only create new order if no pending order exists
    private CartService cartService;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    public OrderService(CartService cartService, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
    }
    @Transactional
    public Orders createOrder(Long cartId,Long userId) throws Exception{
        try {
            //fetch the cart
            Cart cart = cartService.getCartById(cartId);
            List<CartItem> cartItems = cartService.getCartItemsByCart(cart);
            Orders order;
            if(cartItems == null || cartItems.isEmpty()){
                //check for any pending order to be paid
                order = orderRepository.findByUserIdAndStatusWithPositiveTotalPrice(userId, OrderStatus.PENDING);
            }
            else{
                //create an order for this cart
                order = new Orders();
                order.setUserId(userId);
                order.setStatus(OrderStatus.PENDING);
                order.setTotalPrice(cartService.getTotalPrice(cart));
                order = orderRepository.saveAndFlush(order);
                saveOrderItems(cartItems, order, cart);
            }

            if(order == null){
                throw new Exception("Order could not be created.");
            }

            return order;

        }
        catch (Exception e){
            throw new Exception("Error creating order: " + e.getMessage());
        }
    }

    private void saveOrderItems(List<CartItem> cartItems, Orders order, Cart cart) throws Exception {
        List<OrderItems> orderItemsList = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItems orderItem = new OrderItems();
            orderItem.setSku(cartItem.getSku());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrders(order);
            orderItemsList.add(orderItem);
        }
        //create order items from cart items
        orderItemRepository.saveAll(orderItemsList);
        //delete all items in the cart
        cartService.deleteCartItems(cart);
    }

    public void updatePaymentStatus(String orderId, OrderStatus orderStatus) {
        Orders order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
        if(order != null){
            order.setStatus(orderStatus);
            orderRepository.save(order);
        }
    }


}
