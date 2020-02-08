package kitchenpos.model;

public class OrderLineItem { // 주문 메뉴 Mapping
    private Long seq; // 주문메뉴 순서
    private Long orderId; // 주문 id
    private Long menuId; // 메뉴 id
    private long quantity; // 메뉴 수량

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
}
