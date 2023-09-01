package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.MockPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.domain.MenuFixture.MenuGroupFixture.두마리메뉴;
import static kitchenpos.domain.MenuFixture.MenuGroupFixture.한마리메뉴;
import static kitchenpos.domain.MenuFixture.MenuProductFixture.*;
import static kitchenpos.domain.ProductFixture.양념치킨;
import static kitchenpos.domain.ProductFixture.후라이드;
import static kitchenpos.exception.MenuExceptionMessage.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MenuServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final MenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
    private final ProductRepository productRepository = new FakeProductRepository();
    private final PurgomalumClient purgomalumClient = new MockPurgomalumClient();
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    @DisplayName("메뉴 생성 성공")
    @Test
    void menu_create_success() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드, 양념치킨));
        Menu menu = createMenu(33000, "치킨메뉴", 두마리메뉴, List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)), true);

        Menu result = menuService.create(menu);

        Menu savedMenu = menuRepository.findById(result.getId())
                .get();

        assertThat(savedMenu.getId()).isEqualTo(result.getId());
        assertThat(savedMenu.getName()).isEqualTo(result.getName());
        assertThat(savedMenu.getPrice()).isEqualTo(result.getPrice());
        assertThat(savedMenu.isDisplayed()).isEqualTo(result.isDisplayed());
    }

    @DisplayName("메뉴 생성시 가격이 null 이면 예외를 발생시킨다.")
    @Test
    void menu_create_price_null() {
        Menu menu = createMenu(null, "치킨메뉴", null, Collections.emptyList(), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRICE_MORE_ZERO);
    }

    @DisplayName("메뉴 생성시 가격이 음수면 예외를 발생시킨다.")
    @Test
    void menu_create_price_negative() {
        Menu menu = createMenu(-1, "치킨메뉴", null, Collections.emptyList(), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRICE_MORE_ZERO);
    }

    @DisplayName("메뉴는 메뉴그룹이 없으면 예외를 발생시킨다.")
    @Test
    void menu_create_not_found_menuGroup() {
        Menu menu = createMenu(16000, "치킨메뉴", null, Collections.emptyList(), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_MENU_GROUP);
    }

    @DisplayName("메뉴의 메뉴상품목록이 null 이거나 비어있으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menu_create_null_menuProducts(List<MenuProduct> productList) {
        MenuGroup 한마리메뉴 = saveMenuGroup(한마리메뉴());
        Menu menu = createMenu(16000, "치킨메뉴", 한마리메뉴, Collections.emptyList(), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EMPTY_MENU_PRODUCT);
    }

    @DisplayName("메뉴상품의 수와 상품의 수가 다르면 예외를 발생시킨다.")
    @Test
    void menu_create_menuProducts_not_match_size() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = 후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드));
        Menu menu = createMenu(16000, "치킨메뉴", 두마리메뉴, List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_EQUAL_MENU_PRODUCT_SIZE);
    }

    @DisplayName("메뉴상품의 수량이 음수면 예외를 발생시킨다.")
    @Test
    void menu_create_menuProducts_negative_quantity() {
        MenuGroup 한마리메뉴 = saveMenuGroup(한마리메뉴());
        Product 양념치킨 = ProductFixture.양념치킨();
        saveProducts(List.of(양념치킨));
        Menu menu = createMenu(16000, "치킨메뉴", 한마리메뉴, List.of(메뉴상품_양념_재고음수(양념치킨)), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ILLEGAL_QUANTITY);
    }

    @DisplayName("메뉴상품의 상품이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void menu_create_menuProducts_not_found_product() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드));
        Menu menu = createMenu(16000, "치킨메뉴", 두마리메뉴, List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_EQUAL_MENU_PRODUCT_SIZE);
    }


    @DisplayName("메뉴가격이 메뉴상품들의 가격의 합보다 크면 예외를 발생시킨다.")
    @Test
    void menu_create_menu_price_more_menuProducts_price_sum() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드, 양념치킨));
        Menu menu = createMenu(34000, "치킨메뉴", 두마리메뉴, List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MENU_PRICE_MORE_PRODUCTS_SUM);
    }

    @DisplayName("메뉴 이름에 비속어가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void menu_create_menu_name() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드, 양념치킨));
        Menu menu = createMenu(33000, "비속어치킨메뉴", 두마리메뉴, List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)), true);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MENU_NAME_CONTAINS_PURGOMALUM);
    }

    /*
        메뉴가격변경 request의 가격과 메뉴상품 가격의 합을 비교하는 로직이 for문 바깥에 있어야 한다.
     */
    @DisplayName("메뉴 가격 변경 성공")
    @Test
    void menu_price_change_success() {
        Menu menu = saveMenu(createMenu(33000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), true));
        Menu request = new MenuBuilder()
                .price(BigDecimal.valueOf(10000))
                .build();

        Menu result = menuService.changePrice(menu.getId(), request);

        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @DisplayName("메뉴 가격 변경시 가격이 null 이면 예외를 발생시킨다.")
    @Test
    void menu_price_change_price_null() {
        Menu menu = createMenu(33000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), true);
        Menu request = new MenuBuilder()
                .price(null)
                .build();

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRICE_MORE_ZERO);
    }

    @DisplayName("메뉴 가격 변경시 가격이 음수면 예외를 발생시킨다.")
    @Test
    void menu_price_change_price_negative() {
        Menu menu = createMenu(33000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), true);
        Menu request = new MenuBuilder()
                .price(BigDecimal.valueOf(-1))
                .build();

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRICE_MORE_ZERO);
    }

    @DisplayName("메뉴 가격 변경시 메뉴가 없으면 예외를 발생시킨다.")
    @Test
    void menu_price_change_not_found_menu() {
        Menu request = new MenuBuilder()
                .price(BigDecimal.valueOf(1000))
                .build();

        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_MENU);
    }

    @DisplayName("메뉴 가격 변경시 변경하려는 금액이 메뉴상품들 합보다 크면 예외를 발생시킨다.")
    @Test
    void menu_price_change_price_more_menuProducts_price_sum() {
        Menu menu = saveMenu(
                createMenu(33000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), true)
        );

        Menu request = new MenuBuilder()
                .id(UUID.randomUUID())
                .price(BigDecimal.valueOf(35000))
                .build();

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MENU_PRICE_MORE_PRODUCTS_SUM);
    }

    /*
        메뉴 가격과 메뉴상품들 가격 합을 비교하는 로직이 for문 바깥에 있어야 한다.
     */
    @DisplayName("메뉴 노출로 변경 성공.")
    @Test
    void menu_display_success() {
        saveMenu(
                createMenu(33000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), false)
        );

        Menu savedMenu = saveMenu(new MenuBuilder()
                .price(BigDecimal.valueOf(10000))
                .name("치킨메뉴")
                .menuGroup(두마리메뉴())
                .menuGroupId(두마리메뉴().getId())
                .menuProducts(List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())))
                .displayed(false)
                .build());

        Menu result = menuService.display(savedMenu.getId());

        assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 노출로 변경시 메뉴가 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void menu_display_not_found_menu() {
        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_MENU);
    }

    @DisplayName("메뉴 노출로 변경시 메뉴 금액이 메뉴상품들 가격 합보다 크면 예외를 발생시킨다.")
    @Test
    void menu_display_price_more_menuProducts_price_sum() {
        Menu savedMenu = saveMenu(
                createMenu(35000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), false)
        );

        assertThatThrownBy(() -> menuService.display(savedMenu.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(MENU_PRICE_MORE_PRODUCTS_SUM);
    }

    @DisplayName("메뉴 비노출 성공")
    @Test
    void menu_hide_success() {
        Menu savedMenu = saveMenu(
                createMenu(33000, "치킨메뉴", 두마리메뉴(), List.of(메뉴상품_후라이드(후라이드()), 메뉴상품_양념(양념치킨())), true)
        );

        Menu result = menuService.hide(savedMenu.getId());

        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 비노출로 변경시 메뉴가 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void menu_hide_not_found_menu() {
        assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_MENU);
    }


    private MenuGroup saveMenuGroup(MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }

    private List<Product> saveProducts(List<Product> products) {
        products.forEach(productRepository::save);
        return products;
    }

    private Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu createMenu(Integer price, String name, MenuGroup menuGroup, List<MenuProduct> products, boolean display) {
        return new MenuBuilder()
                .price(createPrice(price))
                .name(name)
                .menuGroup(menuGroup)
                .menuGroupId(createMenuGroupId(menuGroup))
                .menuProducts(products)
                .displayed(display)
                .build();
    }

    private UUID createMenuGroupId(MenuGroup menuGroup) {
        if (menuGroup == null) {
            return null;
        } else {
            return menuGroup.getId();
        }
    }

    private BigDecimal createPrice(Integer price) {
        if (price == null) {
            return null;
        }
        return BigDecimal.valueOf(price);
    }

}
