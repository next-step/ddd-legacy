package kitchenpos.service;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static kitchenpos.step.ProductStep.createProduct;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Product 서비스 테스트")
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    @DisplayName("상품의 가격은 0 이상 이어야 한다")
    @Test
    void createWithNegativePrice() {
        // given
        ProductService productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        Product product = createProduct("강정치킨", -1);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
