package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    private static final UUID DEFAULT_MENU_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "메뉴";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.TEN;
    private static final UUID DEFAULT_MENU_GROUP_ID = UUID.randomUUID();

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

    private static List<MenuProduct> defaultMenuProducts() {
        MenuProduct defaultMenuProduct = new MenuProduct();
        Product product = defaultProduct();
        defaultMenuProduct.setProductId(product.getId());
        defaultMenuProduct.setQuantity(1);
        defaultMenuProduct.setProduct(product);

        return Arrays.asList(defaultMenuProduct);
    }
    static Menu defaultMenu() {
        return createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, DEFAULT_PRICE, DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
    }

    private static Menu createMenu(final UUID ID, final String name, final BigDecimal price, final UUID menuGroupId, final List<MenuProduct> menuProducts) {
        Menu menu = new Menu();

        menu.setId(ID);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);

        return menu;
    }

    private static MenuGroup defaultMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("테스트 메뉴");

        return menuGroup;
    }

    private static Product defaultProduct() {
        Product product = new Product();

        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.TEN);
        product.setName("테스트 상품");

        return product;
    }

    @DisplayName("메뉴를 생성 할 수 있다.")
    @Test
    void create_menu() {
        final Menu defaultMenu = defaultMenu();

        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenuGroup()));
        given(productRepository.findAllByIdIn(Mockito.any()))
                .willReturn(Arrays.asList(defaultProduct()));
        given(productRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultProduct()));
        given(purgomalumClient.containsProfanity(Mockito.any(String.class)))
                .willReturn(false);
        given(menuRepository.save(Mockito.any(Menu.class)))
                .willReturn(defaultMenu);

        final Menu result = menuService.create(defaultMenu);
        assertThat(result).isNotNull();
    }

    @DisplayName("가격이 필수이다")
    @Test
    void create_menu_with_null_and_empty_price() {
        final Menu menu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, null, DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격은 음수 일 수 없다")
    @ParameterizedTest
    @ValueSource(longs = -1)
    void create_menu_with_negative_number(final long price) {
        final Menu menu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, BigDecimal.valueOf(price), DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }


    @DisplayName("메뉴의 속한 상품의 가격보다 작으면 등록이 불가능하다")
    @Test
    void create_menu_with_lower_price() {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenuGroup()));

        final Menu menu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, BigDecimal.ONE, DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 이름은 필수이다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_menu_with_null_and_empty_name(final String name) {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenuGroup()));

        final Menu menu = createMenu(DEFAULT_MENU_ID, name, DEFAULT_PRICE, DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 속할 상품이 등록되어 있지 않은 상품이라면 메뉴 생성이 불가능하다")
    @Test
    void create_menu_with_not_exist_product() {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenuGroup()));
        given(productRepository.findAllByIdIn(Mockito.any()))
                .willReturn(Arrays.asList());

        final Menu menu = defaultMenu();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 속할 상품은 필수이다 ")
    @NullAndEmptySource
    @ParameterizedTest
    void create_menu_with_null_and_empty_menu_productszero_quantity_product(final List<MenuProduct> menuProducts) {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenuGroup()));

        final Menu menu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, DEFAULT_PRICE, DEFAULT_MENU_GROUP_ID, menuProducts);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 속할 상품은 필수이다 ")
    @ValueSource(longs = {-1, 0})
    @ParameterizedTest
    void create_menu_with_zero_quantity_menu_product(final long quantity) {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenuGroup()));
        List<MenuProduct> menuProducts = defaultMenuProducts();
        menuProducts.get(0).setQuantity(quantity);

        final Menu menu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, DEFAULT_PRICE, DEFAULT_MENU_GROUP_ID, menuProducts);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴는 하나의 메뉴 그룹에 속해야한다")
    @Test
    void create_menu_must_contain_one_menu() {
        given(menuGroupRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.empty());

        final Menu menu = defaultMenu();
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격을 수정할 수 있다")
    @Test
    void change_price() {
        final Menu menu = defaultMenu();
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        final BigDecimal updatePrice = BigDecimal.valueOf(9);
        final Menu changeMenu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, updatePrice, DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
        Menu result = menuService.changePrice(DEFAULT_MENU_ID, changeMenu);
        assertThat(result.getPrice()).isEqualTo(updatePrice);
    }


    @DisplayName("메뉴에 속한 상품의 총 가격의 합보다 변경하려는 가격이 크다면 수정이 불가능하다")
    @Test
    void change_price_bigger_than_contains_products() {
        final Menu menu = defaultMenu();
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        final BigDecimal updatePrice = BigDecimal.valueOf(15);
        final Menu changeMenu = createMenu(DEFAULT_MENU_ID, DEFAULT_NAME, updatePrice, DEFAULT_MENU_GROUP_ID, defaultMenuProducts());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(DEFAULT_MENU_ID, changeMenu));
    }

    @DisplayName("메뉴를 비공개할 수 있다")
    @Test
    void hide() {
        final Menu menu = defaultMenu();
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        final Menu result = menuService.hide(menu.getId());
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 공개 할 수 있다.")
    @Test
    void show() {
        final Menu menu = defaultMenu();
        menu.setDisplayed(false);
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        final Menu result = menuService.display(menu.getId());
        assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("생성된 메뉴를 조회할 수 있다")
    @Test
    void select_all_menus() {
        final List<Menu> defaultMenus = Arrays.asList(defaultMenu());
        given(menuRepository.findAll())
                .willReturn(defaultMenus);

        final List<Menu> results = menuService.findAll();
        assertThat(defaultMenus).isEqualTo(results);
    }
}