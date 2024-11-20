package payment.internal;

import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    boolean existsByOrderId(String orderId);
}
