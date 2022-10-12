package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

public class DummyKitchenridersClient implements KitchenridersClient {

    @Override
    public void requestDelivery(
        UUID orderId,
        BigDecimal amount,
        String deliveryAddress
    ) {
        // Do nothing.
    }
}
