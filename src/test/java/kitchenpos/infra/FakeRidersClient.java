package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.RidersClient;

public class FakeRidersClient implements RidersClient {

  @Override
  public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

  }
}
