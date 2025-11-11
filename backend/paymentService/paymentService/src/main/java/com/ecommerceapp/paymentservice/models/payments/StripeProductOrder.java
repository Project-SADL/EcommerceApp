package com.ecommerceapp.paymentservice.models.payments;

//import jakarta.persistence.Entity;
import com.ecommerceapp.paymentservice.models.BaseModel;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
public class StripeProductOrder extends BaseModel {
//    private Long productId;
    private String sku;
    private String StripeProductId;
    private String StripePriceId;
}
