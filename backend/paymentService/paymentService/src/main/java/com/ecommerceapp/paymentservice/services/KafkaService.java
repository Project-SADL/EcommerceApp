package com.ecommerceapp.paymentservice.services;

import com.ecommerceapp.paymentservice.adapters.paymentGatewayAdapters.Config.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public boolean sendOrderDetailsToKafka(String orderDetails){
        this.kafkaTemplate.send(AppConstants.order_topic_name, orderDetails);
        return true;
    }
}
