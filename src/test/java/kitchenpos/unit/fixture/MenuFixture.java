package kitchenpos.unit.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static kitchenpos.unit.fixture.ProductFixture.*;

public class MenuFixture {
    public static final MenuGroup 탕수육_세트;
    public static final Menu 한그릇_세트;
    public static final Menu 두그릇_세트;
    public static final Menu 세그릇_세트;

    public static final List<Product> 한그릇_세트_상품목록;
    public static final List<Product> 두그릇_세트_상품목록;
    public static final List<Product> 세그릇_세트_상품목록;

    static {
        탕수육_세트 = MenuGroupFixture.탕수육_세트;
        한그릇_세트_상품목록 = Arrays.asList(탕수육, 짜장면);
        두그릇_세트_상품목록 = Arrays.asList(탕수육, 짜장면, 짬뽕);
        세그릇_세트_상품목록 = Arrays.asList(탕수육, 짜장면, 짬뽕, 볶음밥);

        한그릇_세트 = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(14000), 한그릇_세트_상품목록, true);
        두그릇_세트 = createMenu(탕수육_세트, "두그릇 세트", BigDecimal.valueOf(20000), 두그릇_세트_상품목록, true);
        세그릇_세트 = createMenu(탕수육_세트, "세그릇 세트", BigDecimal.valueOf(25000), 세그릇_세트_상품목록, true);
    }

    public static Menu createMenu(MenuGroup menuGroup, String name, BigDecimal price, List<Product> products, boolean displayed) {
        return createMenuWithMenuProducts(menuGroup, name, price, createMenuProducts(products), displayed);
    }

    public static Menu createMenuWithMenuProducts(MenuGroup menuGroup, String name, BigDecimal price, List<MenuProduct> menuProducts, boolean displayed) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(displayed);

        return menu;
    }

    public static List<MenuProduct> createMenuProducts(List<Product> products) {
        return products.stream()
                .map(p -> createMenuProduct(p, 1))
                .collect(Collectors.toList());
    }

    public static MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }
}
