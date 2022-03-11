package kitchenpos.unit.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.unit.fixture.ProductFixture.*;

public class MenuFixture {
    public static final MenuGroup 탕수육_세트;
    public static final Menu 한그릇_세트;
    public static final Menu 두그릇_세트;
    public static final Menu 세그릇_세트;

    static {
        탕수육_세트 = new MenuGroup();
        한그릇_세트 = createMenu(탕수육_세트, 14000, 탕수육, 짜장면);
        두그릇_세트 = createMenu(탕수육_세트, 20000, 탕수육, 짜장면, 짬뽕);
        세그릇_세트 = createMenu(탕수육_세트, 25000, 탕수육, 짜장면, 짬뽕, 볶음밥);
    }

    private static Menu createMenu(MenuGroup menuGroup, int price, Product... products) {
        Menu menu = new Menu();
        menu.setMenuGroup(menuGroup);
        menu.setName(createMenuName(products));
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuProducts(createMenuProducts(products));
        menu.setDisplayed(true);

        return menu;
    }

    private static List<MenuProduct> createMenuProducts(Product[] products) {
        return Arrays.stream(products)
                .map(p -> createMenuProduct(p, 1))
                .collect(Collectors.toList());
    }

    private static MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private static String createMenuName(Product[] products) {
        String[] names = Arrays.stream(products)
                .map(Product::getName)
                .toArray(String[]::new);
        return String.join("+", names);
    }
}
