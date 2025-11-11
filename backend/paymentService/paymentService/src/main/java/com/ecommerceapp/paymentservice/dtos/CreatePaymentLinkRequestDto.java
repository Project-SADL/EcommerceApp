package com.ecommerceapp.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentLinkRequestDto {
    private Long cartId;
    //I will not take price from frontend but fetch it from the orderService
}
