package kitchenpos.menu;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.menu.fixture.MenuFixture;
import kitchenpos.menu.fixture.MenuGroupFixture;
import kitchenpos.menu.fixture.MenuProductFixture;
import kitchenpos.menu.fixture.ProductFixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class MenuTestHelper {
    public static MenuProduct 메뉴_상품_A = MenuProductFixture.create(ProductFixture.상품_A,10);
    public static Menu 메뉴_A = MenuFixture.create(
            "메뉴", new BigDecimal(10000), MenuGroupFixture.메뉴_그룹_A, true, List.of(메뉴_상품_A)
    );
    public static Menu 메뉴_그룹_없는_메뉴 = MenuFixture.create(
            "메뉴", new BigDecimal(10000), null, true, List.of(메뉴_상품_A)
    );
    public static Menu 가격_없는_메뉴 = MenuFixture.create(
            "메뉴", null, null, true, List.of(메뉴_상품_A)
    );
    public static Menu 상품_없는_메뉴 = MenuFixture.create(
            "메뉴", new BigDecimal(10000), MenuGroupFixture.메뉴_그룹_A, true, null
    );
    public static Menu 이름_없는_메뉴 = MenuFixture.create(
            null, new BigDecimal(10000), MenuGroupFixture.메뉴_그룹_A, true, List.of(메뉴_상품_A)
    );
    public static Menu 부적절한_이름_메뉴 = MenuFixture.create(
            "fuck", new BigDecimal(10000), MenuGroupFixture.메뉴_그룹_A, true, List.of(메뉴_상품_A)
    );
    public static Menu 상품_가격보다_큰_메뉴 = MenuFixture.create(
            "메뉴", new BigDecimal(11000), MenuGroupFixture.메뉴_그룹_A, true, List.of(메뉴_상품_A)
    );

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
