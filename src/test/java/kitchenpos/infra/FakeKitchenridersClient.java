package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient implements KitchenridersClient{
    private boolean requestedDelivery = false;

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
        this.requestedDelivery = true;
    }

    public boolean isRequestedDelivery() {
        return requestedDelivery;
    }
}
