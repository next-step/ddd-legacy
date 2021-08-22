package kitchenpos.application;

import kitchenpos.infra.KitchenridersClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient implements KitchenridersClient {
    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
    }
}
