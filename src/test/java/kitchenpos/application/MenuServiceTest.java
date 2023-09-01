package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.MockPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

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

    @BeforeEach
    void setUp() {

    }

    @DisplayName("메뉴 생성 성공")
    @Test
    void menu_create_success() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드, 양념치킨));

        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(33000))
                .name("치킨메뉴")
                .menuGroup(두마리메뉴)
                .menuGroupId(두마리메뉴.getId())
                .menuProducts(List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)))
                .displayed(true)
                .build();

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
        Menu menu = new MenuBuilder()
                .price(null)
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRICE_MORE_ZERO);
    }

    @DisplayName("메뉴 생성시 가격이 음수면 예외를 발생시킨다.")
    @Test
    void menu_create_price_negative() {
        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(-1))
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRICE_MORE_ZERO);
    }

    @DisplayName("메뉴는 메뉴그룹이 없으면 예외를 발생시킨다.")
    @Test
    void menu_create_not_found_menuGroup() {
        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(16000))
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_MENU_GROUP);
    }

    @DisplayName("메뉴의 메뉴상품목록이 null 이거나 비어있으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menu_create_null_menuProducts(List<MenuProduct> productList) {
        MenuGroup 한마리메뉴 = saveMenuGroup(한마리메뉴());
        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(16000))
                .menuGroup(한마리메뉴)
                .menuGroupId(한마리메뉴.getId())
                .menuProducts(productList)
                .build();

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

        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(16000))
                .menuGroup(두마리메뉴)
                .menuGroupId(두마리메뉴.getId())
                .menuProducts(List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)))
                .build();

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

        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(16000))
                .menuGroup(한마리메뉴)
                .menuGroupId(한마리메뉴.getId())
                .menuProducts(List.of(메뉴상품_양념_재고음수(양념치킨)))
                .build();

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

        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(16000))
                .menuGroup(두마리메뉴)
                .menuGroupId(두마리메뉴.getId())
                .menuProducts(List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)))
                .build();

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

        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(34000))
                .menuGroup(두마리메뉴)
                .menuGroupId(두마리메뉴.getId())
                .menuProducts(List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)))
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MENU_PRICE_MORE_PRODUCT);
    }

    @DisplayName("메뉴 이름에 비속어가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void menu_create_menu_name() {
        MenuGroup 두마리메뉴 = saveMenuGroup(두마리메뉴());
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = 양념치킨();
        saveProducts(List.of(후라이드, 양념치킨));

        Menu menu = new MenuBuilder()
                .price(BigDecimal.valueOf(33000))
                .name("비속어치킨메뉴")
                .menuGroup(두마리메뉴)
                .menuGroupId(두마리메뉴.getId())
                .menuProducts(List.of(메뉴상품_후라이드(후라이드), 메뉴상품_양념(양념치킨)))
                .build();

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MENU_NAME_CONTAINS_PURGOMALUM);
    }

    private MenuGroup saveMenuGroup(MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }

    private List<Product> saveProducts(List<Product> products) {
        products.forEach(productRepository::save);
        return products;
    }

}
