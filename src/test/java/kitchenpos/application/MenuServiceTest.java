package kitchenpos.application;

import kitchenpos.application.fake.InMemoryMenuGroupRepository;
import kitchenpos.application.fake.InMemoryMenuRepository;
import kitchenpos.application.fake.InMemoryProductRepository;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuServiceTest {

    private static final UUID BURGER_PRODUCT_ID = UUID.randomUUID();
    private static final UUID SIDE_PRODUCT_ID = UUID.randomUUID();
    private static final UUID COKE_PRODUCT_ID = UUID.randomUUID();
    private static final UUID MENU_GROUP_ID = UUID.randomUUID();

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        this.menuRepository = new InMemoryMenuRepository();
        this.menuGroupRepository = new InMemoryMenuGroupRepository();
        this.productRepository = new InMemoryProductRepository();
        this.purgomalumClient = new FakePurgomalumClient(List.of("비속어", "욕설"));
        this.menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

        productRepository.save(ProductFixture.createProduct(BURGER_PRODUCT_ID, "치킨버거", new BigDecimal(7000)));
        productRepository.save(ProductFixture.createProduct(SIDE_PRODUCT_ID, "감자튀김", new BigDecimal(2000)));
        productRepository.save(ProductFixture.createProduct(COKE_PRODUCT_ID, "콜라", new BigDecimal(2000)));

        menuGroupRepository.save(MenuGroupFixture.createMenuGroup(MENU_GROUP_ID, "세트메뉴"));
    }

    @DisplayName("메뉴를 생성할 수 있다")
    @Nested
    class MenuCreator {

        @DisplayName("메뉴는 1가지 이상의 상품으로 등록 가능하다")
        @Test
        void createMenuByProducts() {
            //given
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            MenuProduct side = MenuProductFixture.createMenuProduct(SIDE_PRODUCT_ID, 1);
            MenuProduct coke = MenuProductFixture.createMenuProduct(COKE_PRODUCT_ID, 1);

            Menu menu = MenuFixture.createMenu(
                    MENU_GROUP_ID,
                    "치킨버거세트",
                    new BigDecimal(10000),
                    List.of(chickenBurger, side, coke)
            );
            //when
            Menu resultMenu = menuService.create(menu);
            //then
            assertAll(
                    () -> assertThat(resultMenu.getName()).isEqualTo("치킨버거세트"),
                    () -> assertThat(resultMenu.getPrice()).isEqualTo(new BigDecimal(10000)),
                    () -> assertThat(resultMenu.getMenuProducts()).hasSize(3),
                    () -> assertThat(resultMenu.getMenuGroup()).isNotNull(),
                    () -> assertThat(resultMenu.getMenuGroup().getId()).isEqualTo(MENU_GROUP_ID)
            );
        }

        @DisplayName("각 메뉴를 구성하는 상품의 수량은 0이상이어야 한다")
        @Test
        void validateMenuProductQuantity() {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            MenuProduct coke = MenuProductFixture.createMenuProduct(COKE_PRODUCT_ID, -1);
            Menu menu = MenuFixture.createMenu(
                    MENU_GROUP_ID,
                    "치킨버거+음료",
                    new BigDecimal(9000),
                    List.of(chickenBurger, coke)
            );

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName("메뉴명은 반드시 입력되어야 한다")
        @ParameterizedTest
        @NullSource
        void notNullName(String nullName) {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = MenuFixture.createMenu(MENU_GROUP_ID, nullName, new BigDecimal(7000), List.of(chickenBurger));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName("메뉴명에 비속어가 있으면 등록할 수 없다")
        @ValueSource(strings = {"비속어", "욕설이 포함된 메뉴명"})
        @ParameterizedTest
        void validateMenuName(String name) {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = MenuFixture.createMenu(MENU_GROUP_ID, name, new BigDecimal(7000), List.of(chickenBurger));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName("가격은 반드시 입력되어야 한다")
        @ParameterizedTest
        @NullSource
        void notNullPrice(BigDecimal nullPrice) {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = MenuFixture.createMenu(MENU_GROUP_ID, "치킨버거", nullPrice, List.of(chickenBurger));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName("메뉴의 가격은 0원 이상이어야 한다")
        @Test
        void nonNegativeMenuPrice() {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = MenuFixture.createMenu(MENU_GROUP_ID, "치킨버거", new BigDecimal(-1), List.of(chickenBurger));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(menu));
        }

        @DisplayName("메뉴의 가격이 포함된 상품 가격 총합과 동일하거나 할인된 가격일 경우만 등록 가능하다")
        @Test
        void validateMenuPrice() {
            //given
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);    //7000원
            MenuProduct side = MenuProductFixture.createMenuProduct(SIDE_PRODUCT_ID, 1);   //2000원
            MenuProduct coke = MenuProductFixture.createMenuProduct(COKE_PRODUCT_ID, 1);   //2000원

            Menu menu = MenuFixture.createMenu(
                    MENU_GROUP_ID,
                    "치킨버거세트",
                    new BigDecimal(11001),
                    List.of(chickenBurger, side, coke)
            );
            //when, then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(menu));
        }
    }

    @DisplayName("메뉴는 가격을 수정할 수 있다")
    @Nested
    class MenuPriceUpdater {

        @DisplayName("메뉴는 가격을 수정할 수 있다")
        @Test
        void changeMenuPrice() {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = menuService.create(
                    MenuFixture.createMenu(
                            MENU_GROUP_ID,
                            "치킨버거",
                            new BigDecimal(7000),
                            List.of(chickenBurger)));

            ReflectionTestUtils.setField(menu, "price", new BigDecimal(6500));
            Menu newPriceMenu = menuService.changePrice(menu.getId(), menu);

            assertThat(newPriceMenu.getPrice()).isEqualTo(new BigDecimal(6500));
        }

        @DisplayName("메뉴의 가격이 0원 미만이면 변경할 수 없다")
        @Test
        void canNotChangeNegativePrice() {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = menuService.create(
                    MenuFixture.createMenu(
                            MENU_GROUP_ID,
                            "치킨버거",
                            new BigDecimal(7000),
                            List.of(chickenBurger)));

            ReflectionTestUtils.setField(menu, "price", new BigDecimal(-1));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
        }

        @DisplayName("메뉴의 가격은 포함된 상품 가격 총합과 동일하거나 할인된 가격으로만 수정 가능하다")
        @Test
        void validateMenuPriceOnModify() {
            //given
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);    //7000원
            MenuProduct side = MenuProductFixture.createMenuProduct(SIDE_PRODUCT_ID, 1);   //2000원
            MenuProduct coke = MenuProductFixture.createMenuProduct(COKE_PRODUCT_ID, 1);   //2000원

            Menu menu = MenuFixture.createMenu(
                    MENU_GROUP_ID,
                    "치킨버거세트",
                    new BigDecimal(11000),
                    List.of(chickenBurger, side, coke)
            );
            Menu resultMenu = menuService.create(menu);

            ReflectionTestUtils.setField(resultMenu, "price", new BigDecimal(11001));
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.changePrice(resultMenu.getId(), resultMenu));
        }

    }

    @DisplayName("메뉴를 전시할 수 있다")
    @Nested
    class MenuDisplay {

        @DisplayName("메뉴를 메뉴판에 전시한다")
        @Test
        void display() {
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
            Menu menu = menuService.create(
                    MenuFixture.createMenu(
                            MENU_GROUP_ID,
                            "치킨버거",
                            new BigDecimal(7000),
                            List.of(chickenBurger)
                    )
            );

            Menu resultMenu = menuService.display(menu.getId());

            assertThat(resultMenu.isDisplayed()).isTrue();
        }

        @DisplayName("메뉴의 가격이 포함된 상품 가격 총합과 동일하거나 할인된 가격인 경우만 전시할 수 있다")
        @Test
        void validateMenuPriceOnDisplay() {
            //given
            MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);    //7000원
            MenuProduct side = MenuProductFixture.createMenuProduct(SIDE_PRODUCT_ID, 1);   //2000원
            MenuProduct coke = MenuProductFixture.createMenuProduct(COKE_PRODUCT_ID, 1);   //2000원

            Menu menu = MenuFixture.createMenu(
                    MENU_GROUP_ID,
                    "치킨버거세트",
                    new BigDecimal(11000),
                    List.of(chickenBurger, side, coke)
            );
            Menu resultMenu = menuService.create(menu);

            ReflectionTestUtils.setField(resultMenu, "price", new BigDecimal(11001));
            assertThatIllegalStateException()
                    .isThrownBy(() -> menuService.display(resultMenu.getId()));
        }
    }

    @DisplayName("메뉴판의 전시여부를 X로 변경한다")
    @Test
    void hide() {
        MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);
        Menu menu = menuService.create(
                MenuFixture.createMenu(
                        MENU_GROUP_ID,
                        "치킨버거",
                        new BigDecimal(7000),
                        List.of(chickenBurger)
                )
        );

        Menu resultMenu = menuService.hide(menu.getId());

        assertThat(resultMenu.isDisplayed()).isFalse();
    }

    //region [메뉴 조회]
    @DisplayName("모든 메뉴들을 조회할 수 있다")
    @Test
    void findAll() {
        MenuProduct chickenBurger = MenuProductFixture.createMenuProduct(BURGER_PRODUCT_ID, 1);    //7000원
        MenuProduct side = MenuProductFixture.createMenuProduct(SIDE_PRODUCT_ID, 1);   //2000원
        MenuProduct coke = MenuProductFixture.createMenuProduct(COKE_PRODUCT_ID, 1);   //2000원

        menuService.create(MenuFixture.createMenu(MENU_GROUP_ID, "치킨버거", new BigDecimal(7000), List.of(chickenBurger)));
        menuService.create(MenuFixture.createMenu(MENU_GROUP_ID, "감자튀김", new BigDecimal(2000), List.of(side)));
        menuService.create(MenuFixture.createMenu(MENU_GROUP_ID, "콜라", new BigDecimal(2000), List.of(coke)));

        List<Menu> allMenus = menuService.findAll();

        assertAll(
                () -> assertThat(allMenus.size()).isEqualTo(3),
                () -> assertThat(allMenus)
                        .extracting(Menu::getName, menu -> menu.getPrice().intValue())
                        .contains(
                                Tuple.tuple("치킨버거", 7000),
                                Tuple.tuple("감자튀김", 2000),
                                Tuple.tuple("콜라", 2000)
                        )
        );
    }
    //endregion
}
