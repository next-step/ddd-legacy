package kitchenpos.fakeobject;

import kitchenpos.infra.RidersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient implements RidersClient {
    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
        System.out.println(deliveryAddress + "로 배달합니다.");
    }
}
