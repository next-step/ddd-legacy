package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public interface RiderAgencyClient {

    void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress);
}
