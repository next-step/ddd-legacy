package kitchenpos.application.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.application.infra.FakePurgomalumClient;
import kitchenpos.application.menu_group.InMemoryMenuGroupRepository;
import kitchenpos.application.product.InMemoryProductRepository;
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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuServiceTest {

  private MenuService menuService;
  private MenuGroupService menuGroupService;
  private ProductService productService;

  @BeforeEach
  public void init() {
    MenuRepository menuRepository = new InMemoryMenuRepository();
    ProductRepository productRepository = new InMemoryProductRepository();
    MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
        purgomalumClient);
    menuGroupService = new MenuGroupService(menuGroupRepository);
    productService = new ProductService(productRepository, menuRepository, purgomalumClient);
  }

  @DisplayName("메뉴를 등록할 수 있다.")
  @Test
  public void register() {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
    Menu menu = menuService.create(request);
    assertThat(menu).isNotNull();
  }

  @DisplayName("메뉴명이 욕설을 포함했거나 없을 경우 IllegalArgumentException 예외 처리를 한다.")
  @NullSource
  @ValueSource(strings = {"욕설", "비속어"})
  @ParameterizedTest
  public void invalidMenuName(String name) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu(name, menuGroup, List.of(menuProduct), 100L, true);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request));
  }

  @DisplayName("메뉴 가격이 0원 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {-1L, -100L})
  @ParameterizedTest
  public void invalidMenuPrice(Long price) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request));
  }

  @DisplayName("등록되지 않은 메뉴그룹을 포함할 경우 NoSuchElementException 예외 처리를 한다.")
  @Test
  public void invalidMenuGroup() {
    MenuGroup menuGroup = MenuGroupFixture.createFake("메뉴그룹");

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
    assertThatExceptionOfType(NoSuchElementException.class)
        .isThrownBy(() -> menuService.create(request));
  }

  @DisplayName("메뉴 상품을 등록하지 않은 경우 IllegalArgumentException 예외 처리를 한다.")
  @Test
  public void nullAndEmptyMenuProducts() {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Menu request1 = MenuFixture.createMenu("메뉴", menuGroup, List.of(), 100L, true);
    Menu request2 = MenuFixture.createMenu("메뉴", menuGroup, null, 100L, true);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request1));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request2));
  }

  @DisplayName("등록된 상품을 포함하지 않은 경우 IllegalArgumentException 예외 처리를 한다.")
  @Test
  public void invalidMenuProducts() {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product1 = ProductFixture.create("상품", 200L);
    Product product2 = ProductFixture.createFake("상품", 200L);
    product1 = productService.create(product1);

    MenuProduct menuProduct1 = MenuFixture.createMenuProduct(product1, 1L, 1L);
    MenuProduct menuProduct2 = MenuFixture.createMenuProduct(product2, 1L, 1L);

    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct1, menuProduct2),
        100L, true);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request));
  }

  @DisplayName("메뉴 상품의 수량이 0개 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {-1L, -100L})
  @ParameterizedTest
  public void invalidMenuProductQuantity(Long quantity) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, quantity, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request));
  }

  @DisplayName("메뉴 판매 조건을 만족하지 않을 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {300L, 400L})
  @ParameterizedTest
  public void invalidMenuSellingCondition(Long price) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.create(request));
  }

  @DisplayName("메뉴가격을 변경할 수 있다.")
  @ValueSource(longs = {50L, 100L})
  @ParameterizedTest
  public void modifyMenuPrice(Long price) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 150L, true);
    Menu menu = menuService.create(request);

    Menu change = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
    change = menuService.changePrice(menu.getId(), change);
    assertThat(change.getPrice()).isEqualTo(new BigDecimal(price));
  }

  @DisplayName("변경할 메뉴 가격이 0원 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {-50L, -100L})
  @ParameterizedTest
  public void invalidMenuPriceModification(Long price) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 150L, true);
    Menu menu = menuService.create(request);

    Menu change = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.changePrice(menu.getId(), change));
  }

  @DisplayName("변경할 메뉴 가격이 메뉴 판매 조건을 만족하지 못할 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {300L, 4000L})
  @ParameterizedTest
  public void invalidMenuPriceModificationBecauseMenuSellingCondition(Long price) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 150L, true);
    Menu menu = menuService.create(request);

    Menu change = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> menuService.changePrice(menu.getId(), change));
  }

  @DisplayName("메뉴를 공개할 수 있다.")
  @Test
  public void display() {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, false);
    Menu menu = menuService.create(request);
    menu = menuService.display(menu.getId());

    assertThat(menu.isDisplayed()).isTrue();
  }

  @DisplayName("메뉴를 비공개할 수 있다.")
  @Test
  public void hide() {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
    Menu menu = menuService.create(request);
    menu = menuService.hide(menu.getId());

    assertThat(menu.isDisplayed()).isFalse();
  }

  @DisplayName("등록된 메뉴 전체를 조회할 수 있다.")
  @Test
  public void InvalidateMenuGroupName() {
    MenuGroup menuGroup1 = MenuGroupFixture.create("메뉴그룹");
    menuGroup1 = menuGroupService.create(menuGroup1);

    Product product1 = ProductFixture.create("상품", 200L);
    product1 = productService.create(product1);

    MenuProduct menuProduct1 = MenuFixture.createMenuProduct(product1, 1L, 1L);
    Menu request1 = MenuFixture.createMenu("메뉴", menuGroup1, List.of(menuProduct1), 100L, true);
    Menu menu1 = menuService.create(request1);

    MenuGroup menuGroup2 = MenuGroupFixture.create("메뉴그룹");
    menuGroup2 = menuGroupService.create(menuGroup2);

    Product product2 = ProductFixture.create("상품", 200L);
    product2 = productService.create(product2);

    MenuProduct menuProduct2 = MenuFixture.createMenuProduct(product2, 1L, 1L);
    Menu request2 = MenuFixture.createMenu("메뉴", menuGroup2, List.of(menuProduct2), 100L, true);
    Menu menu2 = menuService.create(request2);

    List<Menu> menus = menuService.findAll();
    assertThat(menus).hasSize(2);
  }
}
