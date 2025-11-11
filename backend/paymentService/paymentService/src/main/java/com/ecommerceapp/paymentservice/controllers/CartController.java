package com.ecommerceapp.paymentservice.controllers;

import com.ecommerceapp.paymentservice.dtos.cartDtos.GetCartResponseDto;
import com.ecommerceapp.paymentservice.dtos.cartDtos.StandardResponseDto;
import com.ecommerceapp.paymentservice.dtos.cartDtos.Status;
import com.ecommerceapp.paymentservice.dtos.cartDtos.UpdateCartRequestDto;
import com.ecommerceapp.paymentservice.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping("/")
    public ResponseEntity<GetCartResponseDto> getCartDetails(@RequestHeader ("Authorization") String authorizationHeader, @RequestHeader ("User-ID") Long userId) {
        //check if token is valid
        try{
            GetCartResponseDto getCartResponseDto = cartService.getCartDetails(userId);
            ResponseEntity<GetCartResponseDto> response = new ResponseEntity<>(
                    getCartResponseDto, HttpStatus.OK);

            return response;

        }catch (Exception ex){
            GetCartResponseDto getCartResponseDto = new GetCartResponseDto();

            return new ResponseEntity<GetCartResponseDto>(getCartResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<StandardResponseDto> updateCart(@RequestHeader ("Authorization") String authorizationHeader,
                                                         @RequestHeader ("User-ID") Long userId,
                                                         @RequestBody UpdateCartRequestDto updateCartRequestDto) {

        //check if the token is valid
        StandardResponseDto standardResponseDto = new StandardResponseDto();

        try{
            cartService.Upsert(userId, updateCartRequestDto);
            standardResponseDto.setMessage("Cart updated successfully");
            standardResponseDto.setStatus(Status.SUCCESS);
            return new ResponseEntity<StandardResponseDto>(standardResponseDto, HttpStatus.OK);
        }catch (Exception ex){
            standardResponseDto.setMessage(ex.getMessage());
            standardResponseDto.setStatus(Status.FAILURE);
            return new ResponseEntity<StandardResponseDto>(standardResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
