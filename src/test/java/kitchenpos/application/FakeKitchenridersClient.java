package kitchenpos.application;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.infra.KitchenridersClient;

public class FakeKitchenridersClient implements KitchenridersClient {

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}
