package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private PurgomalumClient purgomalumClient;

  @InjectMocks
  private ProductService productService;

  private Product product;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("강정치킨");
    product.setPrice(BigDecimal.valueOf(17000));
  }

  @DisplayName("상품 등록")
  @Test
  void createProductTest() {
    when(productRepository.save(any())).thenReturn(product);

    Product result = productService.create(product);

    assertThat(result.getName()).isEqualTo("강정치킨");
    assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
  }

  @DisplayName("상품 이름 null 등록 에러")
  @Test
  void createProductNameNull() {
    product.setName(null);

    assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("상품 가격 음수 등록 에러")
  @Test
  void createProductPriceNegative() {
    product.setPrice(BigDecimal.valueOf(-1));

    assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("상품 가격을 수정한다.")
  @Test
  void updatePrice() {
    when(productRepository.findById(any())).thenReturn(Optional.of(product));
    when(menuRepository.findAllByProductId(any())).thenReturn(List.of());

    Product chageProduct = new Product();
    chageProduct.setPrice(BigDecimal.valueOf(20000));

    Product result = productService.changePrice(product.getId(), chageProduct);

    assertThat(result.getName()).isEqualTo("강정치킨");
    assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(20000));
  }

  @DisplayName("상품 가격 음수 변경 에러")
  @Test
  void updatePriceNegative() {
    Product chageProduct = new Product();
    chageProduct.setPrice(BigDecimal.valueOf(-1));

    assertThatThrownBy(() -> productService.changePrice(product.getId(), chageProduct)).isInstanceOf(IllegalArgumentException.class);
  }
}