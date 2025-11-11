package com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.Stripe;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfiguration {

    @Bean
    public StripeClient getStripeClient(){
        Stripe.apiKey = System.getenv("stripe_secret_key");
        return new StripeClient(Stripe.apiKey);
    }
}
