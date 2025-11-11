package com.ecommerceapp.paymentservice.dtos.cartDtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class GetCartResponseDto {
    private Long cartId;
    private List<GetCartItemResponseDto> items = new ArrayList<>();
    private BigDecimal totalPrice;
}
