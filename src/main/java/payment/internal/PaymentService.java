package payment.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.external.ExternalPaymentServer;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ExternalPaymentServer server;

    @Transactional
    public PaymentResponse processPayment(String cardNumber, Long amount, String orderId) {
        // 중복 결제 체크
        if (paymentRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }

        // 결제 처리
        Payment payment = Payment.createPayment(cardNumber, amount, orderId);

        Payment savedPayment = paymentRepository.save(payment);

        // 가짜 PG서버에 요청
        server.requestPayment();

        payment.complete();

        return PaymentResponse.from(savedPayment);

    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));
        return PaymentResponse.from(payment);
    }
}