package kitchenpos.fake;

import kitchenpos.infra.RidersClient;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class FakeRidersClient implements RidersClient {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}