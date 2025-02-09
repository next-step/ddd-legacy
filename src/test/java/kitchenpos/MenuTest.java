package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.TestConstant.*;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.productFixture.createProduct;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName(value = " Menu 테스트")
public class MenuTest {

    private static final String PRODUCT_NAME_비속어 = "fucking 치킨";
    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final BigDecimal 후라이드치킨_OVER_PRICE = new BigDecimal(21000);
    private static final String 후라이드치킨_MENU_NAME = "후라이드 치킨메뉴";

    private ProductRepository productRepository;
    private MenuService menuService;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void initialize() {
        productRepository = new InMemoryProductRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuRepository = new InMemoryMenuRepository();
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
            //given
            Product product = createProduct();
            productRepository.save(product);
            MenuGroup menuGroup = createMenuGroup();
            menuGroupRepository.save(menuGroup);
            Menu menuRequest = createMenu();

            //when
            Menu sut = menuService.create(menuRequest);

            //then
            assertAll(
                    () -> assertThat(sut.getId()).isNotNull(),
                    () -> assertThat(sut.getMenuGroup().getId()).isEqualTo(menuRequest.getMenuGroupId()),
                    () -> assertThat(sut.getName()).isEqualTo(menuRequest.getName()),
                    () -> assertThat(sut.getMenuProducts()).hasSize(menuRequest.getMenuProducts().size()),
                    () -> assertThat(sut.isDisplayed()).isEqualTo(menuRequest.isDisplayed()),
                    () -> assertThat(sut.getPrice()).isEqualTo(menuRequest.getPrice())
            );

        }

        @DisplayName(value = "메뉴의 가격은 0원 이상이어야 한다.")
        @Test
        void invalidMenuAmount() {
            //상품명이나 가격이 없을때 에러처리
            Menu menu = createMenu(후라이드치킨_MINUS_PRICE);
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName(value = "메뉴의 주문 상품이 1개 이상 존재해야 합니다")
        @Test
        void invalidMenuProduct() {
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            menuGroupRepository.save(menuGroup);
            Menu menu = createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, 후라이드치킨_MENU_GROUP_UUID);
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName(value = "메뉴 내부 상품의 수량은 0개 이상이어야 합니다.")
        @Test
        void menuProductNotEqualsProductSize() {
            Product product = createProduct();
            MenuGroup menuGroup = createMenuGroup();
            menuGroupRepository.save(menuGroup);
            var menuProducts = createMenuProduct(product, MINUS_QUANTITY, 후라이드치킨_PRODUCT_UUID);
            Menu menu = createMenu(menuGroup, menuProducts);
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName(value = "등록하려는 메뉴의 가격이 메뉴에 포함된 상품의 총 가격보다 높으면 안됩니다.")
        @Test
        void invalidTotalMenuAmount() {
            menuGroupRepository.save(createMenuGroup());
            Menu menu = createMenu(후라이드치킨_OVER_PRICE);

            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName(value = "메뉴의 이름이 없거나 비속어가 들어가 있으면 안됩니다.")
        @Test
        void invalidMenuName() {
            Product product = createProduct(MenuTest.후라이드치킨_PRODUCT_UUID, PRODUCT_NAME_비속어, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            menuGroupRepository.save(menuGroup);
            List<MenuProduct> menuProducts = List.of(createMenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID));
            Menu menu = createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_PROFANITY_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);

            assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
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

            assertThat(menu.getPrice()).isEqualTo(menuRequest.getPrice());
            assertThat(menuRepository.findById(menuRequest.getId()).get().getPrice()).isEqualTo(menuRequest.getPrice());

        }

        @DisplayName(value = "메뉴의 가격은 0원 이상이어야 한다.")
        @Test
        void invalidMenuAmount() {
           Menu menuRequest = createMenu(후라이드치킨_MINUS_PRICE);
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.changePrice(후라이드치킨_MENU_UUID, menuRequest));
        }

        @DisplayName(value = "메뉴의 변경 금액은 메뉴에 포함된 상품들의 가격 합보다 크면 안됩니다.")
        @Test
        void invalidTotalMenuAmount() {
            Menu menu = createMenu();
            menuRepository.save(menu);
            Menu menuRequest = createMenu(후라이드치킨_OVER_PRICE);
            assertThatIllegalArgumentException().isThrownBy(() -> menuService.changePrice(후라이드치킨_MENU_UUID, menuRequest));
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
            assertThatIllegalStateException().isThrownBy(() -> menuService.display(후라이드치킨_MENU_UUID));
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
            assertThat(ResponseMenu.size()).isEqualTo(1);

        }
    }
}
