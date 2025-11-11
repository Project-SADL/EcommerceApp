package com.ecommerceapp.paymentservice.models.carts_orders;

import com.ecommerceapp.paymentservice.models.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cart_id", "sku"}) //to avoid duplicate items in the cart
        }
)
public class CartItem extends BaseModel {
    String sku;
    BigDecimal price;
    int quantity;
    @ManyToOne
    Cart cart;
}
