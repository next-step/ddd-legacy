package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  private ProductService productService;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private PurgomalumClient purgomalumClient;

  @BeforeEach
  void setUp() {
    this.productService = new ProductService(productRepository, menuRepository, purgomalumClient);
  }

  @Test
  @DisplayName("유효한 상품명과 가격을 입력하여 상품을 등록할 수 있다.")
  void whenCreate_thenReturnProduct() {
    // given
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("후라이드치킨");
    product.setPrice(BigDecimal.valueOf(11000));

    given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
    given(productRepository.save(any(Product.class))).willReturn(product);

    // when
    Product savedProduct = productService.create(product);

    // then
    assertThat(savedProduct.getId()).isNotNull();
    assertThat(savedProduct.getName()).isEqualTo(product.getName());
    assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
  }

}
