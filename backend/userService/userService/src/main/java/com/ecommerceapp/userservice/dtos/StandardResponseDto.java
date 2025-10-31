package com.ecommerceapp.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardResponseDto {
   private String message;
   private RequestStatus requestStatus;
}
