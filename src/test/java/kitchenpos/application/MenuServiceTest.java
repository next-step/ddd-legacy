package kitchenpos.application;

import kitchenpos.application.fakeobject.FakeMenuGroupRepository;
import kitchenpos.application.fakeobject.FakeMenuRepository;
import kitchenpos.application.fakeobject.FakeProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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

    @DisplayName("가격정보가 없거나 유효하지 않은 가격을 설정했을 경우 메뉴 추가 실패한다.")
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
    @MethodSource("kitchenpos.application.InputProvider#provideInvalidMenuGroupId")
    @ParameterizedTest
    public void create_menu_group_not_exist(UUID menuGroupId) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroupId(menuGroupId);

        //when & then
        assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품이 없는 경우 추가 실패한다.")
    @Test
    public void create_empty_menuProductList() {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroupId(fakeMenuGroupRepository.findAll().get(0).getId());
        menu.setMenuProducts(new ArrayList<>());

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품이 존재하지 않는 경우 추가 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideInvalidProductListWithInvalidQuantity")
    @ParameterizedTest
    public void create_non_exist_menuProductList(List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroupId(fakeMenuGroupRepository.findAll().get(0).getId());
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품 수량이 음수인 경우 추가 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidProductListWithInvalidQuantity")
    @ParameterizedTest
    public void create_invalid_quantity_menuProductList(List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroupId(fakeMenuGroupRepository.findAll().get(0).getId());
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴 상품 금액 총합이 메뉴의 금액보다 낮을 경우 추가 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidProductListWithValidQuantity")
    @ParameterizedTest
    public void create_high_price_menuProductList(List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(Long.MAX_VALUE));
        menu.setMenuGroupId(fakeMenuGroupRepository.findAll().get(0).getId());
        menu.setMenuProducts(menuProductList);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴가 존재할 경우 추가 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidProductListWithValidQuantity")
    @ParameterizedTest
    public void create_exist_name_menuProductList(List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroupId(fakeMenuGroupRepository.findAll().get(0).getId());
        menu.setMenuProducts(menuProductList);
        menu.setName("asdf");
        Mockito.when(purgomalumClient.containsProfanity(menu.getName())).thenReturn(true);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
    }

    @DisplayName("메뉴가 존재하지 않을 경우 추가 성공한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidProductListWithValidQuantity")
    @ParameterizedTest
    public void create_non_exist_name_menuProductList(List<MenuProduct> menuProductList) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setMenuGroupId(fakeMenuGroupRepository.findAll().get(0).getId());
        menu.setMenuProducts(menuProductList);
        menu.setName("asdf");
        Mockito.when(purgomalumClient.containsProfanity(menu.getName())).thenReturn(false);

        //when & then
        assertThat(menuService.create(menu)).isNotNull();
    }

    @DisplayName("메뉴가 존재하지 않을 경우 가격 변경에 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideInvalidMenuId")
    @ParameterizedTest
    public void changePrice_non_exist_menu(UUID menuId) {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setId(menuId);

        //when & then
        assertThrows(NoSuchElementException.class, () -> menuService.changePrice(menuId, menu));
    }

    @DisplayName("메뉴가 존재하고, 가격이 유효할 경우 가격 변경에 성공한다.")
    @Test
    public void changePrice_exist_menu_valid_price() {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.ONE);
        menu.setId(fakeMenuRepository.findAll().get(0).getId());
        fakeMenuRepository.setMenuProductsOnMenu(fakeProductRepository.findAll());

        //when & then
        assertThat(menuService.changePrice(menu.getId(), menu)).isNotNull();
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
}
