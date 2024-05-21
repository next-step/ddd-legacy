package kitchenpos.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.application.infra.FakePurgomalumClient;
import kitchenpos.application.menu.InMemoryMenuRepository;
import kitchenpos.application.menu_group.InMemoryMenuGroupRepository;
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

public class ProductServiceTest {

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

  @DisplayName("상품을 등록할 수 있다.")
  @Test
  public void register() {
    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);
    assertThat(product).isNotNull();
  }

  @DisplayName("상품가격이 0원 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {-1L, -100L})
  @ParameterizedTest
  public void invalidPrice(Long price) {
    Product product = ProductFixture.create("상품", price);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> productService.create(product));
  }

  @DisplayName("상품명이 욕설을 포함했거나 없을 경우 IllegalArgumentException 예외 처리를 한다.")
  @NullSource
  @ValueSource(strings = {"욕설", "비속어"})
  @ParameterizedTest
  public void invalidName(String name) {
    Product product = ProductFixture.create(name, -1L);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> productService.create(product));
  }

  @DisplayName("상품 가격을 변경할 수 있다.")
  @ValueSource(longs = {500L, 1000L})
  @ParameterizedTest
  public void modifyPrice(Long price) {
    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);
    Product request = ProductFixture.create("상품", price);
    product = productService.changePrice(product.getId(), request);
    assertThat(product.getPrice()).isEqualTo(new BigDecimal(price));
  }

  @DisplayName("변경할 상품가격이 0원 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(longs = {-500L, -1000L})
  @ParameterizedTest
  public void invalidPriceModification(Long price) {
    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);
    UUID productId = product.getId();
    Product request = ProductFixture.create("상품", price);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> productService.changePrice(productId, request));
  }

  @DisplayName("변경된 상품가격으로 인해 메뉴 판매 조건을 만족하지 못할 경우 메뉴를 비공개 처리한다.")
  @ValueSource(longs = {10L, 50L})
  @ParameterizedTest
  public void menuHide(Long price) {
    MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
    menuGroup = menuGroupService.create(menuGroup);

    Product product = ProductFixture.create("상품", 200L);
    product = productService.create(product);

    MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
    Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
    Menu menu = menuService.create(request);

    UUID productId = product.getId();
    Product requestProduct = ProductFixture.create("상품", price);
    productService.changePrice(productId, requestProduct);

    assertThat(menu.isDisplayed()).isFalse();
  }

  @DisplayName("등록된 상품 전체를 조회할 수 있다.")
  @Test
  public void InvalidateMenuGroupName() {
    Product product1 = ProductFixture.create("상품1", 100L);
    product1 = productService.create(product1);
    Product product2 = ProductFixture.create("상품2", 200L);
    product2 = productService.create(product2);

    List<Product> products = productService.findAll();
    assertThat(products).hasSize(2);
  }

}
