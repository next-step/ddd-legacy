package kitchenpos;

import kitchenpos.infra.KitchenClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenClient implements KitchenClient {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}
