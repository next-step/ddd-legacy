package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuFixture {

    private MenuFixture() {
    }

    public static Menu createWithProducts(String name, BigDecimal price, Product product) {
        MenuProduct menuProduct = MenuProductFixture.create(product, 1);
        return create(name, price, List.of(menuProduct));
    }

    public static Menu createWithProducts(String name, BigDecimal price, List<Product> products) {
        List<MenuProduct> menuProduct = products.stream()
                .map(product -> MenuProductFixture.create(product, 1))
                .collect(Collectors.toList());
        return create(name, price, menuProduct);
    }

    public static Menu create(String name, BigDecimal price, MenuProduct menuProducts) {
        return create(name, price, List.of(menuProducts));
    }

    public static Menu create(String name, BigDecimal price, List<MenuProduct> menuProducts) {
        return create(UUID.randomUUID(), name, price, menuProducts, true);
    }

    public static Menu create(UUID id, String name, BigDecimal price, List<MenuProduct> menuProducts, boolean displayed) {
        MenuGroup menuGroup = MenuGroupFixture.create();

        Menu result = new Menu();
        result.setId(id);
        result.setName(name);
        result.setPrice(price);
        result.setMenuProducts(menuProducts);
        result.setMenuGroup(menuGroup);
        result.setMenuGroupId(menuGroup.getId());
        result.setDisplayed(displayed);
        return result;
    }

}
