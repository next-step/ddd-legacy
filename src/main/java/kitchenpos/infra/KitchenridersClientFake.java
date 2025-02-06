package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

public class KitchenridersClientFake implements KitchenridersClient {

    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
    }
}
