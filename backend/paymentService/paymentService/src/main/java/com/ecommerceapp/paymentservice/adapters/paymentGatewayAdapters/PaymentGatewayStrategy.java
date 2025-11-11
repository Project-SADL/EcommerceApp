package com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters;

import com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.Stripe.StripePaymentGatewayAdapter;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayStrategy {
    private final StripePaymentGatewayAdapter stripePaymentGatewayAdapter;

    public PaymentGatewayStrategy(StripePaymentGatewayAdapter stripePaymentGatewayAdapter) {
        this.stripePaymentGatewayAdapter = stripePaymentGatewayAdapter;
    }

    public PaymentGatewayAdapter getPaymentGatewayAdapter(){
//        if(price < 1000){
//            return new StripePaymentGatewayAdapter();
//        }else{
//            return new PaypalPaymentGatewayAdapter();
//        }
        return stripePaymentGatewayAdapter;
    }
}
