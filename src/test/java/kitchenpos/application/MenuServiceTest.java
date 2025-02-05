package kitchenpos.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.web.client.RestTemplateBuilder;

class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    private Product product;
    private MenuGroup menuGroup;
    private Menu menu;
    private MenuProduct menuProduct;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        purgomalumClient = new FakePurgomalumClient(new RestTemplateBuilder());

        menuService = new MenuService(menuRepository,
                                      menuGroupRepository,
                                      productRepository,
                                      purgomalumClient);

        product = TestFixture.createProduct("product", BigDecimal.valueOf(10));
        menuGroup = TestFixture.createMenuGroup("menuGroup");
        menuProduct = TestFixture.createMenuProduct(1, product);
        menu = TestFixture.createMenu("menu", BigDecimal.valueOf(10), menuGroup, menuProduct);
    }

    @Nested
    class Create {

        @BeforeEach
        public void setUp() {

            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
        }

        @DisplayName("메뉴를 등록 할 수 있다.")
        @Test
        void create() {
            // when
            Menu created = menuService.create(menu);

            // then
            assertNotNull(created);
        }

        @DisplayName("메뉴의 가격은 필수 값 이다")
        @ParameterizedTest
        @MethodSource("provideInvalidPrices")
        void createWithInvalidPrice(BigDecimal price) {
            // given
            menu.setPrice(price);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴의 메뉴그룹은 필수 값 이다.")
        @Test
        void createWithNonExistentMenuGroup() {
            // given
            MenuGroup nonExistentMenuGroup = new MenuGroup();
            nonExistentMenuGroup.setId(UUID.randomUUID());
            menu.setMenuGroup(nonExistentMenuGroup);
            menu.setMenuGroupId(nonExistentMenuGroup.getId());

            // when & then
            assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴의 상품(최소 1개)은 필수 값 이다")
        @ParameterizedTest
        @MethodSource("provideNonExistentMenuProducts")
        void createWithNonExistentProduct(List<MenuProduct> menuProducts) {
            // given
            menu.setMenuProducts(menuProducts);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴의 이름에는 비속어를 넣을 수 없다.")
        @ParameterizedTest
        @MethodSource("provideInvalidNames")
        void createWithInvalidName(String menuName) {
            // given
            menu.setName(menuName);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("제품의 수량은 0개보다 큰 값이어야 한다.")
        @Test
        void createWithNegativeQuantity() {
            // given
            menuProduct.setQuantity(-1);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName(" 메뉴의 가격은 메뉴에 속한 상품목록의 총 가격(가격 곱하기 수량의 합)보다 클 수 없다.")
        @Test
        void createWithPriceGreaterThanTotalPrice() {
            // given
            menu.setPrice(BigDecimal.valueOf(100));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        private static Stream<List<MenuProduct>> provideNonExistentMenuProducts() {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(UUID.randomUUID());
            return Stream.of(List.of(), List.of(menuProduct));
        }

        private static Stream<String> provideInvalidNames() {
            return Stream.of(null, "비속어");
        }

        private static Stream<BigDecimal> provideInvalidPrices() {
            return Stream.of(
                null,
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(-1));
        }
    }

    @Nested
    class ChangePrice {

        @BeforeEach
        public void setUp() {
            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
            menuRepository.save(menu);
        }

        @DisplayName("메뉴의 가격을 변경 할 수 있다")
        @Test
        void changePrice() {
            // given
            BigDecimal newPrice = BigDecimal.valueOf(8);
            menu.setPrice(newPrice);

            // when
            Menu changed = menuService.changePrice(menu.getId(), menu);

            // then
            assertEquals(newPrice, changed.getPrice());
        }

        @DisplayName("메뉴의 가격은 0원보다 큰 값이어야 한다.")
        @ParameterizedTest
        @MethodSource("provideInvalidPrices")
        void changePriceWithInvalidPrice(BigDecimal price) {
            // given
            menu.setPrice(price);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        @DisplayName("메뉴의 가격은 메뉴에 속한 상품목록의 총 가격(가격 곱하기 수량의 합)보다 클 수 없다.")
        @Test
        void createWithPriceGreaterThanTotalPrice() {
            // given
            menu.setPrice(BigDecimal.valueOf(100));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        private static Stream<BigDecimal> provideInvalidPrices() {
            return Stream.of(
                null,
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(-1));
        }
    }

    @Nested
    class Display {

        @BeforeEach
        public void setUp() {
            menu.setDisplayed(false);
            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
        }

        @DisplayName("메뉴을 노출 처리 할 수 있다.")
        @Test
        void display() {
            // given
            menuRepository.save(menu);

            // when
            Menu displayed = menuService.display(menu.getId());

            // then
            assertTrue(displayed.isDisplayed());
        }

        @DisplayName("메뉴의 가격이 메뉴에 속한 상품목록의 총 가격(가격 곱하기 수량의 합)보다 클 경우 노출 처리 할 수 없다.")
        @Test
        void displayWithPriceGreaterThanTotalPrice() {
            // given
            menu.setPrice(BigDecimal.valueOf(100));
            menuRepository.save(menu);

            // when & then
            assertThrows(IllegalStateException.class, () -> menuService.display(menu.getId()));
        }

        @DisplayName("메뉴가 존재하지 않을 경우 예외를 던진다.")
        @Test
        void displayWithNonExistentMenu() {
            // given
            UUID nonExistentMenuId = UUID.randomUUID();

            // when & then
            assertThrows(NoSuchElementException.class, () -> menuService.display(nonExistentMenuId));
        }
    }

    @Nested
    class Hide {

        @BeforeEach
        public void setUp() {
            menu.setDisplayed(true);
            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
        }

        @DisplayName("메뉴을 숨김 처리 할 수 있다.")
        @Test
        void hide() {
            // given
            menuRepository.save(menu);

            // when
            Menu hidden = menuService.hide(menu.getId());

            // then
            assertFalse(hidden.isDisplayed());
        }

        @DisplayName("메뉴가 존재하지 않을 경우 예외를 던진다.")
        @Test
        void hideWithNonExistentMenu() {
            // given
            UUID nonExistentMenuId = UUID.randomUUID();

            // when & then
            assertThrows(NoSuchElementException.class, () -> menuService.hide(nonExistentMenuId));
        }
    }
}
