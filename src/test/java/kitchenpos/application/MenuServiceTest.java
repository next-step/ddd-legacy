package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
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

    @DisplayName("메뉴등록로직 create()를 검증한다.")
    @Nested
    class create {
        @DisplayName("메뉴를 등록할 수 있다.")
        @Test
        void create() {
            // given
            MenuGroup menuGroup = setupMenuGroupMock();
            Product product = setupProductMock("후라이드", 16_000L);
            MenuProduct menuProduct = createMenuProduct(product, 2L);
            Menu request = createMenuRequest(menuProduct, menuGroup, 20_000L, "치킨세트", true);
            Menu response = createMenu(menuProduct, menuGroup, 20_000L, "치킨세트", true);

            given(menuRepository.save(any())).willReturn(response);

            // when
            Menu actual = menuService.create(request);

            // then
            assertThat(actual.getId()).isNotNull();
        }

        @DisplayName("메뉴의 가격의 NULL 또는 0보다 작으면 예외(IllegalArgumentException)를 발생시킨다.")
        @ParameterizedTest
        @NullSource
        @MethodSource("lessThanZero")
        void create_price_Null_Negative(BigDecimal price) {
            // given
            Menu request = createMenuRequest(price, "치킨세트", true);

            // when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private static Stream<BigDecimal> lessThanZero() {
            return Stream.of(
                    BigDecimal.valueOf(-1),
                    BigDecimal.valueOf(-10)
            );
        }

        @DisplayName("메뉴가 메뉴그룹에 하나라도 포함되지 않으면 예외(NoSuchElementException)를 발생시킨다.")
        @Test
        void menu_not_contain_menuGroup() {
            // given
            Menu request = createMenuRequest(BigDecimal.valueOf(20_000), "후라이드", true);

            // when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 구성 상품이 없을 시 예외(IllegalArgumentException)를 발생시킨다.")
        @Test
        void menu_not_product() {
            // given
            MenuGroup menuGroup = setupMenuGroupMock();
            Menu request = createMenuRequest(menuGroup, BigDecimal.valueOf(20_000), "후라이드", true);

            // when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 등록의 상품의 수량이 0이하일 경우 예외(IllegalArgumentException)를 발생시킨다.")
        @Test
        void menuProduct_quantity_lessZero() {
            // given
            MenuGroup menuGroup = setupMenuGroupMock();
            Product product = setupProductMock("후라이드", 16_000L);
            MenuProduct menuProduct = createMenuProduct(product, 0);
            Menu request = createMenuRequest(menuProduct, menuGroup, 20_000L, "후라이트 세트", true);

            // when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 가격이 구성 상품의 가격의 총합보다 클 수 없다.(IllegalArgumentException)")
        @Test
        void createExpensiveMenu() {
            // given
            MenuGroup menuGroup = setupMenuGroupMock();
            Product product = setupProductMock("후라이드", 16_000L);
            MenuProduct menuProduct = createMenuProduct(product, 1L);
            Menu request = createMenuRequest(menuProduct, menuGroup, 17_000L, "후라이트 세트", true);

            // when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름이 올바르지 않을 시 예외(IllegalArgumentException)를 발생시킨다.")
        @NullSource
        @ValueSource(strings = {"비속어", "욕설이 포함된 이름"})
        @ParameterizedTest
        void create_incorrect_name(String name) {
            // given
            MenuGroup menuGroup = setupMenuGroupMock();
            Product product = setupProductMock("후라이드", 16_000L);
            MenuProduct menuProduct = createMenuProduct(product, 2L);
            Menu request = createMenuRequest(menuProduct, menuGroup, 20_000L, name, true);

            if (name != null) {
                given(purgomalumClient.containsProfanity(name)).willReturn(true);
            }

            // when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("MenuService.changePrice 메서드 테스트")
    @Nested
    class changePrice {
        @DisplayName("메뉴의 가격을 변경할 수 있다.")
        @Test
        void changePrice() {
            // given
            Product product = createProduct("후라이드", 10_000L);
            MenuProduct menuProduct = createMenuProduct(product, 2L);
            Menu menu = createMenu(menuProduct, null, 20_000L, "치킨세트", true);
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            Menu request = createMenuRequest(BigDecimal.valueOf(10_000L), "치킨세트", true);

            // when
            Menu changedMenu = menuService.changePrice(request.getId(), request);

            // then
            assertThat(changedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(10_000L));
        }

        @DisplayName("수정할 가격이 0보다 작을 수 없다.(IllegalArgumentException)")
        @Test
        void changePrice_priceNegative() {
            // given
            Menu request = createMenuRequest(BigDecimal.valueOf(-1), "치킨세트", true);

            // when
            assertThatThrownBy(() -> menuService.changePrice(request.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("수정할 가격이 null 일 수 없다.(IllegalArgumentException)")
        @NullSource
        @ParameterizedTest
        void changePrice_priceNull(BigDecimal price) {
            // given
            Menu request = createMenuRequest(price, "치킨세트", true);

            // when
            assertThatThrownBy(() -> menuService.changePrice(request.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("수정할 가격이 구성상품의 총합보다 클 수 없다.(IllegalArgumentException)")
        @Test
        void changePrice_exceedsTotalProductPrice() {
            // given
            Product product = createProduct("후라이드", 10_000L);
            MenuProduct menuProduct = createMenuProduct(product, 2L);
            Menu menu = createMenu(menuProduct, null, 20_000L, "치킨세트", true);
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            Menu request = createMenuRequest(BigDecimal.valueOf(21_000L), "치킨세트", true);

            // when then
            assertThatThrownBy(() -> menuService.changePrice(request.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("menuService.display 메서드 테스트")
    @Nested
    class display {
        @DisplayName("메뉴를 노출할 수 있다.")
        @Test
        void display() {
            // given
            Product product = createProduct("후라이드", 10_000L);
            MenuProduct menuProduct = createMenuProduct(product, 2L);
            Menu menu = createMenu(menuProduct, null, 20_000L, "치킨세트", false);
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when
            Menu displayedMenu = menuService.display(menu.getId());

            // then
            assertThat(displayedMenu.isDisplayed()).isTrue();
        }

        @DisplayName("메뉴의 가격이 메뉴에 속한 상품 금액의 합보다 높을 경우 메뉴를 노출할 수 없다.(IllegalStateException)")
        @Test
        void displayExpensiveMenu() {
            // given
            Product product = createProduct("후라이드", 10_000L);
            MenuProduct menuProduct = createMenuProduct(product, 2L);
            Menu menu = createMenu(menuProduct, null, 21_000L, "치킨세트", false);
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when then
            assertThatThrownBy(() -> menuService.display(menu.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }


    @DisplayName("메뉴를 비노출 시킬 수 있다.")
    @Test
    void hide() {
        Menu menu = createMenuRequest(BigDecimal.valueOf(10_000), "치킨세트", true);
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        Menu hiddenMenu = menuService.hide(menu.getId());
        assertThat(hiddenMenu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Menu menu1 = createMenuRequest(BigDecimal.valueOf(10_000), "치킨세트1", true);
        Menu menu2 = createMenuRequest(BigDecimal.valueOf(10_001), "치킨세트2", true);
        given(menuRepository.findAll()).willReturn(List.of(menu1, menu2));

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertThat(menus).hasSize(2)
                .extracting("price", "name")
                .containsExactlyInAnyOrder(
                        tuple(menu1.getPrice(), menu1.getName()),
                        tuple(menu2.getPrice(), menu2.getName())
                );
    }

    private static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }

    private static Product createProduct(final String name, final long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private static Menu createMenuRequest(MenuGroup menuGroup, BigDecimal price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setMenuGroup(menuGroup);
        request.setPrice(price);
        request.setName(name);
        request.setDisplayed(displayed);
        return request;
    }

    private static Menu createMenuRequest(BigDecimal price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setPrice(price);
        request.setName(name);
        request.setDisplayed(displayed);
        return request;
    }

    private static Menu createMenuRequest(MenuProduct menuProduct, MenuGroup menuGroup,
                                          long price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(price));
        request.setName(name);
        request.setMenuProducts(List.of(menuProduct));
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        return request;
    }

    private static Menu createMenu(MenuProduct menuProduct, MenuGroup menuGroup,
                                   long price, String name, boolean displayed) {
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(price));
        request.setName(name);
        request.setMenuProducts(List.of(menuProduct));
        request.setDisplayed(displayed);
        request.setMenuGroup(menuGroup);
        return request;
    }

    private MenuGroup setupMenuGroupMock() {
        MenuGroup menuGroup = createMenuGroup();
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        return menuGroup;
    }

    private Product setupProductMock(String name, long price) {
        Product product = createProduct(name, price);
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        return product;
    }

}
