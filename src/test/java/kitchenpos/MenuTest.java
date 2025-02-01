package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName(value = " Menu 테스트")
public class MenuTest {

    private static final BigDecimal BIG_DECIMAL_MINUS_ONE = BigDecimal.valueOf(-1);
    private static final String TEST_PRODUCT_NAME = "TEST치킨";
    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");

    @Autowired
    private ProductService productService;
    @SpyBean
    private ProductRepository productRepository;
    @Autowired
    private PurgomalumClient mockPurgomalumClient;
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;


    @DisplayName(value = "메뉴 등록 기능")
    @Nested
    class MenuCreateTest {
        private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
        private static final BigDecimal 후라이드치킨_MINUS_PRICE = new BigDecimal(-10);

        private static final String 후라이드치킨_MENU_NAME = "후라이드 치킨메뉴";
        public static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
        public static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
        private static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";
        public static final int MINUS_QUANTITY = -1;
        private static final BigDecimal 후라이드치킨_OVER_PRICE = new BigDecimal(21000);
        private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");

        @BeforeEach
        void initialize() {
            Product product = Product(후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            productRepository.save(product);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME,후라이드치킨_MENU_GROUP_UUID);
            menuGroupRepository.save(menuGroup);
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);
            menuRepository.save(menu);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @AfterEach
        void cleanup() {

        }

        @DisplayName(value = "메뉴의 가격은 0원 이상이어야 한다.")
        @Test
        void invalidMenuAmount() {
            Product product = Product(MenuTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME,후라이드치킨_MENU_UUID);
            //상품명이나 가격이 없을때 에러처리
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_MINUS_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 주문 상품이 1개 이상 존재해야 합니다")
        @Test
        void invalidMenuProduct() {
            Product product = Product(MenuTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME,후라이드치킨_MENU_GROUP_UUID);
            //상품명이나 가격이 없을때 에러처리
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_MINUS_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴 내부 상품의 수량은 0개 이상이어야 합니다.")
        @Test
        void menuProductNotEqualsProductSize() {
            Product product = Product(MenuTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME,후라이드치킨_MENU_GROUP_UUID);
            //상품명이나 가격이 없을때 에러처리
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, MINUS_QUANTITY));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "등록하려는 메뉴의 가격이 메뉴에 포함된 상품의 총 가격보다 높으면 안됩니다.")
        @Test
        void invalidTotalMenuAmount() {
            Product product = Product(MenuTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME,후라이드치킨_MENU_GROUP_UUID);
            //상품명이나 가격이 없을때 에러처리
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_OVER_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);

            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

    }

    private MenuProduct MenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }


    private static Product Product(String name, int price) {
        return Product(null, name, new BigDecimal(price));
    }

    private static Product Product(UUID uuid, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static MenuGroup MenuGroup(String name, UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }

    private static Menu Menu(UUID id, String name, BigDecimal price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(null);
        return menu;
    }

    private static Menu Menu(UUID id, String name, BigDecimal price, MenuGroup menuGroup, List<MenuProduct> menuProducts, UUID menugroupId) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menugroupId);
        return menu;
    }
}
