package kitchenpos.mock;

import kitchenpos.infra.KitchenridersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class MockKitchenridersClient implements KitchenridersClient {
    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
    }
}
