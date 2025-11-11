package com.ecommerceapp.paymentservice.controllers;

import com.ecommerceapp.paymentservice.models.carts_orders.OrderStatus;
import com.ecommerceapp.paymentservice.models.payments.PaymentGateway;
import com.ecommerceapp.paymentservice.models.payments.PaymentStatus;
import com.ecommerceapp.paymentservice.services.OrderService;
import com.ecommerceapp.paymentservice.services.PaymentService;
import com.stripe.exception.ApiException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/webhook/stripe")
public class StripeWebHook {
    private final OrderService orderService;
    private String endpointSecret;
    private PaymentService paymentService ;

    public StripeWebHook(OrderService orderService, PaymentService paymentService) {
        this.paymentService = paymentService;
        this.endpointSecret = System.getenv("end_point_secret");
        this.orderService = orderService;
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        String message;
        if (sigHeader == null) {
            return new ResponseEntity<>("Null signhed header", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Process the Stripe event
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return new ResponseEntity<>("Could notcreate event ", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                throw new RuntimeException("Stripe object not found");
            }


            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) stripeObject;
                    //get metadata from session
                    String orderId = session.getMetadata().get("order_id");
                    String userId = session.getMetadata().get("user_id");
                    String paymentId = session.getMetadata().get("payment_id");
                    orderService.updatePaymentStatus(orderId, OrderStatus.COMPLETED);
                    paymentService.updatePaymentStatus(orderId,
                            userId,
                            PaymentStatus.SUCCESS,
                            PaymentGateway.STRIPE);
                    break;
                case "checkout.session.async_payment_failed":
                    session = (Session) stripeObject;
                    orderId = session.getMetadata().get("order_id");
                    userId = session.getMetadata().get("user_id");
                    paymentId = session.getMetadata().get("payment_id");
                    orderService.updatePaymentStatus(orderId, OrderStatus.PENDING);
                    paymentService.updatePaymentStatus(orderId,
                            userId,
                            PaymentStatus.FAILURE,
                            PaymentGateway.STRIPE);

                    break;

                default:
                    System.out.println("Unhandled event type: " + event.getType());

                    //post to the inventory service to work on delivery of this order
            }

            message = "Success";

            //notify Inventory Service about the order to be delivered
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e) {
            message = "Webhook error: " + e.getMessage();

            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }
}
