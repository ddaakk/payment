package payment.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import payment.external.PaymentServerDto.PaymentServerResponse;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MockExternalPaymentServer implements ExternalPaymentServer {
    @Override
    public PaymentServerResponse requestPayment() {
        // 실제 PG사처럼 랜덤 딜레이 발생
        long startTime = System.nanoTime();

        randomDelay();

        long duration = System.nanoTime() - startTime;
        log.info("실제 지연 시간: {}ms", duration / 1_000_000);

        String transactionId = UUID.randomUUID().toString();
        PaymentServerResponse response = PaymentServerResponse.success(transactionId);

        log.info("결제가 성공하였습니다. 트랜잭션: {}", transactionId);

        return response;
    }

    private void randomDelay() {
        try {
            // 100ms ~ 200ms 랜덤 딜레이
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
