package com.ecommerceapp.paymentservice.models.payments;

import com.ecommerceapp.paymentservice.models.BaseModel;
import com.ecommerceapp.paymentservice.models.carts_orders.Orders;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Getter
@Setter
public class Payment extends BaseModel {
    BigDecimal amount;
    PaymentStatus paymentStatus;
    PaymentGateway paymentGateway;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;
    Long userId;
}
