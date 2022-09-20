package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public interface RidersClient {

  void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress);
}
