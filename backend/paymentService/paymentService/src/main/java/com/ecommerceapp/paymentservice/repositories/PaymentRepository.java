package com.ecommerceapp.paymentservice.repositories;

import com.ecommerceapp.paymentservice.models.payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
