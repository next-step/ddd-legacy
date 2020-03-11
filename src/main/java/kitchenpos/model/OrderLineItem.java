package kitchenpos.model;

import java.util.Objects;

public class OrderLineItem {
    private Long seq;
    private Long orderId;
    private Long menuId;
    private long quantity;

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(final Long menuId) {
        this.menuId = menuId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItem that = (OrderLineItem) o;
        return getQuantity() == that.getQuantity() &&
            Objects.equals(getSeq(), that.getSeq()) &&
            Objects.equals(getOrderId(), that.getOrderId()) &&
            Objects.equals(getMenuId(), that.getMenuId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeq(), getOrderId(), getMenuId(), getQuantity());
    }
}
