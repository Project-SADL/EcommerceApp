package com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.Stripe;

import com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.PaymentGatewayAdapter;
import com.ecommerceapp.paymentservice.models.carts_orders.OrderItems;
import com.ecommerceapp.paymentservice.models.carts_orders.Orders;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentGatewayAdapter implements PaymentGatewayAdapter {
    private final StripeClient stripeClient;

    public StripePaymentGatewayAdapter(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @Override
    public String createPaymentLink(Orders order) throws Exception{
       Long orderId = order.getId();
        //create product
//        Stripe.apiKey = System.getenv("stripe_secret_key");
        String productName = order.getId().toString() + order.getUserId().toString();
        ProductCreateParams productCreateParams = ProductCreateParams.builder().setName(productName).build();
        Product product = Product.create(productCreateParams);
        Map<String,String> metadata = new HashMap<>();
        metadata.put("order_id",order.getId().toString());
        metadata.put("user_id",order.getUserId().toString());

//        PaymentLinkCreateParams.PaymentIntentData paymentIntentData =
//                PaymentLinkCreateParams.PaymentIntentData
//                        .builder()
//                        .putMetadata("order_id",order.getId().toString())
//                        .putMetadata("user_id",order.getUserId().toString())
//                        .build();
//        PaymentIntent paymentIntent = PaymentIntent.create(params);
        //create price
        PriceCreateParams priceCreateParams =
                PriceCreateParams.builder()
                        .setCurrency("usd")
                        .setProduct(product.getId())
                        .setUnitAmount(order.getTotalPrice().multiply(new BigDecimal(100)).longValue())
                        .build();

        Price price = Price.create(priceCreateParams);

        //create payment link
        PaymentLinkCreateParams paymentLinkCreateParams =
                PaymentLinkCreateParams.builder()
                        .setAfterCompletion(
                                PaymentLinkCreateParams.AfterCompletion.builder()
                                        .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                        .setRedirect(
                                                PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                        .setUrl("https://thebookshopnashville.com/")
                                                        .build()
                                        )
                                        .build()
                        )
                        .setCurrency("usd")
                        .putMetadata("order_id",order.getId().toString())
                        .putMetadata("user_id",order.getUserId().toString())
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setPrice(price.getId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .build();

        PaymentLink paymentLink = PaymentLink.create(paymentLinkCreateParams);
        return paymentLink.getUrl();
    }
}
