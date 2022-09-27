package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

class FakeKitchenridersClient implements KitchenridersClient {

    @Override
    public void requestDelivery(
            UUID orderId,
            BigDecimal amount,
            String deliveryAddress
    ) {
        // Do nothing.
    }
}
