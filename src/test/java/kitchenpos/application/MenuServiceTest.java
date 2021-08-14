package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class MenuServiceTest {
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private MenuService menuService;

    private long productQuantity;
    private long productPrice;
    private String menuName;
    private BigDecimal menuPrice;
    private UUID menuGroupId;
    private List<MenuProduct> menuProducts;
    private BigDecimal changeMenuPrice;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new TestMenuGroupRepository();
        productRepository = new TestProductRepository();
        menuService = new MenuService(
                new TestMenuRepository(),
                menuGroupRepository,
                productRepository,
                new TestPurgomalumClient()
        );

        productQuantity = 2L;
        productPrice = 500L;
        menuName = "메뉴1";
        menuPrice = BigDecimal.valueOf(1000L);
        menuGroupId = createMenuGroup().getId();
        menuProducts = createMenuProducts();
        changeMenuPrice = BigDecimal.valueOf(500L);
    }

    @Test
    @DisplayName("메뉴를 생성한다.")
    void create() {
        Menu menu = saveMenu();

        assertThat(menu.getId()).isNotNull();
        assertThat(menu.getName()).isEqualTo(menuName);
        assertThat(menu.getPrice()).isEqualTo(menuPrice);
    }

    @NullSource
    @ValueSource(strings = "-1")
    @ParameterizedTest
    @DisplayName("메뉴를 생성시 가격은 0 이상이어야 한다.")
    void create_valid_price(BigDecimal price) {
        Menu request = createRequest(menuName, price, menuGroupId, menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성시 메뉴 그룹은 기등록된 그룹이어야 한다")
    void create_valid_exist_menuGroup() {
        Menu request = createRequest(menuName, menuPrice, UUID.randomUUID(), menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @NullSource
    @EmptySource
    @ParameterizedTest
    @DisplayName("메뉴를 생성시 메뉴 상품은 필수이다.")
    void create_valid_notEmpty_menuProduct(List<MenuProduct> menuProducts) {
        Menu request = createRequest(menuName, menuPrice, menuGroupId, menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성시 상품은 기등록된 상품이어야 한다")
    void create_valid_exist_products() {
        List<MenuProduct> menuProducts = createMenuProducts(Collections.singletonList(createProduct()));
        Menu request = createRequest(menuName, menuPrice, menuGroupId, menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성시 각 상품 수량은 0 이상이어야 한다.")
    void create_valid_product_quantity() {
        List<MenuProduct> menuProducts = createMenuProducts(Collections.singletonList(createProduct()), -1L);
        Menu request = createRequest(menuName, menuPrice, menuGroupId, menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성시 메뉴 가격은 메뉴에 포함된 각 상품 수량의 합보다 클 수 없다.")
    void create_valid_price() {
        BigDecimal price = BigDecimal.valueOf(productQuantity * productPrice + 1);
        Menu request = createRequest(menuName, price, menuGroupId, menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @NullSource
    @ValueSource(strings = {"하드코딩", "레거시"})
    @ParameterizedTest
    @DisplayName("메뉴를 생성시 메뉴명은 필수이며 비속어가 포함되면 안된다.")
    void create_valid_name(String name) {
        Menu request = createRequest(name, menuPrice, menuGroupId, menuProducts);

        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴가격을 변경한다.")
    void changePrice() {
        UUID menuId = saveMenu().getId();
        BigDecimal price = BigDecimal.valueOf(500L);
        Menu request = createChangeMenuRequest(price);

        Menu menu = menuService.changePrice(menuId, request);

        assertThat(menu.getId()).isEqualTo(menuId);
        assertThat(menu.getPrice()).isEqualTo(price);
    }

    @NullSource
    @ValueSource(strings = {"-1"})
    @ParameterizedTest
    @DisplayName("메뉴가격 변경시 가격은 0 이상이어야 한다.")
    void changePrice_valid_price(BigDecimal price) {
        UUID menuId = saveMenu().getId();
        Menu request = createChangeMenuRequest(price);

        assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴가격 변경시 가격은 메뉴에 포함된 각 상품 수량의 합보다 클 수 없다.")
    void changePrice_valid_priceSize() {
        UUID menuId = saveMenu().getId();
        Menu request = createChangeMenuRequest(BigDecimal.valueOf(20000L));

        assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴가격 변경시 기 등록된 메뉴만 가능하다.")
    void changePrice_exit_menu() {
        Menu request = createChangeMenuRequest(changeMenuPrice);

        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴 노출한다.")
    void display() {
        UUID menuId = saveMenu().getId();

        Menu menu = menuService.display(menuId);

        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴 노출 시 기 등록된 메뉴만 가능하다.")
    void display_exit_menu() {
        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴 노출 시 해당 메뉴의 가격은 메뉴에 포함된 각 상품 수량의 합보다 클 수 없다.")
    void display_valid_priceSize() {
        Product product = createProduct();
        productRepository.save(product);
        List<MenuProduct> menuProducts = createMenuProducts(Collections.singletonList(product));
        UUID menuId = saveMenu(menuProducts).getId();

        product.setPrice(BigDecimal.ZERO);
        productRepository.save(product);

        assertThatThrownBy(() -> menuService.display(menuId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("메뉴를 숨긴다.")
    void hide() {
        UUID menuId = saveMenu().getId();

        Menu menu = menuService.hide(menuId);

        assertThat(menu.getId()).isEqualTo(menuId);
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("메뉴 숨김 시 기 등록된 메뉴만 가능하다.")
    void hide_exit_menu() {
        assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴를 전체 조회한다.")
    void findAll() {
        saveMenu();
        saveMenu();

        List<Menu> menus = menuService.findAll();

        AssertionsForInterfaceTypes.assertThat(menus).hasSize(2);
    }

    private Menu saveMenu() {
        return saveMenu(menuProducts);
    }

    private Menu saveMenu(List<MenuProduct> menuProducts) {
        Menu request = createRequest(menuName, menuPrice, menuGroupId, menuProducts);
        return menuService.create(request);
    }

    private Menu createRequest(String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProducts) {
        Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(menuProducts);
        return request;
    }

    private Menu createChangeMenuRequest(BigDecimal price) {
        Menu request = new Menu();
        request.setPrice(price);
        return request;
    }

    private MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        return menuGroupRepository.save(menuGroup);
    }

    private List<MenuProduct> createMenuProducts() {
        Product product = createProduct();
        productRepository.save(product);
        return createMenuProducts(Collections.singletonList(product));
    }

    private List<MenuProduct> createMenuProducts(List<Product> products) {
        return products.stream()
                .map(this::getMenuProduct)
                .collect(Collectors.toList());
    }

    private List<MenuProduct> createMenuProducts(List<Product> products, long quantity) {
        return products.stream()
                .map(product -> getMenuProduct(product, quantity))
                .collect(Collectors.toList());
    }

    private MenuProduct getMenuProduct(Product product) {
        return getMenuProduct(product, productQuantity);
    }

    private MenuProduct getMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private Product createProduct() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(productPrice));
        return product;
    }
}
