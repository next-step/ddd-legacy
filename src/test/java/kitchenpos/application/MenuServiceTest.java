package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MenuServiceTest extends MockTest {

    public static final int ZERO = 0;
    public static final String MENU_NAME = "후라이드";
    public static final String MENU_NAME2 = "양념";
    public static final long PRICE = 20000L;
    public static final long PRICE2 = 30000L;
    public static final long NEGATIVE_PRICE = -19000L;
    public static final long EXPENSIVE_PRICE = 20000000L;

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

    private MenuGroup menuGroup;
    private Product product1;
    private Product product2;
    private List<Product> products;
    private List<MenuProduct> menuProducts;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴그룹1");

        product1 = makeProduct("상품1", 10000L);
        product2 = makeProduct("상품2", 20000L);
        products = Arrays.asList(product1, product2);

        final MenuProduct menuProduct1 = makeMenuProduct(product1, 2L);
        final MenuProduct menuProduct2 = makeMenuProduct(product2, 3L);

        menuProducts = Arrays.asList(menuProduct1, menuProduct2);
    }

    private Product makeProduct(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(price));
        product.setName(name);
        return product;
    }

    private MenuProduct makeMenuProduct(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    @DisplayName("create - 메뉴를 추가할 수 있다")
    @Test
    void createOK() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1), Optional.of(product2));
        given(menuRepository.save(any())).willReturn(menu);

        //when
        final Menu sut = menuService.create(menu);

        //then
        assertThat(sut).isInstanceOf(Menu.class);
    }

    @DisplayName("create - 메뉴 그룹이 존재하지 않으면 예외가 발생한다")
    @Test
    void menuGroupNotExist() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        given(menuGroupRepository.findById(any())).willThrow(NoSuchElementException.class);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 가격이 없으면 예외가 발생한다")
    @Test
    void noPrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);
        menu.setPrice(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 가격이 음수이라면 예외가 발생한다")
    @Test
    void negativePrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, NEGATIVE_PRICE);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴상품을 요청하지 않으면 예외가 발생한다")
    @Test
    void noMenuProduct() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        menuProducts = Collections.emptyList();
        menu.setMenuProducts(menuProducts);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 상품의 상품들 중 하나라도 존재하지 않으면 예외가 발생한다")
    @Test
    void productNotExist() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1))
            .willThrow(NoSuchElementException.class);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 상품의 상품수량이 하나라도 음수인 것이 있으면 예외가 발생한다")
    @Test
    void productsNegativeQuentity() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        menu.getMenuProducts()
            .get(1)
            .setQuantity(-1);

        menu.setMenuProducts(menuProducts);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1), Optional.of(product2));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void menuValidPrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, EXPENSIVE_PRICE);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1), Optional.of(product2));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 이름이 한글자 미만이라면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullAndEmpty(final String value) {
        //given
        final Menu menu = createMenu(value, PRICE);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1), Optional.of(product2));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("create - 메뉴 이름은 중복될 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"name1", "name2"})
    void duplicateName(final String name) {
        //given
        final Menu menu1 = createMenu(name, PRICE);
        final Menu menu2 = createMenu(name, PRICE2);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1), Optional.of(product2));
        given(menuRepository.save(any())).willReturn(menu1, menu2);

        //when
        final Menu createdMenu1 = menuService.create(menu1);
        final Menu createdMenu2 = menuService.create(menu2);

        //then
        assertThat(createdMenu1).isInstanceOf(Menu.class);
        assertThat(createdMenu2).isInstanceOf(Menu.class);
    }

    @DisplayName("create - 메뉴 이름에 비속어가 포함되어 있다면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "bitch", "Damn"})
    void profanityName(final String profanityName) {
        //given
        final Menu menu = createMenu(profanityName, PRICE);

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
        given(productRepository.findAllById(any())).willReturn(products);
        given(productRepository.findById(any())).willReturn(Optional.of(product1), Optional.of(product2));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("changePrice - 메뉴의 가격을 수정할 수 있다")
    @ParameterizedTest()
    @ValueSource(longs = {0L, 1L, 10000L, 20000L})
    void change(final long price) {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when
        menu.setPrice(BigDecimal.valueOf(price));
        final Menu changedMenu = menuService.changePrice(menu.getId(), menu);

        //then
        assertThat(changedMenu).isInstanceOf(Menu.class);
        assertThat(changedMenu.getPrice()).isEqualTo(BigDecimal.valueOf(price));
    }

    @DisplayName("changePrice - 메뉴 가격이 없으면 예외가 발생한다")
    @Test
    void changeWithNoPrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);
        menu.setPrice(null);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("changePrice - 메뉴 가격이 음수라면 예외가 발생한다")
    @Test
    void changeWithNegativePrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, NEGATIVE_PRICE);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("changePrice - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void changeValidPrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, EXPENSIVE_PRICE);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("display - 메뉴가 노출되도록 변경할 수 있다")
    @Test
    void display() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);
        menu.setDisplayed(false);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when
        final Menu sut = menuService.display(menu.getId());

        //then
        assertThat(sut.isDisplayed()).isTrue();
    }

    @DisplayName("display - 메뉴가 존재하지 않으면 예외가 발생한다")
    @Test
    void displayNotExistMenu() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);
        menu.setDisplayed(false);

        given(menuRepository.findById(any())).willThrow(NoSuchElementException.class);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @DisplayName("display - 메뉴 가격이 메뉴에 포함된 상품가격과 갯수를 곱해 모두 더한 가격보다 비싸다면 예외가 발생한다")
    @Test
    void displayMenuPrice() {
        //given
        final Menu menu = createMenu(MENU_NAME, EXPENSIVE_PRICE);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @DisplayName("hide - 메뉴가 노출되지 않도록 변경할 수 있다")
    @Test
    void hide() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when
        final Menu sut = menuService.hide(menu.getId());

        //then
        assertThat(sut.isDisplayed()).isFalse();
    }

    @DisplayName("hide - 메뉴가 존재하지 않으면 예외가 발생한다")
    @Test
    void hideNotExistMenu() {
        //given
        final Menu menu = createMenu(MENU_NAME, PRICE);

        given(menuRepository.findById(any())).willThrow(NoSuchElementException.class);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> menuService.hide(menu.getId()));
    }

    @DisplayName("findAll - 메뉴리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        final Menu menu1 = createMenu(MENU_NAME, PRICE);
        final Menu menu2 = createMenu(MENU_NAME2, PRICE2);

        given(menuRepository.findAll()).willReturn(Arrays.asList(menu1, menu2));

        //when
        final List<Menu> menus = menuService.findAll();

        //then
        assertAll(
            () -> assertThat(menus.get(ZERO)).isEqualTo(menu1),
            () -> assertThat(menus.get(1)).isEqualTo(menu2)
        );
    }

    private Menu createMenu(final String name, final Long price) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

}
