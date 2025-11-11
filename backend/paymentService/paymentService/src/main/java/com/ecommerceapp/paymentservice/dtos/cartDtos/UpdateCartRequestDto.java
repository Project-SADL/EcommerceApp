package com.ecommerceapp.paymentservice.dtos.cartDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequestDto {
    List<GetCartItemResponseDto> items;
}
