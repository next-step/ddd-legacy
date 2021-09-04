package kitchenpos.fixture;

import kitchenpos.application.InMemoryMenuRepository;
import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class MenuFixture {

    public static MenuRepository menuRepository = new InMemoryMenuRepository();

    public static MenuProduct 메뉴상품() {
        final Product product = ProductFixture.상품저장();
        return 상품으로_메뉴상품만들기(product);
    }

    public static MenuProduct 등록되지않은_메뉴상품() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        return 상품으로_메뉴상품만들기(product);
    }

    public static MenuProduct 수량이음수인_메뉴상품() {
        final Product product = ProductFixture.상품저장();
        final MenuProduct menuProduct = 상품으로_메뉴상품만들기(product);
        menuProduct.setQuantity(-1);
        return menuProduct;
    }

    private static MenuProduct 상품으로_메뉴상품만들기(Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static Menu 메뉴() {
        final Menu menu = new Menu();
        final MenuGroup menuGroup = MenuGroupFixture.메뉴그룹저장();
        List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(메뉴상품(), 메뉴상품()));
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(10_000L));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu 메뉴저장() {
        final Menu menu = 메뉴();
        return 메뉴저장(menu);
    }

    public static Menu 메뉴저장(Menu menu) {
        menu.setId(randomUUID());
        return menuRepository.save(menu);
    }

    public static void 비우기() {
        menuRepository = new InMemoryMenuRepository();
    }
}
