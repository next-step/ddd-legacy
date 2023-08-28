package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.BDDMockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class MenuServiceTest {


    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
    }

    @DisplayName("메뉴 생성시 가격이 null 이면 예외를 발생시킨다.")
    @Test
    void menu_create_price_null() {
        menu.setPrice(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 생성시 가격이 음수면 예외를 발생시킨다.")
    @Test
    void menu_create_price_negative() {
        menu.setPrice(BigDecimal.valueOf(-1));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴는 메뉴그룹이 없으면 예외를 발생시킨다.")
    @Test
    void menu_create_not_found_menuGroup() {
        메뉴그룹반환(Optional.empty());
        menu.setPrice(BigDecimal.valueOf(16000));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 메뉴상품목록이 null 이거나 비어있으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menu_create_null_menuProducts(List<MenuProduct> productList) {
        메뉴그룹반환(Optional.of(MenuFixture.MenuGroupFixture.한마리메뉴()));
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(productList);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴상품의 수와 상품의 수가 다르면 예외를 발생시킨다.")
    @Test
    void menu_create_menuProducts_not_match_size() {
        메뉴그룹반환(Optional.of(MenuFixture.MenuGroupFixture.두마리메뉴()));
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(
                List.of(
                        MenuFixture.MenuProductFixture.메뉴상품_후라이드(ProductFixture.후라이드()),
                        MenuFixture.MenuProductFixture.메뉴상품_후라이드(ProductFixture.후라이드()))
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }
    @DisplayName("메뉴상품의 상품이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void menu_create_menuProducts_not_found_product() {
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = ProductFixture.양념치킨();
        메뉴그룹반환(Optional.of(MenuFixture.MenuGroupFixture.두마리메뉴()));
        상품목록반환(List.of(후라이드, 양념치킨));

        상품반환(후라이드, Optional.of(후라이드));
        상품반환(양념치킨, Optional.empty());
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(
                List.of(
                        MenuFixture.MenuProductFixture.메뉴상품_후라이드(후라이드),
                        MenuFixture.MenuProductFixture.메뉴상품_양념(양념치킨))
        );

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴상품의 수량이 음수면 예외를 발생시킨다.")
    @Test
    void menu_create_menuProducts_negative_quantity() {
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = ProductFixture.양념치킨();
        메뉴그룹반환(Optional.of(MenuFixture.MenuGroupFixture.두마리메뉴()));
        상품목록반환(List.of(후라이드, 양념치킨));
        상품반환(후라이드, Optional.of(후라이드));
        상품반환(양념치킨, Optional.of(양념치킨));
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(
                List.of(
                        MenuFixture.MenuProductFixture.메뉴상품_후라이드(후라이드),
                        MenuFixture.MenuProductFixture.메뉴상품_양념_재고음수(양념치킨))
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }


    @DisplayName("메뉴가격이 메뉴상품들의 가격의 합보다 크면 예외를 발생시킨다.")
    @Test
    void menu_create_menu_price_more_menuProducts_price_sum() {
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = ProductFixture.양념치킨();
        메뉴그룹반환(Optional.of(MenuFixture.MenuGroupFixture.두마리메뉴()));
        상품목록반환(List.of(후라이드, 양념치킨));
        상품반환(후라이드, Optional.of(후라이드));
        상품반환(양념치킨, Optional.of(양념치킨));
        menu.setPrice(후라이드.getPrice().add(양념치킨.getPrice()).add(BigDecimal.valueOf(10000)));
        menu.setMenuProducts(
                List.of(
                        MenuFixture.MenuProductFixture.메뉴상품_후라이드(후라이드),
                        MenuFixture.MenuProductFixture.메뉴상품_양념(양념치킨))
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 이름에 비속어가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void menu_create_menu_name() {
        Product 후라이드 = ProductFixture.후라이드();
        Product 양념치킨 = ProductFixture.양념치킨();
        메뉴그룹반환(Optional.of(MenuFixture.MenuGroupFixture.두마리메뉴()));
        상품목록반환(List.of(후라이드, 양념치킨));
        상품반환(후라이드, Optional.of(후라이드));
        상품반환(양념치킨, Optional.of(양념치킨));
        BDDMockito.given(purgomalumClient.containsProfanity(any())).willReturn(true);
        menu.setPrice(후라이드.getPrice().add(양념치킨.getPrice()).add(BigDecimal.valueOf(33000)));
        menu.setMenuProducts(
                List.of(
                        MenuFixture.MenuProductFixture.메뉴상품_후라이드(후라이드),
                        MenuFixture.MenuProductFixture.메뉴상품_양념(양념치킨))
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    private void 메뉴그룹반환(Optional<MenuGroup> menuGroup) {
        BDDMockito.given(menuGroupRepository.findById(any())).willReturn(menuGroup);
    }

    private void 상품반환(Product product, Optional<Product> optionalProduct) {
        BDDMockito.given(productRepository.findById(product.getId())).willReturn(optionalProduct);
    }

    private void 상품목록반환(List<Product> products) {
        BDDMockito.given(productRepository.findAllByIdIn(any())).willReturn(products);
    }

}
