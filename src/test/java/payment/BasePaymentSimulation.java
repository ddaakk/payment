package payment;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * BasePaymentSimulation
 * - 공통 설정과 데이터 생성 로직을 포함하는 상위 클래스.
 * - 모든 결제 시뮬레이션에서 공통적으로 사용되는 구성 요소를 정의.
 */
public abstract class BasePaymentSimulation extends Simulation {

    // HTTP 프로토콜 설정 (기본 URL 및 헤더 구성)
    protected HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Feeder: 테스트 데이터 공급자 (무작위 데이터 생성)
    protected Iterator<Map<String, Object>> feeder =
            Stream.generate((Supplier<Map<String, Object>>) () ->
                    Map.of(
                            "cardNumber", generateCardNumber(),
                            "orderId", generateOrderId(),
                            "amount", generateAmount()
                    )).iterator();

    // 공통 결제 요청 메서드
    protected HttpRequestActionBuilder paymentRequest(String requestName) {
        return http(requestName)
                .post("/payments")
                .body(StringBody("""
                        {
                            "cardNumber": "#{cardNumber}",
                            "amount": #{amount},
                            "orderId": "#{orderId}"
                        }
                        """))
                .check(status().is(200))
                .check(jsonPath("$.paymentId").saveAs("paymentId"));
    }

    // 카드 번호 생성기 (16자리 무작위 번호)
    private static String generateCardNumber() {
        Random rand = new Random();
        return "4" + String.format("%015d", Math.abs(rand.nextLong() % 999999999999999L));
    }

    // 주문 ID 생성기 (고유 주문 번호)
    private static String generateOrderId() {
        return "ORDER-" + String.format("%06d", new Random().nextInt(999999));
    }

    // 결제 금액 생성기 (10,000원 ~ 100,000원 사이 무작위 값)
    private static int generateAmount() {
        return new Random().nextInt(90000) + 10000;
    }
}
