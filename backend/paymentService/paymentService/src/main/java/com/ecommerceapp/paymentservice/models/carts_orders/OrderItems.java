package com.ecommerceapp.paymentservice.models.carts_orders;

import com.ecommerceapp.paymentservice.models.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItems extends BaseModel {
    String sku;
    BigDecimal price;
    int quantity;
    @ManyToOne
    Orders orders;

}
