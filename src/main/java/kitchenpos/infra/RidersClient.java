package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

public interface RidersClient {
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress);
}
