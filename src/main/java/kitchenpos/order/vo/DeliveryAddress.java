package kitchenpos.order.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class DeliveryAddress {

    @Column(name = "delivery_address")
    private String address;

    protected DeliveryAddress() {

    }

    public DeliveryAddress(String address) {
        validateDeliveryAddress(address);
        this.address = address;
    }

    private void validateDeliveryAddress(String address) {
        if (Objects.isNull(address) || address.isEmpty()) {
            throw new IllegalArgumentException("null 이나 공백일 수 없습니다.");
        }
    }

    public String getAddress() {
        return this.address;
    }
}
