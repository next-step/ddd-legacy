package kitchenpos.integration.mock;

import kitchenpos.domain.Riders;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeRidersClient implements Riders {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
    }
}
