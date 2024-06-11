package kitchenpos.application;

import java.math.BigDecimal;
import java.util.UUID;

@FunctionalInterface
public interface KitchenridersService {
    void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress);
}
