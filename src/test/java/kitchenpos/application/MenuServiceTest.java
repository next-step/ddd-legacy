package kitchenpos.application;

import kitchenpos.ApplicationServiceTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest extends ApplicationServiceTest {

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

    static class create_source {
        static Object[][] create_fail_because_illegal_price() {
            return new Object[][]{
                {MenuFixture.create("역마진 치킨 세트", BigDecimal.valueOf(-1_000L), new ArrayList<>())},
                {MenuFixture.create("null 가격  치킨 세트", null, new ArrayList<>())}
            };
        }
    }

    @DisplayName("메뉴를 등록합니다.")
    @Nested
    class create {

        @DisplayName("[정상] 메뉴가 정상적으로 등록됩니다.")
        @Test
        void create_success() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), products);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menu.getMenuGroup()));
            when(productRepository.findAllByIdIn(any())).thenReturn(products);
            products.forEach(
                product -> when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product))
            );
            when(purgomalumClient.containsProfanity(any())).thenReturn(false);
            when(menuRepository.save(any())).thenReturn(menu);

            menuService.create(menu);
        }

        @DisplayName("[예외] 등록 요청한 메뉴의 가격이 null 이거나 0원 미만 입니다.")
        @MethodSource("kitchenpos.application.MenuServiceTest$create_source#create_fail_because_illegal_price")
        @ParameterizedTest
        void create_fail_because_illegal_price(Menu menu) {
            assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 등록 요청한 메뉴의 메뉴 구성 상품이 존재하지 않습니다.")
        @Test
        void create_fail_because_menu_products_do_not_exist() {
            Menu menu = MenuFixture.create("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), new ArrayList<>());

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menu.getMenuGroup()));

            assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴를 구성하는 메뉴 구성 상품과 매치되는 상품 중 일부가 존재하지 않습니다.")
        @Test
        void create_fail_because_some_product_do_not_exist() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Product firstProduct = products.get(0);
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), products);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menu.getMenuGroup()));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(firstProduct));

            assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴를 구성하는 일부 메뉴 구성 상품의 개수가 0개 미만 입니다.")
        @Test
        void create_fail_because_some_quantity_of_menu_product_is_minus() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), products);
            menu.getMenuProducts().get(1).setQuantity(0L);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menu.getMenuGroup()));
            when(productRepository.findAllByIdIn(any())).thenReturn(products);
            products.forEach(
                product -> when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product))
            );

            assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴의 가격의 `sum(메뉴를 구성하는 상품의 가격 * 개수)` 보다 큽니다.")
        @Test
        void create_fail_because_menu_price_is_over_than_sum_of_menu_product_price() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(20_001L), products);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menu.getMenuGroup()));
            when(productRepository.findAllByIdIn(any())).thenReturn(products);
            products.forEach(
                product -> when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product))
            );

            assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴의 이름에 비속어가 포함되어서는 안됩니다.")
        @Test
        void create_fail_because_menu_name_is_purgomalum() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), products);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menu.getMenuGroup()));
            when(productRepository.findAllByIdIn(any())).thenReturn(products);
            products.forEach(
                product -> when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product))
            );
            when(purgomalumClient.containsProfanity(any())).thenReturn(false);

            menuService.create(menu);
        }
    }

    static class changePrice_source {
        static Object[][] create_success() {
            return new Object[][]{
                {BigDecimal.valueOf(1_999L)},
                {BigDecimal.valueOf(2_000L)}
            };
        }

        static Object[][] create_fail_because_menu_price_is_null_or_minus() {
            return new Object[][]{
                {MenuFixture.create("", null, new ArrayList<>())},
                {MenuFixture.create("", BigDecimal.valueOf(-1), new ArrayList<>())},
            };
        }
    }

    /**
     * 구현된 로직과 실제 올바르다고 생각되는 로직에 차이가 있어 보입니다.
     * - 현재 구현된 로직: 변경되는 가격은 개별 구성 상품 가격 * 개수 보다 작거나 같아야 한다.
     * - 추측되는 올바른 로직(?): 변경되는 가격은 `sum(메뉴를 구성하는 상품의 가격 * 갯수)` 보다 작거나 같아야 한다.
     * 의도된 것으로 생각하고 우선 테스트로 보호합니다!
     */
    @DisplayName("메뉴의 가격을 변경합니다.")
    @Nested
    class changePrice {
        @DisplayName("[정상] 메뉴의 가격이 정상적으로 변경됩니다.")
        @MethodSource("kitchenpos.application.MenuServiceTest$changePrice_source#create_success")
        @ParameterizedTest
        void create_success(BigDecimal changedPrice) {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(19_000L), products);
            Menu changingPriceMenu = MenuFixture.create(
                menu.getId(), "후라이드 치킨 세트", changedPrice, menu.getMenuProducts(), true
            );

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            menuService.changePrice(menu.getId(), changingPriceMenu);
        }

        @DisplayName("[예외] 변경되는 메뉴의 가격은 null 이거나 음수 일 수 없습니다.")
        @MethodSource("kitchenpos.application.MenuServiceTest$changePrice_source#create_fail_because_menu_price_is_null_or_minus")
        @ParameterizedTest
        void create_fail_because_menu_price_is_null_or_minus(Menu menu) {
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 변경되는 메뉴의 가격은 개별 구성 상품 가격 * 개수 보다 클 수 없습니다.")
        @Test
        void create_fail_because_menu_price_is_over_than_each_menu_product_price_multiply_quantity() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(20_000L), products);
            Menu changingPriceMenu = MenuFixture.create(
                menu.getId(), "후라이드 치킨 세트", BigDecimal.valueOf(2_001L), menu.getMenuProducts(), true
            );

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changingPriceMenu))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    /**
     * 구현된 로직과 실제 올바르다고 생각되는 로직에 차이가 있어 보입니다.
     * - 현재 구현된 로직: 메뉴 전시시 메뉴의 가격은 개별 구성 상품 가격 * 개수 보다 작거나 같아야 한다.
     * - 추측되는 올바른 로직(?): 메뉴 전시시 메뉴의 가격은 `sum(메뉴를 구성하는 상품의 가격 * 갯수)` 보다 작거나 같아야 한다.
     * 의도된 것으로 생각하고 우선 테스트로 보호합니다!
     */
    @DisplayName("메뉴를 전시합니다.")
    @Nested
    class display {
        @DisplayName("[정상] 메뉴가 정상적으로 전시됩니다.")
        @Test
        void display_success() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(2_000L), products);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            Menu actualResult = menuService.display(menu.getId());
            assertTrue(actualResult.isDisplayed());
        }

        @DisplayName("[정상] 메뉴가 전시시 메뉴의 가격이 개별 구성 상품 가격 * 개수 보다 크면 전시가 불가합니다.")
        @Test
        void display_fail_because_menu_price_is_over_than_each_menu_product_price_multiply_quantity() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(2_001L), products);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("메뉴를 숨깁니다.")
    @Nested
    class hide {
        @DisplayName("[정상] 메뉴가 정상적으로 숨겨집니다.")
        @Test
        void hide_success() {
            List<Product> products = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
            );
            Menu menu = MenuFixture.createWithProducts("후라이드 치킨 세트", BigDecimal.valueOf(2_000L), products);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            Menu actualResult = menuService.hide(menu.getId());
            assertFalse(actualResult.isDisplayed());
        }
    }

}