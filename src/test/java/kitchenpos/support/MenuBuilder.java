package kitchenpos.support;

import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;

import java.math.BigDecimal;
import java.util.List;

public class MenuBuilder {

    private Long id;
    private String name;
    private BigDecimal price;
    private Long menuGroupId;
    private List<MenuProduct> menuProducts;

    public MenuBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MenuBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuBuilder menuGroupId(Long menuGroupId) {
        this.menuGroupId = menuGroupId;
        return this;
    }

    public MenuBuilder menuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu();
        menu.setId(this.id);
        menu.setName(this.name);
        menu.setPrice(this.price);
        menu.setMenuGroupId(this.menuGroupId);
        menu.setMenuProducts(this.menuProducts);

        return menu;
    }
}
