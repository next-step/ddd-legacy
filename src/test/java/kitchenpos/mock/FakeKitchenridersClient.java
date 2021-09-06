package kitchenpos.mock;

import kitchenpos.infra.KitchenridersClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient implements KitchenridersClient {

    private int callCounter = 0;

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
        callCounter++;
    }

    public int getCallCounter() {
        return callCounter;
    }
}
