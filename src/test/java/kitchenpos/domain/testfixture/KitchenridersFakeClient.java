package kitchenpos.domain.testfixture;

import kitchenpos.infra.KitchenridersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class KitchenridersFakeClient implements KitchenridersClient {
    @Override
    public void requestDelivery(UUID orderId,
                                BigDecimal amount,
                                String deliveryAddress) {
        // delivery를 호출한다. 구현 필요 x
    }
}
