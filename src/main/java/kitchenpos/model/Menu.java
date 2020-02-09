package kitchenpos.model;

import java.math.BigDecimal;
import java.util.List;

public class Menu {
    private Long id; // 메뉴 id
    private String name; // 메뉴명
    private BigDecimal price; // 메뉴 가격
    private Long menuGroupId; // 메뉴 그룹 id
    private List<MenuProduct> menuProducts; // 메뉴제품 리스트

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public Long getMenuGroupId() {
        return menuGroupId;
    }

    public void setMenuGroupId(final Long menuGroupId) {
        this.menuGroupId = menuGroupId;
    }

    public List<MenuProduct> getMenuProducts() {
        return menuProducts;
    }

    public void setMenuProducts(final List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
    }
}
