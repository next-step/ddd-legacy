package kitchenpos.acceptance;

import kitchenpos.infra.KitchenridersClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Primary
public class KitchenridersClientDummy extends KitchenridersClient {
    private int requestDeliveryCallCount = 0;
    
    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
        requestDeliveryCallCount++;
    }
    
    public int getRequestDeliveryCallCount() {
        return requestDeliveryCallCount;
    }
    
    public void clearCallCount() {
        this.requestDeliveryCallCount = 0;
    }
}
