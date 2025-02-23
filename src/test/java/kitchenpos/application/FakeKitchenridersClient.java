package kitchenpos.application;

import kitchenpos.infra.KitchenridersClient;

import java.math.BigDecimal;
import java.util.UUID;

public class FakeKitchenridersClient implements KitchenridersClient {
    private UUID orderId;
    private BigDecimal amount;
    private String deliveryAddress;

    @Override
    public void requestDelivery(final UUID orderId, final BigDecimal amount, final String deliveryAddress) {
        this.orderId = orderId;
        this.amount = amount;
        this.deliveryAddress = deliveryAddress;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }
}
