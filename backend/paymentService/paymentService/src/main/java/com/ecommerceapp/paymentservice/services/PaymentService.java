package com.ecommerceapp.paymentservice.services;

import com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.PaymentGatewayAdapter;
import com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.PaymentGatewayStrategy;
import com.ecommerceapp.paymentservice.models.carts_orders.Orders;
import com.ecommerceapp.paymentservice.models.payments.Payment;
import com.ecommerceapp.paymentservice.models.payments.PaymentGateway;
import com.ecommerceapp.paymentservice.models.payments.PaymentStatus;
import com.ecommerceapp.paymentservice.repositories.OrderRepository;
import com.ecommerceapp.paymentservice.repositories.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    private static PaymentGatewayStrategy paymentGatewayStrategy;
    private static OrderService orderService;
    private static PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private KafkaService kafkaService;

    public PaymentService(PaymentGatewayStrategy paymentGatewayStrategy,
                          OrderService orderService,
                          PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          KafkaService kafkaService) {
        this.paymentGatewayStrategy = paymentGatewayStrategy;
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.kafkaService = kafkaService;
    }
    public String createPaymentLink(Long cartId, Long userId)throws Exception{
        //call method to create an order
        Orders order = orderService.createOrder(cartId, userId);
        String paymentLink = "";
        // choose 1 payment gateway from a *runtime* strategy based on time or money etc
        // call the payment gateway api to create a payment link
        PaymentGatewayAdapter paymentGatewayAdapter = paymentGatewayStrategy.getPaymentGatewayAdapter();
        try{
            paymentLink = paymentGatewayAdapter.createPaymentLink(order);
        }
        catch (Exception ex){
            ex.printStackTrace(); // update later
        }




        return paymentLink;

    }

    public void updatePaymentStatus(String orderId, String userId, PaymentStatus paymentStatus, PaymentGateway paymentGateway)throws Exception {
        //TODO
        // restrict multiple payments on the same order and userId
        try{
            Payment payment = new Payment();
            Optional<Orders> ordersOptional = orderRepository.findById(Long.valueOf(orderId));
            Orders order = ordersOptional.get();
            payment.setOrder(order);
            payment.setAmount(order.getTotalPrice());
            payment.setUserId(Long.valueOf(userId));
            payment.setPaymentStatus(paymentStatus);
            payment.setPaymentGateway(paymentGateway);
            paymentRepository.save(payment);

            //send order details to inventoryservice via kafka
            if(paymentStatus == PaymentStatus.SUCCESS){
                ObjectMapper Obj = new ObjectMapper();
                String jsonOrder = Obj.writeValueAsString(order);

                kafkaService.sendOrderDetailsToKafka(jsonOrder);
            }
        }catch (Exception ex){
            throw new Exception("Error updating payment status: " + ex.getMessage());
        }



    }


}
