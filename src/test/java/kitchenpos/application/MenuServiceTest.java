package kitchenpos.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("MenuService 클래스의")
class MenuServiceTest {

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuService menuService;

    @MockBean
    private PurgomalumClient purgomalumClient;

    private Product product;
    private MenuGroup menuGroup;
    private Menu menu;
    private MenuProduct menuProduct;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("product");
        product.setPrice(BigDecimal.valueOf(10));

        menuGroup = new MenuGroup();
        menuGroup.setName("menuGroup");
        menuGroup.setId(UUID.randomUUID());

        menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        menuProduct.setProduct(product);

        menu = new Menu();
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName("menu");
        menu.setPrice(BigDecimal.valueOf(10));
        menu.setId(UUID.randomUUID());
        menu.setMenuProducts(Collections.singletonList(menuProduct));
    }


    @DisplayName("create 메서드는")
    @Nested
    class Create {

        @BeforeEach
        public void setUp() {

            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
            when(purgomalumClient.containsProfanity(menu.getName())).thenReturn(false);
        }

        @DisplayName("메뉴의 가격이 없거나 0보다 작을 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("provideInvalidPrices")
        void createWithInvalidPrice(BigDecimal price) {
            // given
            menu.setPrice(price);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴 그룹이 존재하지 않을 경우 예외를 던진다.")
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

        @DisplayName("메뉴에 속한 상품이 없거나 존재하지 않은 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("provideNonExistentMenuProducts")
        void createWithNonExistentProduct(List<MenuProduct> menuProducts) {
            // given
            menu.setMenuProducts(menuProducts);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴의 속한 상품의 수량이 0보다 작을 경우 예외를 던진다.")
        @Test
        void createWithNegativeQuantity() {
            // given
            menuProduct.setQuantity(-1);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴에 속한 제품들의 총 가격(가격 곱하기 수량의 합)보다 메뉴의 금액이 클 경우 예외를 던진다.")
        @Test
        void createWithPriceGreaterThanTotalPrice() {
            // given
            menu.setPrice(BigDecimal.valueOf(100));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴의 이름이 없거나 비속어 일 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("provideInvalidNames")
        void createWithInvalidName(String menuName) {
            // given
            menu.setName(menuName);
            when(purgomalumClient.containsProfanity(menu.getName())).thenReturn(true);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @DisplayName("메뉴를 생성할 수 있다.")
        @Test
        void create() {
            // when
            Menu created = menuService.create(menu);

            // then
            assertNotNull(created);
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

    @DisplayName("changePrice 메서드는")
    @Nested
    class ChangePrice {

        @BeforeEach
        public void setUp() {
            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
            menuRepository.save(menu);
        }

        @DisplayName("메뉴의 가격이 없거나 0보다 작을 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("provideInvalidPrices")
        void changePriceWithInvalidPrice(BigDecimal price) {
            // given
            menu.setPrice(price);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        @DisplayName("메뉴에 속한 제품들의 총 가격(가격 곱하기 수량의 합)보다 메뉴의 금액이 클 경우 예외를 던진다.")
        @Test
        void createWithPriceGreaterThanTotalPrice() {
            // given
            menu.setPrice(BigDecimal.valueOf(100));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        @DisplayName("매뉴의 가격이 변경된다.")
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

        private static Stream<BigDecimal> provideInvalidPrices() {
            return Stream.of(
                null,
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(-1));
        }
    }

    @DisplayName("display 메서드는")
    @Nested
    class Display {

        @BeforeEach
        public void setUp() {
            menu.setDisplayed(false);
            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
        }

        @DisplayName("메뉴가 존재하지 않을 경우 예외를 던진다.")
        @Test
        void displayWithNonExistentMenu() {
            // given
            UUID nonExistentMenuId = UUID.randomUUID();

            // when & then
            assertThrows(NoSuchElementException.class, () -> menuService.display(nonExistentMenuId));
        }

        @DisplayName("메뉴에 속한 제품들의 총 가격(가격 곱하기 수량의 합)보다 메뉴의 금액이 클 경우 예외를 던진다.")
        @Test
        void displayWithPriceGreaterThanTotalPrice() {
            // given
            menu.setPrice(BigDecimal.valueOf(100));
            menuRepository.save(menu);

            // when & then
            assertThrows(IllegalStateException.class, () -> menuService.display(menu.getId()));
        }

        @DisplayName("메뉴가 노출된다.")
        @Test
        void display() {
            // given
            menuRepository.save(menu);

            // when
            Menu displayed = menuService.display(menu.getId());

            // then
            assertTrue(displayed.isDisplayed());
        }
    }

    @DisplayName("hide 메서드는")
    @Nested
    class Hide {

        @BeforeEach
        public void setUp() {
            menu.setDisplayed(true);
            menuGroupRepository.save(menuGroup);
            productRepository.save(product);
        }

        @DisplayName("메뉴가 존재하지 않을 경우 예외를 던진다.")
        @Test
        void hideWithNonExistentMenu() {
            // given
            UUID nonExistentMenuId = UUID.randomUUID();

            // when & then
            assertThrows(NoSuchElementException.class, () -> menuService.hide(nonExistentMenuId));
        }

        @DisplayName("메뉴가 숨겨진다.")
        @Test
        void hide() {
            // given
            menuRepository.save(menu);

            // when
            Menu hidden = menuService.hide(menu.getId());

            // then
            assertFalse(hidden.isDisplayed());
        }
    }
}
