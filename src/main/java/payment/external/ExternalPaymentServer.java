package payment.external;

import payment.external.PaymentServerDto.PaymentServerResponse;

public interface ExternalPaymentServer {
    PaymentServerResponse requestPayment();
}
