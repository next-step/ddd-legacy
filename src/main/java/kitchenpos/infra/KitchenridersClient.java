package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class KitchenridersClient {

    public void requestDelivery(final UUID orderId, final BigDecimal amount,
        final String deliveryAddress) {
    }
}
