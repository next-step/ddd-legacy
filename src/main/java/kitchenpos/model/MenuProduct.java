package kitchenpos.model;

import java.util.Objects;

public class MenuProduct {
    private Long seq;
    private Long menuId;
    private Long productId;
    private int quantity;

    private MenuProduct (Builder builder){
        this.seq = builder.seq;
        this.menuId = builder.menuId;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
    }

    public Long getSeq() {
        return seq;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId){
        this.menuId = menuId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
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

    public static class Builder {
        private Long seq = 0L;
        private Long menuId = 0L;
        private Long productId = 0L;
        private int quantity = 0;

        public Builder(){

        }

        public Builder seq (Long seq){
            this.seq = seq;
            return this;
        }

        public Builder menuId (Long menuId){
            this.menuId = menuId;
            return this;
        }

        public Builder productId (Long productId){
            this.productId = productId;
            return this;
        }

        public Builder quantity (int quantity){
            this.quantity = quantity;
            return this;
        }

        public MenuProduct build(){
            return new MenuProduct(this);
        }
    }
}
