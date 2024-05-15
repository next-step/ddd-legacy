package kitchenpos.fakeClient;


import kitchenpos.infra.KitchenridersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient extends KitchenridersClient {

    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
        return;
    }
}
