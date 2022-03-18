package kitchenpos.application.fake.helper;

import kitchenpos.infra.RidersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeRidersClient implements RidersClient {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}
