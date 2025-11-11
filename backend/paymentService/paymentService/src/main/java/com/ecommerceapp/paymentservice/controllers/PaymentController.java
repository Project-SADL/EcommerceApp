package com.ecommerceapp.paymentservice.controllers;

import com.ecommerceapp.paymentservice.dtos.CreatePaymentLinkRequestDto;
import com.ecommerceapp.paymentservice.dtos.CreatePaymentLinkResponseDto;
import com.ecommerceapp.paymentservice.services.PaymentService;
import com.stripe.model.issuing.Authorization;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public CreatePaymentLinkResponseDto createPaymentLink(@RequestHeader ("Authorization") String AuthorizationHeader,
                                                          @RequestHeader ("User-ID") Long UserId,
                                                          @RequestBody CreatePaymentLinkRequestDto request){
        try{
            String url = paymentService.createPaymentLink(request.getCartId(), UserId);
            CreatePaymentLinkResponseDto response = new CreatePaymentLinkResponseDto();
            response.setUrl(url);
            return response;
        }catch (Exception e){
            throw new RuntimeException("Error creating payment link: " + e.getMessage());
        }
    }
}
