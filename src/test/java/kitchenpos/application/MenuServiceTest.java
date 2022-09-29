package kitchenpos.application;

import kitchenpos.application.fakeobject.FakeMenuGroupRepository;
import kitchenpos.application.fakeobject.FakeMenuRepository;
import kitchenpos.application.fakeobject.FakeProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    private MenuService menuService;

    private FakeMenuRepository fakeMenuRepository;

    private FakeMenuGroupRepository fakeMenuGroupRepository;

    private FakeProductRepository fakeProductRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        this.fakeMenuRepository = new FakeMenuRepository();
        this.fakeMenuGroupRepository = new FakeMenuGroupRepository();
        this.fakeProductRepository = new FakeProductRepository();
        this.menuService = new MenuService(fakeMenuRepository, fakeMenuGroupRepository, fakeProductRepository, purgomalumClient);
    }

    @DisplayName("가격정보가 없는 경우 메뉴 추가 실패한다.")
    @MethodSource("provideNullOrMinusBigDecimal")
    @ParameterizedTest
    public void create_menu_group_not_exist(BigDecimal price) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 그룹이 없는 경우 메뉴 추가 실패한다.")
    @MethodSource("provideValidPriceAndNonExistMenuGroupId")
    @ParameterizedTest
    public void create_menu_group_not_exist(BigDecimal price, UUID menuGroupId) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);

        //when & then
        assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품이 없는 경우 추가 실패한다.")
    @MethodSource("provideValidPriceAndExistMenuGroupIdAndEmptyMenuProductList")
    @ParameterizedTest
    public void create_empty_menuProductList(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품이 존재하지 않는 경우 추가 실패한다.")
    @MethodSource("provideValidPriceAndExistMenuGroupIdAndNonExistProductList")
    @ParameterizedTest
    public void create_non_exist_menuProductList(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품 수량이 음수인 경우 추가 실패한다.")
    @MethodSource("provideValidPriceAndExistMenuGroupIdAndInvalidQuantityMenuProductList")
    @ParameterizedTest
    public void create_invalid_quantity_menuProductList(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품 금액 총합이 메뉴의 금액보다 낮을 경우 추가 실패한다.")
    @MethodSource("provideValidPriceAndExistMenuGroupIdAndValidQuantityMenuProductListAndHighPrice")
    @ParameterizedTest
    public void create_high_price_menuProductList(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴가 존재할 경우 추가 실패한다.")
    @MethodSource("provideValidPriceAndExistMenuGroupIdAndValidQuantityMenuProductListAndValidPrice")
    @ParameterizedTest
    public void create_exist_name_menuProductList(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProductList);
        menu.setName("asdf");
        Mockito.when(purgomalumClient.containsProfanity(menu.getName())).thenReturn(true);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴가 존재하지 않을 경우 추가 성공한다.")
    @MethodSource("provideValidPriceAndExistMenuGroupIdAndValidQuantityMenuProductListAndValidPrice")
    @ParameterizedTest
    public void create_non_exist_name_menuProductList(BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProductList);
        menu.setName("asdf");
        Mockito.when(purgomalumClient.containsProfanity(menu.getName())).thenReturn(false);

        //when & then
        assertThat(menuService.create(menu)).isNotNull();
    }

    @DisplayName("메뉴가 존재하지 않을 경우 가격 변경에 실패한다.")
    @MethodSource("provideInvalidMenuIdAndValidPrice")
    @ParameterizedTest
    public void changePrice_non_exist_menu(BigDecimal price, UUID menuId) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setId(menuId);

        //when & then
        assertThrows(NoSuchElementException.class, () -> menuService.changePrice(menuId, menu));
    }

    @DisplayName("메뉴가 존재하고, 가격이 유효할 경우 가격 변경에 성공한다.")
    @MethodSource("provideValidMenuIdAndValidPrice")
    @ParameterizedTest
    public void changePrice_exist_menu_valid_price(BigDecimal price, UUID menuId) {
        //given
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setId(menuId);
        fakeMenuRepository.setMenuProductsOnMenu(fakeProductRepository.findAll());

        //when & then
        assertThat(menuService.changePrice(menuId, menu)).isNotNull();
    }

    @DisplayName("메뉴가 존재하지 않으면 전시 불가.")
    @MethodSource("kitchenpos.application.InputProvider#provideInvalidMenuId")
    @ParameterizedTest
    public void display_non_exist_menu(UUID menuId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> menuService.display(menuId));
    }

    @DisplayName("메뉴가 존재하고 가격이 유효하면 전시 성공.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidMenuId")
    @ParameterizedTest
    public void display_exist_menu(UUID menuId) {
        //given
        fakeMenuRepository.setMenuProductsOnMenu(fakeProductRepository.findAll());
        Menu menu = menuService.display(menuId);

        //when & then
        assertThat(menu)
                .returns(true, from(Menu::isDisplayed));
    }

    @DisplayName("메뉴가 존재하지 않으면 숨기기 불가.")
    @MethodSource("kitchenpos.application.InputProvider#provideInvalidMenuId")
    @ParameterizedTest
    public void hide_non_exist_menu(UUID menuId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> menuService.hide(menuId));
    }

    @DisplayName("메뉴가 존재하면 숨기기 성공.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidMenuId")
    @ParameterizedTest
    public void hide_exist_menu(UUID menuId) {
        //given
        Menu menu = menuService.hide(menuId);

        //when & then
        assertThat(menu)
                .returns(false, from(Menu::isDisplayed));
    }

    public static Stream<BigDecimal> provideNullOrMinusBigDecimal() {
        return Stream.of(null, BigDecimal.valueOf(-1));
    }

    public static Stream<Arguments> provideValidPriceAndNonExistMenuGroupId() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        return validPriceStream.map(
                v -> Arguments.of(v, new UUID(1, 2))
        );
    }

    public static Stream<Arguments> provideValidPriceAndExistMenuGroupIdAndEmptyMenuProductList() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        Stream<UUID> validMenuGroupIdStream = InputProvider.provideValidMenuGroupId();
        return validPriceStream.flatMap(
                price -> validMenuGroupIdStream.map(menuGroupId -> Arguments.of(price, menuGroupId, new ArrayList<>()))
        );
    }

    public static Stream<Arguments> provideValidPriceAndExistMenuGroupIdAndNonExistProductList() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        Stream<UUID> validMenuGroupIdStream = InputProvider.provideValidMenuGroupId();
        Stream<List<MenuProduct>> notExistMenuProductStream = InputProvider.provideInvalidProductListWithInvalidQuantity();

        return validPriceStream.flatMap(
                price -> validMenuGroupIdStream.flatMap(
                        menuGroupId -> notExistMenuProductStream.map(
                            nonExistMenuProduct -> Arguments.of(price, menuGroupId, nonExistMenuProduct)
                        )
                )
        );
    }

    public static Stream<Arguments> provideValidPriceAndExistMenuGroupIdAndInvalidQuantityMenuProductList() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        Stream<UUID> validMenuGroupIdStream = InputProvider.provideValidMenuGroupId();
        Stream<List<MenuProduct>> existMenuProductStream = InputProvider.provideValidProductListWithInvalidQuantity();

        return validPriceStream.flatMap(
                price -> validMenuGroupIdStream.flatMap(
                        menuGroupId -> existMenuProductStream.map(
                                existMenuProduct -> Arguments.of(price, menuGroupId, existMenuProduct)
                        )
                )
        );
    }

    public static Stream<Arguments> provideValidPriceAndExistMenuGroupIdAndValidQuantityMenuProductListAndHighPrice() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideHighPrice();
        Stream<UUID> validMenuGroupIdStream = InputProvider.provideValidMenuGroupId();
        Stream<List<MenuProduct>> existMenuProductStream = InputProvider.provideValidProductListWithValidQuantity();

        return validPriceStream.flatMap(
                price -> validMenuGroupIdStream.flatMap(
                        menuGroupId -> existMenuProductStream.map(
                                existMenuProduct -> Arguments.of(price, menuGroupId, existMenuProduct)
                        )
                )
        );
    }

    public static Stream<Arguments> provideValidPriceAndExistMenuGroupIdAndValidQuantityMenuProductListAndValidPrice() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        Stream<UUID> validMenuGroupIdStream = InputProvider.provideValidMenuGroupId();
        Stream<List<MenuProduct>> existMenuProductStream = InputProvider.provideValidProductListWithValidQuantity();

        return validPriceStream.flatMap(
                price -> validMenuGroupIdStream.flatMap(
                        menuGroupId -> existMenuProductStream.map(
                                existMenuProduct -> Arguments.of(price, menuGroupId, existMenuProduct)
                        )
                )
        );
    }

    public static Stream<Arguments> provideValidMenuIdAndInvalidPrice() {
        Stream<BigDecimal> invalidPriceStream = InputProvider.provideInvalidPrice();
        Stream<UUID> validMenuId = InputProvider.provideValidMenuId();
        return invalidPriceStream.flatMap(
                invalidPrice -> validMenuId.map(menuId -> Arguments.of(invalidPrice, menuId))
        );
    }

    public static Stream<Arguments> provideInvalidMenuIdAndValidPrice() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        Stream<UUID> invalidMenuId = InputProvider.provideInvalidMenuId();
        return validPriceStream.flatMap(
                validPrice -> invalidMenuId.map(menuId -> Arguments.of(validPrice, menuId))
        );
    }

    public static Stream<Arguments> provideValidMenuIdAndValidPrice() {
        Stream<BigDecimal> validPriceStream = InputProvider.provideValidPrice();
        Stream<UUID> validMenuId = InputProvider.provideValidMenuId();
        return validPriceStream.flatMap(
                validPrice -> validMenuId.map(menuId -> Arguments.of(validPrice, menuId))
        );
    }
}
