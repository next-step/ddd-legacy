package kitchenpos.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.application.fake.repository.InMemoryProductRepository;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.application.fake.infra.FakePurgomalumClient;
import kitchenpos.application.fake.repository.InMemoryMenuRepository;
import kitchenpos.application.fake.repository.InMemoryMenuGroupRepository;
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

  @Nested
  @DisplayName("상품을 등록할 수 있다.")
  class Register {
    @DisplayName("성공")
    @Test
    public void register() {
      String name = "상품명";
      Long price = 500L;
      Product product = ProductFixture.create(name, price);
      product = productService.create(product);
      assertThat(product.getId()).isNotNull();
      assertThat(product.getName()).isEqualTo(name);
      assertThat(product.getPrice()).isEqualTo(new BigDecimal(price));
    }

    @DisplayName("상품 가격은 0원 이상의 정수여야 한다.")
    @ValueSource(longs = {-1L, -100L})
    @ParameterizedTest
    public void invalidPrice(Long price) {
      Product product = ProductFixture.create(price);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> productService.create(product));
    }
    @DisplayName("상품명은 욕설이 포함될 수 없으며 0자 이상이어야 한다.")
    @NullSource
    @ValueSource(strings = {"욕설", "비속어"})
    @ParameterizedTest
    public void invalidName(String name) {
      Product product = ProductFixture.create(name);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> productService.create(product));
    }
  }

  @Nested
  @DisplayName("상품 가격을 변경할 수 있다.")
  class ChangePrice {
    @DisplayName("성공")
    @Test
    public void modifyPrice() {
      Product product = ProductFixture.normal();
      product = productService.create(product);
      long changePrice = 300L;
      Product priceChangeRequest = ProductFixture.create(changePrice);
      product = productService.changePrice(product.getId(), priceChangeRequest);
      assertThat(product.getPrice()).isEqualTo(new BigDecimal(changePrice));
    }

    @DisplayName("변경할 상품 가격은 0원 이상의 정수여야 한다.")
    @ValueSource(longs = {-500L, -1000L})
    @ParameterizedTest
    public void invalidPriceModification(Long changePrice) {
      Product product = ProductFixture.normal();
      product = productService.create(product);
      UUID productId = product.getId();
      Product priceChangeRequest = ProductFixture.create("상품", changePrice);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> productService.changePrice(productId, priceChangeRequest));
    }

    @DisplayName("상품이 속한 여러 메뉴들이 상품 가격 변경으로 `메뉴 판매 조건`을 만족하지 못할 경우 메뉴를 비공개 처리한다.")
    @ValueSource(longs = {10L, 50L})
    @ParameterizedTest
    public void menuHide(Long price) {
      MenuGroup menuGroup = MenuGroupFixture.normal();
      menuGroup = menuGroupService.create(menuGroup);

      Product product = ProductFixture.normal();
      product = productService.create(product);

      MenuProduct menuProduct = MenuFixture.createMenuProduct(product, 1L, 1L);
      Menu request = MenuFixture.createMenu("메뉴", menuGroup, List.of(menuProduct), 100L, true);
      Menu menu = menuService.create(request);

      UUID productId = product.getId();
      Product requestProduct = ProductFixture.create("상품", price);
      productService.changePrice(productId, requestProduct);

      assertThat(menu.isDisplayed()).isFalse();
    }
  }

  @DisplayName("등록된 상품 전체를 조회할 수 있다.")
  @Test
  public void InvalidateMenuGroupName() {
    int saveSize = 5;
    List<Product> requestList = ProductFixture.createList(saveSize);
    for (Product request : requestList) {
      productService.create(request);
    }
    List<Product> products = productService.findAll();
    assertThat(products).hasSize(saveSize);
  }

}
