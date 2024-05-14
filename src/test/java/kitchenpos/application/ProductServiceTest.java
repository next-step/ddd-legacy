package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    void 상품을_등록할_수_있다() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(20_000L));
        product.setName("후라이드");
        given(purgomalumClient.containsProfanity(any())).willReturn(false);

        Product response = new Product();
        response.setId(UUID.randomUUID());
        response.setPrice(BigDecimal.valueOf(20_000L));
        response.setName("후라이드");
        given(productRepository.save(any())).willReturn(response);

        Product actual = productService.create(product);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 상품가격이_올바르지않으면_예외가_발생한다() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-10_000L));
        product.setName("후라이드");

        // given(purgomalumClient.containsProfanity(any())).willReturn(false);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(product));
    }
}