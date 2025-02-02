package com.delivery.delivery_app.service;

import com.delivery.delivery_app.dto.payment.PaymentMethodResponse;
import com.delivery.delivery_app.dto.payment.SetupIntentResponse;
import com.delivery.delivery_app.entity.User;
import com.delivery.delivery_app.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.SetupIntentCreateParams;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeService {
    private final UserRepository userRepository;
    @Value("${stripe.secret.key}")
    private String secretKey;

    public SetupIntentResponse createSetupIntent() throws StripeException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Stripe.apiKey = secretKey;
        if (user.getPaymentId() == null) {
            CustomerCreateParams.Builder paramsBuilder = CustomerCreateParams.builder();
            if (user.getEmail() != null) paramsBuilder.setEmail(user.getEmail());
            if (user.getName() != null) paramsBuilder.setName(user.getName());
            if (user.getUsername() != null) paramsBuilder.setPhone(user.getPhoneNumber());
            Customer customer = Customer.create(paramsBuilder.build());
            user.setPaymentId(customer.getId());
            userRepository.save(user);
        }

        SetupIntentCreateParams params = SetupIntentCreateParams.builder().setCustomer(user.getPaymentId()).addPaymentMethodType("card").build();
        var setupIntent = SetupIntent.create(params);
        return SetupIntentResponse.builder().clientSecret(setupIntent.getClientSecret()).build();
    }

    public PaymentIntent verifyPayment(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;
        return PaymentIntent.retrieve(paymentIntentId);
    }

    public List<PaymentMethodResponse> getAllPaymentMethods() throws StripeException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Stripe.apiKey = secretKey;
        String customerId = user.getPaymentId();
        if (customerId == null) return new ArrayList<>();
        PaymentMethodListParams params = PaymentMethodListParams.builder().setCustomer(customerId).setType(PaymentMethodListParams.Type.CARD).build();
        PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
        List<PaymentMethodResponse> paymentMethodsResponse = new ArrayList<>();
        for (PaymentMethod paymentMethod : paymentMethods.getData()) {
            paymentMethodsResponse.add(PaymentMethodResponse.builder().id(paymentMethod.getId()).brand(paymentMethod.getCard().getBrand()).last4(paymentMethod.getCard().getLast4()).build());
        }
        return paymentMethodsResponse;
    }

    public PaymentIntent refundPayment(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.cancel();
    }

    public PaymentIntent paymentIntent(String customerId, String paymentMethodId, Long amount) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntentCreateParams paymentParams = PaymentIntentCreateParams.builder().setAmount(amount).setCurrency("vnd").setCustomer(customerId).setPaymentMethod(paymentMethodId).setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC).setConfirm(true).setReturnUrl("https://your-domain.com/payment-status").build();
        return PaymentIntent.create(paymentParams);
    }

    public PaymentMethod getPaymentMethod(String paymentMethodId) throws StripeException {
        return PaymentMethod.retrieve(paymentMethodId);
    }
}
