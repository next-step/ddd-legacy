package kitchenpos.application.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.application.MenuService;
import kitchenpos.application.fake.repository.InMemoryMenuRepository;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.application.fake.infra.FakePurgomalumClient;
import kitchenpos.application.fake.repository.InMemoryMenuGroupRepository;
import kitchenpos.application.fake.repository.InMemoryProductRepository;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MenuServiceTest {

  private MenuService menuService;
  private MenuGroupRepository menuGroupRepository;
  private ProductRepository productRepository;

  private MenuGroup menuGroup;
  private Product product;

  @BeforeEach
  public void init() {
    MenuRepository menuRepository = new InMemoryMenuRepository();
    this.productRepository = new InMemoryProductRepository();
    this.menuGroupRepository = new InMemoryMenuGroupRepository();
    PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
        purgomalumClient);

    MenuGroup menuGroup = MenuGroupFixture.normal();
    this.menuGroup = menuGroupRepository.save(menuGroup);
    Product product = ProductFixture.normal();
    this.product = productRepository.save(product);
  }

  @Nested
  @DisplayName("메뉴를 등록할 수 있다.")
  class Register {
    @DisplayName("성공")
    @Test
    public void register() {
      String name = "메뉴";
      Long price = 100L;
      boolean displayed = true;
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu(name, menuGroup, List.of(menuProduct), price, true);
      Menu menu = menuService.create(request);
      assertAll(
          () -> assertThat(menu.getId()).isNotNull(),
          () -> assertThat(menu.getPrice()).isEqualTo(new BigDecimal(price)),
          () -> assertThat(menu.getName()).isEqualTo(name),
          () -> assertThat(menu.isDisplayed()).isEqualTo(displayed)
      );
    }
    @DisplayName("메뉴명은 욕설이 포함될 수 없다.")
    @NullSource
    @ValueSource(strings = {"욕설", "비속어"})
    @ParameterizedTest
    public void invalidMenuName(String name) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu(name, menuGroup, List.of(menuProduct), 100L, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 가격은 0원 이상의 정수여야한다.")
    @ValueSource(longs = {-1L, -100L})
    @ParameterizedTest
    public void invalidMenuPrice(Long price) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴는 등록된 메뉴그룹에 포함되어야한다.")
    @Test
    public void invalidMenuGroup() {
      MenuGroup menuGroup = MenuGroupFixture.normal();
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
      assertThatExceptionOfType(NoSuchElementException.class)
          .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("1개 이상의 메뉴상품을 등록해야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    public void nullAndEmptyMenuProducts(List<MenuProduct> products) {
      Menu menu = MenuFixture.createMenu("메뉴", menuGroup, products, 100L, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴상품은 등록된 상품을 포함해야한다.")
    @Test
    public void invalidMenuProducts() {
      Product product2 = ProductFixture.normal();
      MenuProduct menuProduct1 = MenuFixture.createMenuProduct(product, 1L, 1L);
      MenuProduct menuProduct2 = MenuFixture.createMenuProduct(product2, 1L, 1L);

      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct1, menuProduct2),
          100L, true);

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴상품의 수량은 0개 이상이어야한다.")
    @ValueSource(longs = {-1L, -100L})
    @ParameterizedTest
    public void invalidMenuProductQuantity(Long quantity) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, quantity, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 판매 조건을 만족해야한다.")
    @ValueSource(longs = {1_100L, 3_000L})
    @ParameterizedTest
    public void invalidMenuSellingCondition(Long price) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.create(request));
    }
  }

  @Nested
  @DisplayName("메뉴가격을 변경할 수 있다.")
  class ChangePrice {
    @DisplayName("성공")
    @ValueSource(longs = {50L, 100L})
    @ParameterizedTest
    public void modifyMenuPrice(Long price) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 150L, true);
      Menu menu = menuService.create(request);

      Menu change = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
      change = menuService.changePrice(menu.getId(), change);
      assertThat(change.getPrice()).isEqualTo(new BigDecimal(price));
    }

    @DisplayName("변경할 메뉴 가격은 0원 이상의 정수여야한다.")
    @ValueSource(longs = {-50L, -100L})
    @ParameterizedTest
    public void invalidMenuPriceModification(Long price) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 150L, true);
      Menu menu = menuService.create(request);

      Menu change = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.changePrice(menu.getId(), change));
    }

    @DisplayName("변경할 메뉴 가격이 메뉴 판매 조건을 만족해야한다.")
    @ValueSource(longs = {3_300L, 4_400L})
    @ParameterizedTest
    public void invalidMenuPriceModificationBecauseMenuSellingCondition(Long price) {
      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 150L, true);
      Menu menu = menuService.create(request);

      Menu change = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), price, true);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> menuService.changePrice(menu.getId(), change));
    }
  }

  @DisplayName("메뉴를 공개할 수 있다.")
  @Test
  public void display() {
    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, false);
    Menu menu = menuService.create(request);
    menu = menuService.display(menu.getId());

    assertThat(menu.isDisplayed()).isTrue();
  }

  @DisplayName("메뉴를 비공개할 수 있다.")
  @Test
  public void hide() {
    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
    Menu menu = menuService.create(request);
    menu = menuService.hide(menu.getId());

    assertThat(menu.isDisplayed()).isFalse();
  }

  @DisplayName("등록된 메뉴 전체를 조회할 수 있다.")
  @Test
  public void InvalidateMenuGroupName() {
    MenuProduct menuProduct1 = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request1 = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct1), 100L, true);
    Menu menu1 = menuService.create(request1);

    Product product2 = ProductFixture.create("상품", 200L);
    product2 = productRepository.save(product2);

    MenuProduct menuProduct2 = MenuFixture.createMenuProduct(product2, 1L, 1L);
    Menu request2 = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct2), 100L, true);
    Menu menu2 = menuService.create(request2);

    List<Menu> menus = menuService.findAll();
    assertThat(menus).hasSize(2);
  }
}
