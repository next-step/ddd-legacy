package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuFixture {
    private MenuGroupFixture menuGroupFixture = new MenuGroupFixture();
    private MenuProductFixture menuProductFixture = new MenuProductFixture();

    public Menu 메뉴 = create(
            "메뉴_A", new BigDecimal(10000), menuGroupFixture.메뉴_그룹_A,
            true, List.of(menuProductFixture.메뉴_상품)
    );
    public Menu 메뉴_A_가격_10000 = create(
            "메뉴_A", new BigDecimal(10000), menuGroupFixture.메뉴_그룹_A,
            true, List.of(menuProductFixture.메뉴_상품_A_수량_10)
    );
    public Menu 메뉴_B_가격_5000 = create(
            "메뉴_B", new BigDecimal(5000), menuGroupFixture.메뉴_그룹_A,
            true, List.of(menuProductFixture.메뉴_상품_B)
    );
    public Menu 메뉴_C_가격_100000 = create(
            "메뉴_C", new BigDecimal(100000), menuGroupFixture.메뉴_그룹_A,
            false, List.of(menuProductFixture.메뉴_상품_C)
    );
    public Menu 메뉴_그룹_없는_메뉴 = create(
            "메뉴", new BigDecimal(10000), null,
            true, List.of(menuProductFixture.메뉴_상품_A_수량_10)
    );
    public Menu 가격_없는_메뉴 = create(
            "메뉴", null, null,
            true, List.of(menuProductFixture.메뉴_상품_A_수량_10)
    );
    public Menu 상품_없는_메뉴 = create(
            "메뉴", new BigDecimal(10000), menuGroupFixture.메뉴_그룹_A,
            true, null
    );
    public Menu 이름_없는_메뉴 = create(
            null, new BigDecimal(10000), menuGroupFixture.메뉴_그룹_A,
            true, List.of(menuProductFixture.메뉴_상품_A_수량_10)
    );
    public Menu 부적절한_이름_메뉴 = create(
            "fuck", new BigDecimal(10000), menuGroupFixture.메뉴_그룹_A,
            true, List.of(menuProductFixture.메뉴_상품_A_수량_10)
    );
    public Menu 비활성화_메뉴 = create(
            "메뉴_A", new BigDecimal(10000), menuGroupFixture.메뉴_그룹_A,
            false, List.of(menuProductFixture.메뉴_상품)
    );
    public Menu 메뉴_가격_11000_상품_가격_10000 = create(
            "메뉴", new BigDecimal(11000), menuGroupFixture.메뉴_그룹_A,
            true, List.of(menuProductFixture.메뉴_상품_A_수량_10)
    );

    public static Menu create(String name, BigDecimal price, MenuGroup menuGroup,
                              boolean isDisplayed, List<MenuProduct> menuProducts) {
        return create(UUID.randomUUID(), name, price, menuGroup, isDisplayed, menuProducts);
    }

    public static Menu create(UUID id, String name, BigDecimal price, MenuGroup menuGroup,
                              boolean isDisplayed, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(isDisplayed);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static MenuGroup extractMenuGroupFrom(Menu menu) {
        return menu.getMenuGroup();
    }

    public static List<Product> extractProductsFrom(Menu menu) {
        List<Product> products = menu.getMenuProducts()
                .stream()
                .map(menuProduct -> menuProduct.getProduct())
                .collect(Collectors.toList());

        return products;
    }

    public static Product extractProductFrom(Menu menu) {
        MenuProduct menuProduct = menu.getMenuProducts().get(0);
        return menuProduct.getProduct();
    }
}
