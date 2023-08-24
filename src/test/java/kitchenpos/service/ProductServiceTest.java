package kitchenpos.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private final PurgomalumClient purgomalumClient = new PurgomalumClient(new RestTemplateBuilder());
    private final ProductService productService = new ProductService(null, null, purgomalumClient);

    @Test
    void 상품_생성_실패__가격이_null() {
        Product product = new Product();
        product.setPrice(null);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_실패__가격이_음수() {
        Product product = new Product();
        product.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격_변경_실패__가격이_null() {
        UUID productId = null;
        Product product = new Product();
        product.setPrice(null);

        assertThatThrownBy(() -> productService.changePrice(productId, product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격_변경_실패__가격이_음수() {
        UUID productId = null;
        Product product = new Product();
        product.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> productService.changePrice(productId, product))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
