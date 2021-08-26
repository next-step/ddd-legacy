package kitchenpos.ui.dto;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Menu;

public class OrderLineItemRequest {

    private Long seq;
    private MenuRequest menu;
    private long quantity;
    private BigDecimal price;

    private UUID menuId;

    public OrderLineItemRequest(final long quantity, final Menu menu) {
        this.quantity = quantity;
        this.menu = new MenuRequest(menu);
        this.price = menu.getPrice();

        this.menuId = menu.getId();
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public MenuRequest getMenu() {
        return menu;
    }

    public void setMenu(final MenuRequest menu) {
        this.menu = menu;
    }

    public void setMenu(final Menu menu) {
        this.menu = new MenuRequest(menu);
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public void setMenuId(final UUID menuId) {
        this.menuId = menuId;
    }
}
