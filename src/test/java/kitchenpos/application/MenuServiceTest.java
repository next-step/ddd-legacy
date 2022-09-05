package kitchenpos.application;

import kitchenpos.application.support.TestFixture;
import kitchenpos.domain.*;
import kitchenpos.domain.Menu;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;


    @DisplayName("메뉴를 생성 할 수 있다.")
    @Test
    void create_menu() {
        final Menu menu = TestFixture.createFirstMenu();

        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenuGroup()));
        given(productRepository.findAllByIdIn(Mockito.any()))
                .willReturn(List.of(TestFixture.createFirstProduct()));
        given(productRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstProduct()));
        given(purgomalumClient.containsProfanity(Mockito.any(String.class)))
                .willReturn(false);
        given(menuRepository.save(Mockito.any(Menu.class)))
                .willReturn(menu);

        final Menu result = menuService.create(menu);
        assertThat(result).isNotNull();
    }

    @DisplayName("가격이 null이라면 IllegalArgumentException을 발생시킨다")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = -1)
    void create_menu_with_null_and_empty_price(final Long price) {
        Menu menu = TestFixture.createFirstMenu();
        BigDecimal wrapPrice = Optional.ofNullable(price)
                .map(BigDecimal::new)
                .orElse(null);
        menu.setPrice(wrapPrice);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격이 음수라면 IllegalArgumentException을 발생시킨다")
    @ParameterizedTest
    @ValueSource(longs = -1)
    void create_menu_with_negative_number(final long price) {
        Menu menu = TestFixture.createFirstMenu();
        BigDecimal wrapPrice = Optional.ofNullable(price)
                .map(BigDecimal::new)
                .orElse(null);
        menu.setPrice(wrapPrice);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 속한 상품의 가격보다 등록하려는 가격이 크다면 IllegalArgumentException를 발생시킨다")
    @Test
    void create_menu_with_lower_price() {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenuGroup()));
        given(productRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(TestFixture.createFirstMenuProducts());
        Product product = TestFixture.createFirstProduct();
        product.setPrice(BigDecimal.ONE);
        given(productRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(product));

        Menu menu = TestFixture.createFirstMenu();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 이름이 null이거나 비어있다면 IllegalArgumentException을 발생시킨다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_menu_with_null_and_empty_name(final String name) {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenuGroup()));

        Menu menu = TestFixture.createFirstMenu();
        menu.setName(name);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 속할 상품이 등록되어 있지 않은 상품이라면 메뉴 생성이 불가능하다")
    @Test
    void create_menu_with_not_exist_product() {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createSecondMenuGroup()));
        given(productRepository.findAllByIdIn(Mockito.any()))
                .willReturn(List.of());

        final Menu menu = TestFixture.createFirstMenu();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 속할 상품이 null이거나 비어있다면 IllegalArgumentException을 발생시킨다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_menu_with_null_and_empty_menu_products(final List<MenuProduct> menuProducts) {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenuGroup()));

        Menu menu = TestFixture.createFirstMenu();
        menu.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 속할 상품의 개수가 0보다 작다면 IllegalArgumentException을 발생시킨다")
    @ValueSource(longs = {-1, 0})
    @ParameterizedTest
    void create_menu_with_zero_quantity_menu_product(final long quantity) {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenuGroup()));
        given(productRepository.findAllByIdIn(Mockito.any()))
                .willReturn(List.of(TestFixture.createFirstProduct()));

        List<MenuProduct> menuProducts = TestFixture.createFirstMenuProducts();
        menuProducts.stream()
                .forEach(menuProduct -> menuProduct.setQuantity(quantity));

        Menu menu = TestFixture.createFirstMenu();
        menu.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 그룹에 속하지 않는다면 NoSuchElementException을 발생시킨다")
    @Test
    void create_menu_must_contain_one_menu() {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.empty());

        final Menu menu = TestFixture.createFirstMenu();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격을 수정할 수 있다")
    @Test
    void change_price() {
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenu()));

        final BigDecimal updatePrice = BigDecimal.valueOf(9);
        Menu updateMenu = TestFixture.createFirstMenu();
        updateMenu.setPrice(updatePrice);

        Menu result = menuService.changePrice(updateMenu.getId(), updateMenu);
        assertThat(result.getPrice()).isEqualTo(updatePrice);
    }


    @DisplayName("메뉴에 속한 개별 상품의 가격보다 변경하려는 가격이 크다면 IllegalArgumentException을 발생시킨다")
    @Test
    void change_price_bigger_than_contains_products() {
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(TestFixture.createFirstMenu()));

        final BigDecimal updatePrice = BigDecimal.valueOf(10000);
        Menu updateMenu = TestFixture.createFirstMenu();
        updateMenu.setPrice(updatePrice);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(updateMenu.getId(), updateMenu));
    }

    @DisplayName("메뉴를 비공개할 수 있다")
    @Test
    void hide() {
        final Menu menu = TestFixture.createFirstMenu();
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        final Menu result = menuService.hide(menu.getId());
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 공개 할 수 있다.")
    @Test
    void show() {
        final Menu menu = TestFixture.createFirstMenu();
        menu.setDisplayed(false);
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        final Menu result = menuService.display(menu.getId());
        assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("생성된 메뉴를 조회할 수 있다")
    @Test
    void select_all_menus() {
        final List<Menu> defaultMenus = List.of(TestFixture.createFirstMenu(), TestFixture.createSecondMenu());
        given(menuRepository.findAll())
                .willReturn(defaultMenus);

        final List<Menu> results = menuService.findAll();
        assertThat(defaultMenus).isEqualTo(results);
    }
}
