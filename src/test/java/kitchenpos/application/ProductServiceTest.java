package kitchenpos.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixtures.Fixture;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock private ProductRepository productRepository;
  @Mock private MenuRepository menuRepository;
  @Mock private PurgomalumClient purgomalumClient;

  @InjectMocks private ProductService productService;

  @Test
  @DisplayName("상품을 등록하기 위해 이름과 금액을 입력해야 한다.")
  void test_case_1() {
    final Product expected = Fixture.fixtureProduct();
    given(productRepository.save(any())).willReturn(Fixture.fixtureProduct());

    final Product product = productService.create(Fixture.fixtureProduct());

    Assertions.assertThat(product.getName()).isEqualTo(expected.getName());
    Assertions.assertThat(product.getPrice()).isEqualTo(expected.getPrice());
  }

  @Test
  @DisplayName("상품을 등록하기 위해 이름에 비속어 또는 욕설을 작성할 수 없다.")
  void test_case_2() {
    final Product product = Fixture.fixtureProduct();

    Mockito.when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(
            () -> {
              productService.create(product);
            });
  }

  @Test
  @DisplayName("상품의 금액을 수정")
  void test_case_3() {
    final Product expected = Fixture.fixtureProduct();
    given(productRepository.findById(any())).willReturn(Optional.of(expected));
    given(menuRepository.findAllByProductId(any())).willReturn(List.of(Fixture.fixtureMenu()));

    expected.setPrice(BigDecimal.valueOf(10000L));
    final Product actual = productService.changePrice(UUID.randomUUID(), expected);

    Assertions.assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
  }

  @Test
  @DisplayName("상품의 금액을 수정하기 위해 상품이 존재해야 하고, 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
  void test_case_4() {
    final Product expected = Fixture.fixtureProduct();

    expected.setPrice(BigDecimal.valueOf(-1));
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> productService.changePrice(expected.getId(), expected));
  }

  @Test
  @DisplayName("상품의 금액을 수정하면 해당 상품이 등록된 모든 메뉴에 영향이 간다.")
  void test_case_5() {
    final Product expected = Fixture.fixtureProduct();
    final List<Menu> menus = List.of(Fixture.fixtureMenu());
    given(productRepository.findById(any())).willReturn(Optional.of(expected));
    given(menuRepository.findAllByProductId(any())).willReturn(menus);

    expected.setPrice(BigDecimal.valueOf(10L));
    productService.changePrice(expected.getId(), expected);

    Assertions.assertThat(menus.getFirst().isDisplayed()).isFalse();
  }

  @Test
  @DisplayName("상품 전체를 조회할 수 있다.")
  void test_case_6() {
    final List<Product> expected = List.of(Fixture.fixtureProduct());
    given(productRepository.findAll()).willReturn(expected);

    final List<Product> actual = productService.findAll();

    Assertions.assertThat(actual.size()).isEqualTo(expected.size());
  }
}
