package kitchenpos.unit.application;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.testdouble.MenuGroupStubRepository;
import kitchenpos.testdouble.MenuStubRepository;
import kitchenpos.testdouble.ProductStubRepository;
import kitchenpos.testdouble.ProfanityClientStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuServiceTest {

    private MenuService menuService;

    private final MenuRepository menuRepository = new MenuStubRepository();
    private final MenuGroupRepository menuGroupRepository = new MenuGroupStubRepository();
    private final ProductRepository productRepository = new ProductStubRepository();
    private final ProfanityClient profanityClient = new ProfanityClientStub();

    @BeforeEach
    void setUp() {
        menuService = new MenuService(
                menuRepository,
                menuGroupRepository,
                productRepository,
                profanityClient
        );
    }

    @DisplayName("메뉴는 가격 없이는 생성할 수 없다.")
    @Test
    void menuWithoutPrice() {
        // Arrange
        Menu menu = new Menu();

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 등록된 메뉴 그룹 없이는 생성할 수 없다.")
    @Test
    void menuWithoutMenuGroup() {
        // Arrange
        Menu menu = createMenuWithoutMenuGroup();

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴가 가진 메뉴 제품이 없다면 생성할 수 없다.")
    @ParameterizedTest
    @EmptySource
    void menuWithoutMenuProducts(MenuProduct... menuProducts) {
        // Arrange
        MenuGroup menuGroup = 메뉴_그룹_등록();
        Menu menu = createMenuWithoutMenuProducts(menuGroup);
        menu.setMenuProducts(Arrays.asList(menuProducts));

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 가진 메뉴 제품의 개수와 등록된 메뉴 제품의 개수가 다르면 메뉴를 생성할 수 없다.")
    @Test
    void menuProductsCount() {
        // Arrange
        MenuGroup menuGroup = 메뉴_그룹_등록();
        Menu menu = createMenuWithoutMenuProducts(menuGroup);
        MenuProduct menuProduct = new MenuProduct();
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 가진 메뉴 제품의 총 수량이 0보다 작다면 메뉴를 생성할 수 없다.")
    @Test
    void menuProductsQuantityUnderZero() {
        // Arrange
        MenuGroup menuGroup = 메뉴_그룹_등록();
        Menu menu = createMenuWithoutMenuProducts(menuGroup);
        Product product = 제품_등록();
        MenuProduct menuProduct = createMenuProduct(-1);
        menuProduct.setProduct(product);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 메뉴가 가진 메뉴 제품 가격의 합보다 크면 메뉴를 생성할 수 없다.")
    @Test
    void menuPriceOverThanMenuProductsPrice() {
        // Arrange
        MenuGroup menuGroup = 메뉴_그룹_등록();
        Menu menu = createMenuWithoutMenuProducts(menuGroup);
        Product product = 제품_등록(BigDecimal.ONE);

        MenuProduct menuProduct = createMenuProduct(1);
        menuProduct.setProduct(product);
        menu.setMenuProducts(Arrays.asList(menuProduct));
        menu.setPrice(BigDecimal.TEN);

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 없다면 메뉴를 생성할 수 없다.")
    @Test
    void menuWithoutName() {
        // Arrange
        MenuGroup menuGroup = 메뉴_그룹_등록();
        Menu menu = createMenuWithoutMenuProducts(menuGroup);
        Product product = 제품_등록(BigDecimal.TEN);

        MenuProduct menuProduct = createMenuProduct(1);
        menuProduct.setProduct(product);
        menu.setMenuProducts(Arrays.asList(menuProduct));
        menu.setPrice(BigDecimal.ONE);

        // Act
        // Assert
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 성공.")
    @Test
    void createMenu() {
        // Arrange
        MenuGroup menuGroup = 메뉴_그룹_등록();
        Product product = 제품_등록(BigDecimal.TEN);

        MenuProduct menuProduct = createMenuProduct(1);
        menuProduct.setProduct(product);

        Menu menu = createMenu("menu name", menuGroup, BigDecimal.ONE, menuProduct);

        // Act
        Menu result = menuService.create(menu);

        // Assert
        assertThat(result.getId()).isNotNull();
    }

    private Menu createMenu(String name, MenuGroup menuGroup, BigDecimal price, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroup(menuGroup);
        menu.setPrice(price);
        menu.setMenuProducts(Arrays.asList(menuProducts));

        return menu;
    }

    private Product 제품_등록(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);
        return productRepository.save(product);
    }

    private MenuProduct createMenuProduct(int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private Product 제품_등록() {
        Product product = new Product();
        return productRepository.save(product);
    }

    private MenuGroup 메뉴_그룹_등록() {
        return menuGroupRepository.save(new MenuGroup());
    }

    private Menu createMenuWithoutMenuProducts(MenuGroup menuGroup) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.TEN);
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    private Menu createMenuWithoutMenuGroup() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.TEN);

        return menu;
    }
}
