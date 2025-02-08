package kitchenpos.application.fake;

import kitchenpos.infra.RiderClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeRiderClient implements RiderClient {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
        //nothingToDo
    }
}
