package kitchenpos.application;

import static kitchenpos.MenuTestFixture.createMenuGroupRequest;
import static kitchenpos.MenuTestFixture.createMenuProductRequest;
import static kitchenpos.MenuTestFixture.createMenuRequest;
import static kitchenpos.MenuTestFixture.getSavedMenu;
import static kitchenpos.ProductTestFixture.createProductRequest;
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

        Product productRequest = createProductRequest("감자튀김", BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = createMenuGroupRequest("두 마리 메뉴그룹");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "두 마리 메뉴";
        Menu menuRequest = createMenuRequest(menuPrice, menuName, menuGroup, product, 2);

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
            () -> assertThat(createdMenu.getMenuProducts().getFirst().getQuantity()).isEqualTo(2),
            () -> assertThat(createdMenu.getMenuProducts().getFirst().getProduct().getId()).isEqualTo(product.getId())
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

        Product productRequest = createProductRequest("피자", BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = createMenuGroupRequest("피자그룹");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal price = menuPrice == null ? null : BigDecimal.valueOf(menuPrice);
        String menuName = "피자메뉴";
        Menu menuRequest = createMenuRequest(price, menuName, menuGroup, product, 2);

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

        Product productRequest = createProductRequest("양념치킨", BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = createMenuGroupRequest("두 마리그룹");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        Menu menuRequest = createMenuRequest(menuPrice, menuName, menuGroup, product, 2);

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

        Product productRequest = createProductRequest("양념치킨", BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup notSavedMenuGroup = new MenuGroup();
        notSavedMenuGroup.setName("양념치킨그룹");
        notSavedMenuGroup.setId(UUID.randomUUID());

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        Menu menuRequest = createMenuRequest(menuPrice, "매운 양념 메뉴", notSavedMenuGroup, List.of(menuProduct));
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
        menuGroupRequest.setName("버거그룹");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        Menu menuRequest = createMenuRequest(menuPrice, "버거", menuGroup, List.of());
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
        productRequest.setName("과일");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("후식");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "모듬 과일";
        MenuProduct menuProduct = createMenuProductRequest(product, 2);
        MenuProduct dummy = new MenuProduct();
        dummy.setQuantity(1L);
        dummy.setProductId(UUID.randomUUID());
        Menu menuRequest = createMenuRequest(menuPrice, menuName, menuGroup, List.of(menuProduct, dummy));
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
        productRequest.setName("버거");
        productRequest.setPrice(BigDecimal.valueOf(20));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("빅 버거 그룹");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "매운 버거";
        MenuProduct menuProduct = createMenuProductRequest(product, -1);
        MenuProduct dummy = new MenuProduct();
        dummy.setQuantity(1L);
        dummy.setProductId(UUID.randomUUID());
        Menu menuRequest = createMenuRequest(menuPrice, menuName, menuGroup, List.of(menuProduct, dummy));

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

        Product productRequest = createProductRequest("cheeze burger", BigDecimal.valueOf(1));
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = createMenuGroupRequest("Burger Group");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        BigDecimal menuPrice = BigDecimal.valueOf(10);
        String menuName = "Big Burger";
        MenuProduct menuProduct = createMenuProductRequest(product, 1);
        Menu menuRequest = createMenuRequest(menuPrice, menuName, menuGroup, List.of(menuProduct));

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
        assertThrows(NoSuchElementException.class,
            () -> menuService.changePrice(UUID.randomUUID(), priceChangeRequest));
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1})
    void ChangeMenuPriceFailInvalidPrice(Long price) {
        // given
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);

        // when
        BigDecimal newPrice = price == null ? null : BigDecimal.valueOf(price);
        Menu priceChangeRequest = new Menu();
        priceChangeRequest.setPrice(newPrice);

        // then
        assertThrows(IllegalArgumentException.class,
            () -> menuService.changePrice(UUID.randomUUID(), priceChangeRequest));
    }

    @DisplayName("메뉴를 전시할 수 있다")
    @Test
    void displayMenu() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            (text) -> false);
        ProductService productService = new ProductService(productRepository, menuRepository, (text) -> false);
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService, BigDecimal.valueOf(5), false,
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
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService, BigDecimal.valueOf(5), true,
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
        Menu savedMenu = getSavedMenu(productService, menuService, menuGroupService, BigDecimal.valueOf(5), true,
            BigDecimal.TEN);

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertAll(
            () -> assertThat(menus.size()).isEqualTo(1),
            () -> assertThat(menus.getFirst().getId()).isEqualTo(savedMenu.getId())
        );
    }
}
