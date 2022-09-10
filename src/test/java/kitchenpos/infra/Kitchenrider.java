package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

public class Kitchenrider {
    private UUID orderId;
    private BigDecimal amount;
    private String deliveryAddress;

    public Kitchenrider(UUID orderId, BigDecimal amount, String deliveryAddress) {
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
