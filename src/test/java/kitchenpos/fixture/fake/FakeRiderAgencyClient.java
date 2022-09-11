package kitchenpos.fixture.fake;

import kitchenpos.domain.RiderAgencyClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeRiderAgencyClient implements RiderAgencyClient {

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}
