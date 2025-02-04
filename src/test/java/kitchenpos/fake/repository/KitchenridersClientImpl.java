package kitchenpos.fake.repository;

import kitchenpos.infra.KitchenridersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class KitchenridersClientImpl implements KitchenridersClient {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}
