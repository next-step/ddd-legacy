package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName(value = " Menu 테스트")
public class MenuTest {

    private static final String TEST_PRODUCT_NAME = "TEST치킨";
    private static final String PRODUCT_NAME_비속어 = "fucking 치킨";

    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final BigDecimal 후라이드치킨_OVER_PRICE = new BigDecimal(21000);
    private static final String 후라이드치킨_MENU_NAME = "후라이드 치킨메뉴";
    private static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
    private static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
    private static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";


    private ProductRepository productRepository;
    private MenuService menuService;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void initialize() {
        productRepository = spy(new InMemoryProductRepository());
        menuGroupRepository = spy(new InMemoryMenuGroupRepository());
        menuRepository = spy(new InMemoryMenuRepository());
        purgomalumClient = new FakeDefaultPurgomalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName(value = "메뉴 등록 기능")
    @Nested
    class MenuCreateTest {
        private static final BigDecimal 후라이드치킨_MINUS_PRICE = new BigDecimal(-10);
        private static final String 후라이드치킨_PROFANITY_MENU_NAME = "fucking 치킨메뉴";
        private static final int MINUS_QUANTITY = -1;



        @DisplayName(value = "메뉴를 등록할 수 있다.")
        @Test
        void createMenuTest() {
            Mockito.clearInvocations(menuRepository, productRepository);
            Product product = createProduct();
            productRepository.save(product);
            MenuGroup menuGroup = createMenuGroup();
            menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID);
            Menu menuRequest = createMenu();
            Menu menu = menuService.create(menuRequest);
            //행위검증
            verify(menuRepository, times(1)).save(ArgumentCaptor.forClass(Menu.class).capture());
            verify(productRepository, times(1)).findAllByIdIn(Mockito.anyList());
            verify(productRepository, times(1)).findById(Mockito.any());
            verify(menuGroupRepository, times(1)).findById(Mockito.any());


            assertAll(
                    () -> assertThat(menu.getId()).isNotNull(),
                    () -> assertThat(menu.getMenuGroup().getId()).isEqualTo(menuRequest.getMenuGroupId()),
                    () -> assertThat(menu.getName()).isEqualTo(menuRequest.getName()),
                    () -> assertThat(menu.getMenuProducts()).hasSize(menuRequest.getMenuProducts().size()),
                    () -> assertThat(menu.isDisplayed()).isEqualTo(menuRequest.isDisplayed()),
                    () -> assertThat(menu.getPrice()).isEqualTo(menuRequest.getPrice())
            );

        }

        @DisplayName(value = "메뉴의 가격은 0원 이상이어야 한다.")
        @Test
        void invalidMenuAmount() {
            //상품명이나 가격이 없을때 에러처리
            Menu menu = MenuTest.createMenu(후라이드치킨_MINUS_PRICE);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 주문 상품이 1개 이상 존재해야 합니다")
        @Test
        void invalidMenuProduct() {
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            menuGroupRepository.save(menuGroup);
            Menu menu = MenuTest.createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴 내부 상품의 수량은 0개 이상이어야 합니다.")
        @Test
        void menuProductNotEqualsProductSize() {
            Product product = createProduct();
            MenuGroup menuGroup = createMenuGroup();
            menuGroupRepository.save(menuGroup);
            var menuProducts = createMenuProduct(product, MINUS_QUANTITY, 후라이드치킨_PRODUCT_UUID);
            Menu menu = MenuTest.createMenu(menuGroup, menuProducts);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "등록하려는 메뉴의 가격이 메뉴에 포함된 상품의 총 가격보다 높으면 안됩니다.")
        @Test
        void invalidTotalMenuAmount() {
            menuGroupRepository.save(createMenuGroup());
            Menu menu = MenuTest.createMenu(후라이드치킨_OVER_PRICE);

            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 이름이 없거나 비속어가 들어가 있으면 안됩니다.")
        @Test
        void invalidMenuName() {
            Product product = createProduct(MenuTest.후라이드치킨_PRODUCT_UUID, PRODUCT_NAME_비속어, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            menuGroupRepository.save(menuGroup);
            List<MenuProduct> menuProducts = List.of(createMenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID));
            Menu menu = MenuTest.createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_PROFANITY_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);

            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }
    }



    @DisplayName(value = "메뉴 가격 수정 기능")
    @Nested
    class MenuChangePriceTest {
        private static final BigDecimal 후라이드치킨_MINUS_PRICE = new BigDecimal(-10);
        private static final BigDecimal 후라이드치킨_CHANGE_PRICE = new BigDecimal(19000);

        @DisplayName(value = "메뉴가격을 수정할 수 있다.")
        @Test
        void changeMenu() {
            menuRepository.save( createMenu());
            Menu menuRequest = createMenu(후라이드치킨_CHANGE_PRICE);
            Menu menu = menuService.changePrice(후라이드치킨_MENU_UUID, menuRequest);
            //행위검증
            verify(menuRepository,times(1)).findById(Mockito.any());
            assertThat(menu.getPrice()).isEqualTo(menuRequest.getPrice());
        }

        @DisplayName(value = "메뉴의 가격은 0원 이상이어야 한다.")
        @Test
        void invalidMenuAmount() {
           Menu menuRequest = createMenu(후라이드치킨_MINUS_PRICE);

            ThrowingCallable throwingCallable = () -> menuService.changePrice(후라이드치킨_MENU_UUID, menuRequest);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 변경 금액은 메뉴에 포함된 상품들의 가격 합보다 크면 안됩니다.")
        @Test
        void invalidTotalMenuAmount() {
            Menu menu = createMenu();
            menuRepository.save(menu);
            Menu menuRequest = createMenu(후라이드치킨_OVER_PRICE);
            ThrowingCallable throwingCallable = () -> menuService.changePrice(후라이드치킨_MENU_UUID, menuRequest);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

    }


    @DisplayName(value = "메뉴 노출 기능")
    @Nested
    class MenuDisplayTest {
        @DisplayName(value = "메뉴내에 포함된 금액의 합이 메뉴 금액보다 크면 안됩니다.")
        @Test
        void invalidMenuAmount() {
            Menu menu = createMenu(후라이드치킨_OVER_PRICE);
            menuRepository.save(menu);
            ThrowingCallable throwingCallable = () -> menuService.display(후라이드치킨_MENU_UUID);
            assertThatIllegalStateException().isThrownBy(throwingCallable);
        }


        @DisplayName(value = "메뉴를 노출시킵니다.")
        @Test
        void displayMenu() {
            Menu menu = createMenu();
            menuRepository.save(menu);
            Menu ResponseMenu = menuService.display(후라이드치킨_MENU_UUID);
            assertThat(ResponseMenu.isDisplayed()).isTrue();
        }
    }

    @DisplayName(value = "메뉴 비노출 기능")
    @Nested
    class MenuHideTest {
        @DisplayName(value = "메뉴를 비노출시킵니다.")
        @Test
        void displayMenu() {
            Menu menu = createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE);
            menuRepository.save(menu);
            Menu ResponseMenu = menuService.hide(후라이드치킨_MENU_UUID);
            assertThat(ResponseMenu.isDisplayed()).isFalse();
        }
    }

    @DisplayName(value = "모든 메뉴 조회 기능")
    @Nested
    class findAllMenuTest {
        @DisplayName(value = "모든 메뉴를 조회합니다.")
        @Test
        void displayMenu() {
            Menu menu = createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE);
            menuRepository.save(menu);
            List<Menu> ResponseMenu = menuService.findAll();
            verify(menuRepository, times(1)).findAll();
            assertThat(ResponseMenu.size()).isEqualTo(1);

        }
    }

    private static Menu createMenu() {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(후라이드치킨_DEFAULT_PRICE);
        menu.setMenuGroup(createMenuGroup());
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(createMenuProduct()));
        menu.setMenuGroupId(후라이드치킨_MENU_GROUP_UUID);
        return menu;
    }

    private static Menu createMenu(BigDecimal price) {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(price);
        menu.setMenuGroup(createMenuGroup());
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(createMenuProduct()));
        menu.setMenuGroupId(후라이드치킨_MENU_GROUP_UUID);
        return menu;
    }

    private static Menu createMenu(final UUID id, final String name, final BigDecimal price, final MenuGroup menuGroup, final List<MenuProduct> menuProducts, final UUID menugroupId) {
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

    private static Menu createMenu(final UUID id, final String name, final BigDecimal price, final MenuGroup menuGroup, final UUID menugroupId) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuGroupId(menugroupId);
        return menu;
    }

    private static Menu createMenu(MenuGroup menuGroup, MenuProduct menuProducts) {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(후라이드치킨_DEFAULT_PRICE);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProducts));
        menu.setMenuGroupId(후라이드치킨_MENU_GROUP_UUID);
        return menu;
    }

    private Menu createMenu(UUID id, String name, BigDecimal price) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        return menu;
    }


    private static MenuProduct createMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(createProduct());
        menuProduct.setQuantity(1);
        menuProduct.setProductId(후라이드치킨_PRODUCT_UUID);
        return menuProduct;
    }

    private static MenuProduct createMenuProduct(final Product product, final int quantity, final UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }

    private static Product createProduct(final UUID uuid, final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static Product createProduct() {
        Product product = new Product();
        product.setId(후라이드치킨_PRODUCT_UUID);
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(후라이드치킨_DEFAULT_PRICE);
        return product;
    }



    private static MenuGroup createMenuGroup(final String name, final UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }

    private static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(한마리메뉴_MENU_GROUP_NAME);
        menuGroup.setId(후라이드치킨_MENU_GROUP_UUID);
        return menuGroup;
    }



}
