package kitchenpos.model;

public class MenuProduct {
    private Long seq; // 메뉴 내 메뉴제품 순서
    private Long menuId; // 메뉴 id
    private Long productId; // 제품 id
    private long quantity; // 제품 수량

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(final Long menuId) {
        this.menuId = menuId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }
}
