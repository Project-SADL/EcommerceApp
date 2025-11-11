package com.ecommerceapp.paymentservice.models.carts_orders;

import com.ecommerceapp.paymentservice.dtos.cartDtos.Status;
import com.ecommerceapp.paymentservice.models.BaseModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Orders extends BaseModel {
    Long userId;
    BigDecimal totalPrice;
    OrderStatus status;

}
