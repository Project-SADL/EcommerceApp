package com.ecommerceapp.paymentservice.models.carts_orders;

import com.ecommerceapp.paymentservice.models.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id"}) //for now we will only allow one cart per user
        }
)
public class Cart extends BaseModel {
    Long userId;
}
