package payment.internal;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        String orderId,
        Long amount,
        String cardNumber,
        PaymentStatus status,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                maskCardNumber(payment.getCardNumber()),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }
        return cardNumber.substring(0, 6) + "******" +
                cardNumber.substring(cardNumber.length() - 4);
    }
}