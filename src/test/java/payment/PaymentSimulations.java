package payment;

import io.gatling.javaapi.core.ScenarioBuilder;

import java.util.Iterator;

import static io.gatling.javaapi.core.CoreDsl.*;

public class PaymentSimulations extends BasePaymentSimulation {
    // 1. 일일 기본 부하 테스트 시나리오
    ScenarioBuilder dailyLoadTest = scenario("일일_기본_부하_테스트")
            .feed(Iterator.class.cast(feeder))
            .exec(paymentRequest("일일_기본_결제_요청"));

    // 2. 이벤트성 부하 테스트 시나리오
    ScenarioBuilder eventLoadTest = scenario("이벤트_부하_테스트")
            .feed(Iterator.class.cast(feeder))
            .exec(paymentRequest("이벤트_결제_요청"));

    // 3. 스트레스 테스트 시나리오
    ScenarioBuilder stressTest = scenario("스트레스_테스트")
            .feed(Iterator.class.cast(feeder))
            .exec(paymentRequest("스트레스_결제_요청"));

    // 4. 단계적 증가 테스트 시나리오
    ScenarioBuilder incrementalLoadTest = scenario("단계적_증가_테스트")
            .feed(Iterator.class.cast(feeder))
            .exec(paymentRequest("단계적_증가_결제_요청"));


    {
        setUp(
                dailyLoadTest.injectOpen(
                        // 일일 부하 테스트 시나리오
                        rampUsers(50).during(10),                      // 10초 동안 50명 점진적 증가
                        constantUsersPerSec(80).during(15),           // 초당 80명으로 15초 유지
                        rampUsersPerSec(80).to(150).during(10),      // 10초 동안 80명에서 150명으로 증가
                        constantUsersPerSec(150).during(20),          // 초당 150명으로 20초 유지
                        rampUsersPerSec(150).to(80).during(10)       // 10초 동안 150명에서 80명으로 감소
                ).andThen(
                        eventLoadTest.injectOpen(
                                // 이벤트 부하 테스트 시나리오
                                rampUsers(80).during(10),              // 10초 동안 80명 점진적 증가
                                constantUsersPerSec(120).during(15),   // 초당 120명으로 15초 유지
                                rampUsersPerSec(120).to(200).during(10), // 10초 동안 120명에서 200명으로 증가
                                constantUsersPerSec(200).during(20),    // 초당 200명으로 20초 유지
                                rampUsersPerSec(200).to(120).during(10)  // 10초 동안 200명에서 120명으로 감소
                        )
                ).andThen(
                        stressTest.injectOpen(
                                // 스트레스 테스트 시나리오
                                rampUsers(150).during(5),              // 5초 동안 150명 급격히 증가
                                constantUsersPerSec(150).during(15),   // 초당 150명으로 15초 유지
                                stressPeakUsers(250).during(5),        // 5초 동안 250명까지 스파이크
                                constantUsersPerSec(150).during(15),   // 다시 초당 150명으로 15초 유지
                                rampUsersPerSec(150).to(80).during(10) // 10초 동안 150명에서 80명으로 감소
                        )
                ).andThen(
                        incrementalLoadTest.injectOpen(
                                // 단계별 부하 증가 테스트
                                incrementUsersPerSec(60)              // 초당 60명씩 증가
                                        .times(3)                     // 3단계로 진행
                                        .eachLevelLasting(10)         // 각 단계는 10초 유지
                                        .separatedByRampsLasting(8)   // 단계 사이 8초 동안 점진적 증가
                                        .startingFrom(60)             // 초당 60명부터 시작
                        )
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(1000),   // 95%의 응답이 1000ms 이하
                        global().responseTime().percentile4().lt(1500),   // 99%의 응답이 1500ms 이하
                        global().responseTime().max().lt(2500),           // 최대 응답시간 2500ms 이하
                        global().successfulRequests().percent().gt(98d),  // 성공률 98% 이상
                        global().failedRequests().count().lt(200L)        // 실패 요청 200건 미만
                );
    }
}