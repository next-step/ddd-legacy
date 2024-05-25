package kitchenpos.stub;

import kitchenpos.infra.KitchenridersClient;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.UUID;

@TestComponent
public class TestKitchenridersClient extends KitchenridersClient {

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }

}
