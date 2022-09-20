package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.RidersClient;
import org.springframework.stereotype.Component;

@Component
public class KitchenridersClient implements RidersClient {

  @Override
  public void requestDelivery(final UUID orderId, final BigDecimal amount,
      final String deliveryAddress) {
  }
}
