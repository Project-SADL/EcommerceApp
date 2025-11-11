package com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters;

import com.ecommerceapp.paymentservice.models.carts_orders.OrderItems;
import com.ecommerceapp.paymentservice.models.carts_orders.Orders;

import java.math.BigDecimal;

public interface PaymentGatewayAdapter {

    String createPaymentLink(Orders order) throws  Exception;
}
