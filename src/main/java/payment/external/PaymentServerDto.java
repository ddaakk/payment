package payment.external;

public class PaymentServerDto {

    public record PaymentServerRequest(
            String cardNumber,
            Long amount,
            String orderId
    ) {
        public static PaymentServerRequest of(String cardNumber, Long amount, String orderId) {
            return new PaymentServerRequest(cardNumber, amount, orderId);
        }
    }

    public record PaymentServerResponse(
            boolean success,
            String transactionId,
            String errorMessage
    ) {
        public static PaymentServerResponse success(String transactionId) {
            return new PaymentServerResponse(true, transactionId, null);
        }

        public static PaymentServerResponse failed(String errorMessage) {
            return new PaymentServerResponse(false, null, errorMessage);
        }
    }
}