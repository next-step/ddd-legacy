package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest
class ProductServiceTest {

    private final ProductService productService;

    @MockBean
    private PurgomalumClient purgomalumClient;

    public ProductServiceTest(ProductService productService) {
        this.productService = productService;
    }

    @Test
    void 상품을_등록할_수_있다() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(20_000L));
        product.setName("후라이드");

        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        Product actual = productService.create(product);
        assertThat(actual.getId()).isNotNull();
    }
}