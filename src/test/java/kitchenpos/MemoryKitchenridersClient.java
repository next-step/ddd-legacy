package kitchenpos;

import kitchenpos.infra.KitchenridersClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

public class MemoryKitchenridersClient extends KitchenridersClient {
  public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
  }
}
