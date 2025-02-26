package kitchenpos.fake;

import kitchenpos.infra.KitchenridersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient implements KitchenridersClient {

    private int requestDeliveryCount = 0;

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
        requestDeliveryCount++;
    }

    public int getRequestDeliveryCount() {
        return requestDeliveryCount;
    }

}
