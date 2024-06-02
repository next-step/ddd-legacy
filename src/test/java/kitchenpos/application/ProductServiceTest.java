package kitchenpos.application;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixtures.FixtureMenu;
import kitchenpos.fixtures.FixtureProduct;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.infra.menu.InMemoryMenuRepository;
import kitchenpos.infra.product.InMemoryProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  private final ProductRepository productRepository = new InMemoryProductRepository();
  private final MenuRepository menuRepository = new InMemoryMenuRepository();
  private ProductService productService;
  @Mock private PurgomalumClient purgomalumClient;

  @BeforeEach
  void beforeEach() {
    this.productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    this.productRepository.save(FixtureProduct.fixtureProduct());
  }

  @Nested
  @DisplayName("상품 등록")
  class Nested1 {
    @Test
    @DisplayName("상품을 등록하기 위해 이름과 금액을 입력해야 한다.")
    void test_case_1() {
      final Product expected = FixtureProduct.fixtureProduct();
      final Product product = productService.create(FixtureProduct.fixtureProduct());

      Assertions.assertThat(product.getName()).isEqualTo(expected.getName());
      Assertions.assertThat(product.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    @DisplayName("상품을 등록하기 위해 이름에 비속어 또는 욕설을 작성할 수 없다.")
    void test_case_2() {
      final Product product = FixtureProduct.fixtureProduct();

      Mockito.when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(
              () -> {
                productService.create(product);
              });
    }
  }

  @Nested
  @DisplayName("상품 금액 수정")
  class Nested2 {
    @Test
    @DisplayName("상품의 금액을 수정")
    void test_case_3() {
      final Product expected = FixtureProduct.fixtureProduct();
      productRepository.save(expected);

      expected.setPrice(BigDecimal.valueOf(10000L));
      final Product actual = productService.changePrice(expected.getId(), expected);

      Assertions.assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    @DisplayName("상품의 금액을 수정하기 위해 상품이 존재해야 하고, 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
    void test_case_4() {
      final Product expected = FixtureProduct.fixtureProduct();

      expected.setPrice(BigDecimal.valueOf(-1));
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(() -> productService.changePrice(expected.getId(), expected));
    }

    @Test
    @DisplayName("상품의 금액을 수정하면 해당 상품이 등록된 모든 메뉴에 영향이 간다.")
    void test_case_5() {
      final List<Menu> menus = List.of(FixtureMenu.fixtureMenu());
      final Product expected = menus.getFirst().getMenuProducts().getFirst().getProduct();
      final Product request = FixtureProduct.fixtureProduct();
      productRepository.save(expected);
      menus.forEach(menuRepository::save);

      request.setPrice(BigDecimal.valueOf(10L));
      productService.changePrice(expected.getId(), request);

      Assertions.assertThat(menus.getFirst().isDisplayed()).isFalse();
    }
  }

  @Nested
  @DisplayName("상품 전체 조회")
  class Nested3 {
    @Test
    @DisplayName("상품 전체를 조회할 수 있다.")
    void test_case_6() {
      final List<Product> actual = productService.findAll();
      Assertions.assertThat(actual.size()).isEqualTo(1);
    }
  }
}
