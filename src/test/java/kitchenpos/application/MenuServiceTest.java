package kitchenpos.application;

import static kitchenpos.TestFixture.createMenuProductRequest;
import static kitchenpos.TestFixture.getSavedMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.menu.TestMenuRepository;
import kitchenpos.fake.menuGroup.TestMenuGroupRepository;
import kitchenpos.fake.product.TestProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;


class MenuServiceTest {
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        menuRepository = new TestMenuRepository();
        menuGroupRepository = new TestMenuGroupRepository();
        productRepository = new TestProductRepository();
    }

    @DisplayName("메뉴 생성 성공")
    @Test
    void createMenu() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        menuRequest.setMenuProducts(List.of(menuProduct));
        menuRequest.setDisplayed(true);

        // when
        Menu createdMenu = menuService.create(menuRequest);

        // then
        assertAll(
            () -> assertThat(createdMenu.getId()).isNotNull(),
            () -> assertThat(createdMenu.getPrice()).isEqualTo(menuPrice),
            () -> assertTrue(createdMenu.isDisplayed()),
            () -> assertThat(createdMenu.getName()).isEqualTo(menuName),
            () -> assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId()),
            () -> assertThat(createdMenu.getMenuProducts().size()).isEqualTo(1),
            () -> assertThat(createdMenu.getMenuProducts().get(0).getQuantity()).isEqualTo(2),
            () -> assertThat(createdMenu.getMenuProducts().get(0).getProduct().getId()).isEqualTo(product.getId())
        );
    }

    @DisplayName("메뉴 생성 실패 - 가격")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1})
    void createMenuFailPrice(Long menuPrice) {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal price = menuPrice == null ? null : BigDecimal.valueOf(menuPrice);
        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(price);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        menuRequest.setMenuProducts(List.of(menuProduct));
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴 생성 실패 - 이름")
    @ParameterizedTest
    @ValueSource(strings = {"bad"})
    @NullSource
    void createMenuNameFail(String menuName) {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> true);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        menuRequest.setMenuProducts(List.of(menuProduct));
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴 생성 실패 - 메뉴그룹 미존재")
    @Test
    void createMenuMenuGroupFail() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        menuGroupRequest.setId(UUID.randomUUID());

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName("hello");
        menuRequest.setMenuGroup(menuGroupRequest);
        menuRequest.setMenuGroupId(menuGroupRequest.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        menuRequest.setMenuProducts(List.of(menuProduct));
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(NoSuchElementException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴 생성 실패 - 메뉴상품 미존재")
    @Test
    void createMenuMenuProductFail() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName("hello");
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        menuRequest.setMenuProducts(List.of());
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴 생성 실패 - 상품개수 다름")
    @Test
    void createMenuProductCount() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        MenuProduct dummy = new MenuProduct();
        dummy.setQuantity(1L);
        dummy.setProductId(UUID.randomUUID());
        menuRequest.setMenuProducts(List.of(menuProduct, dummy));
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴 생성 실패 - 음수 수량")
    @Test
    void createMenuProductNegativeQuantity() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, -1);
        MenuProduct dummy = new MenuProduct();
        dummy.setQuantity(1L);
        dummy.setProductId(UUID.randomUUID());
        menuRequest.setMenuProducts(List.of(menuProduct, dummy));
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴 생성 실패 - 메뉴의 가격이 더 큼")
    @Test
    void createMenuFailPriceTooBig() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);

        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(BigDecimal.valueOf(1));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 1);
        MenuProduct dummy = new MenuProduct();
        dummy.setQuantity(1L);
        dummy.setProductId(UUID.randomUUID());
        menuRequest.setMenuProducts(List.of(menuProduct, dummy));
        menuRequest.setDisplayed(true);

        // when then
        assertThrows(IllegalArgumentException.class, () -> menuService.create(menuRequest));
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다")
    @Test
    void ChangeMenuPrice() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        BigDecimal originalPrice = BigDecimal.valueOf(5);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService, originalPrice, true,
            BigDecimal.TEN);

        // when
        BigDecimal newPrice = BigDecimal.valueOf(3);
        Menu priceChangeRequest = new Menu();
        priceChangeRequest.setPrice(newPrice);
        Menu menu = menuService.changePrice(savedMenu.getId(), priceChangeRequest);

        // then
        assertThat(menu.getPrice()).isEqualTo(newPrice);
    }

    @DisplayName("존재하지 않는 메뉴의 가격을 변경할 수 없다")
    @Test
    void ChangeMenuPriceFailNotExist() {
        // given
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);

        // when
        BigDecimal newPrice = BigDecimal.valueOf(3);
        Menu priceChangeRequest = new Menu();
        priceChangeRequest.setPrice(newPrice);

        // then
        assertThrows(NoSuchElementException.class, () -> menuService.changePrice(UUID.randomUUID(), priceChangeRequest));
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1})
    void ChangeMenuPriceFailInvalidPrice(Long price) {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        BigDecimal originalPrice = BigDecimal.valueOf(5);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService, originalPrice, true,
            BigDecimal.TEN);

        // when
        BigDecimal newPrice = price == null ? null : BigDecimal.valueOf(price);
        Menu priceChangeRequest = new Menu();
        priceChangeRequest.setPrice(newPrice);

        // then
        assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(UUID.randomUUID(), priceChangeRequest));
    }

    @DisplayName("메뉴를 전시할 수 있다")
    @Test
    void displayMenu() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService,  BigDecimal.valueOf(5), false,
            BigDecimal.TEN);

        // when
        Menu menu = menuService.display(savedMenu.getId());

        // then
        assertTrue(menu.isDisplayed());
    }

    @DisplayName("메뉴를 전시할 수 없다")
    @Test
    void displayMenuFail() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        BigDecimal originalPrice = BigDecimal.valueOf(5);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService, originalPrice, false,
            BigDecimal.TEN);
        savedMenu.setPrice(BigDecimal.valueOf(100));

        // when then
        assertThrows(IllegalStateException.class, () -> menuService.display(savedMenu.getId()));
    }


    @DisplayName("메뉴를 숨길 수 있다")
    @Test
    void hideMenu() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService,  BigDecimal.valueOf(5), true,
            BigDecimal.TEN);

        // when
        Menu menu = menuService.hide(savedMenu.getId());

        // then
        assertFalse(menu.isDisplayed());
    }

    @DisplayName("모든 메뉴를 조회할 수 있다")
    @Test
    void findAllMenus() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService,  BigDecimal.valueOf(5), true,
            BigDecimal.TEN);

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertAll(
            () -> assertThat(menus.size()).isEqualTo(1),
            () -> assertThat(menus.get(0).getId()).isEqualTo(savedMenu.getId())
        );
    }
}
