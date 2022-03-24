package kitchenpos.infra;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class KitchenridersClient implements KitchenClient {

    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {

    }
}
