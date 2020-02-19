package kitchenpos.model;

import java.util.Objects;

public class MenuProduct {
    private Long seq;
    private Long menuId;
    private Long productId;
    private long quantity;

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuProduct that = (MenuProduct) o;
        return getQuantity() == that.getQuantity() &&
            Objects.equals(getSeq(), that.getSeq()) &&
            Objects.equals(getMenuId(), that.getMenuId()) &&
            Objects.equals(getProductId(), that.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeq(), getMenuId(), getProductId(), getQuantity());
    }

    @Override
    public String toString() {
        return "MenuProduct{" +
            "seq=" + seq +
            ", menuId=" + menuId +
            ", productId=" + productId +
            ", quantity=" + quantity +
            '}';
    }
}
