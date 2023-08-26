package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class MenuFixture {

    private MenuFixture() {
    }

    public static Menu create(String name, BigDecimal price, MenuProduct menuProducts) {
        return create(name, price, List.of(menuProducts));
    }

    public static Menu create(String name, BigDecimal price, List<MenuProduct> menuProducts) {
        Menu result = new Menu();
        result.setName(name);
        result.setPrice(menuProducts.stream()
                .map(menuProduct -> menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.setMenuProducts(menuProducts);
        result.setMenuGroup(MenuGroupFixture.create());
        result.setDisplayed(true);
        return result;
    }

    public static class MenuFixtureCreateDto {
        private String name;
        private BigDecimal price;
        private List<Product> products;
    }

}
