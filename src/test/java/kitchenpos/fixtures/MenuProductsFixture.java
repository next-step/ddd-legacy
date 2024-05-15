package kitchenpos.fixtures;

import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductsFixture {
    private final List<MenuProductFixture> menuProductsFixture;

    public MenuProductsFixture(MenuProductFixture...menuProductFixtures) {
        this.menuProductsFixture = Arrays.asList(menuProductFixtures);
    }

    public List<MenuProductFixture> getMenuProductsFixture() {
        return menuProductsFixture;
    }

    public List<MenuProduct> getMenuProductList() {
        return this.menuProductsFixture.stream().map(MenuProductFixture::getMenuProduct).toList();
    }
}
