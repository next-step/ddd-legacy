package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public interface RidersClient {
    void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress);
}
