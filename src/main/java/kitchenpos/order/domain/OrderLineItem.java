package kitchenpos.order.domain;

import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "order_line_item")
@Entity
public class OrderLineItem {
    @Column(name = "seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long seq;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "menu_id",
            columnDefinition = "binary(16)",
            foreignKey = @ForeignKey(name = "fk_order_line_item_to_menu")
    )
    private Menu menu;

    private Quantity quantity;

    @Transient
    private UUID menuId;

    @Transient
    private BigDecimal price;

    protected OrderLineItem() {

    }

    public OrderLineItem(Menu menu, Quantity quantity) {
        validate(menu);
        this.quantity = quantity;
        this.menu = menu;
    }

    private static void validate(Menu menu) {
        if (!menu.isDisplayed()) {
            throw new IllegalStateException("안보이는 메뉴가 주문될 수 없다.");
        }
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(final Long seq) {
        this.seq = seq;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(final Menu menu) {
        this.menu = menu;
    }


    public UUID getMenuId() {
        return menuId;
    }

    public void setMenuId(final UUID menuId) {
        this.menuId = menuId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity.getQuantity();
    }
}
