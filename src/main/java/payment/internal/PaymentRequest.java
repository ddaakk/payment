package payment.internal;

public record PaymentRequest(
        String cardNumber,
        Long amount,
        String orderId
) { }