package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

public interface KitchenClient {
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress);
}
