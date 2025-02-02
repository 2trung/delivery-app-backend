package com.delivery.delivery_app.controller;

import com.delivery.delivery_app.dto.ApiResponse;
import com.delivery.delivery_app.dto.payment.PaymentMethodResponse;
import com.delivery.delivery_app.dto.payment.SetupIntentResponse;
import com.delivery.delivery_app.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeController {
    StripeService stripeService;

    @PostMapping("/setup-intent")
    public ApiResponse<SetupIntentResponse> createSetupIntent() throws StripeException {
        return ApiResponse.<SetupIntentResponse>builder().data(stripeService.createSetupIntent()).build();
    }

    @GetMapping("/payment-methods")
    public ApiResponse<List<PaymentMethodResponse>> getAllPaymentMethods() throws StripeException {
        return ApiResponse.<List<PaymentMethodResponse>>builder().data(stripeService.getAllPaymentMethods()).build();
    }

}
