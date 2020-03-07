package kitchenpos.builder;

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

    public MenuBuilder() {
    }

    public MenuBuilder id(Long val) {
        id = val;
        return this;
    }

    public MenuBuilder name(String val) {
        name = val;
        return this;
    }

    public MenuBuilder price(BigDecimal val) {
        price = val;
        return this;
    }

    public MenuBuilder menuGroupId(Long val) {
        menuGroupId = val;
        return this;
    }

    public MenuBuilder menuProducts(List<MenuProduct> val) {
        menuProducts = val;
        return this;
    }

    public Menu build() {
        final Menu menu = new Menu();
        menu.setId(this.id);
        menu.setName(this.name);
        menu.setPrice(this.price);
        menu.setMenuProducts(this.menuProducts);
        menu.setMenuGroupId(this.menuGroupId);
        return menu;
    }

}
