package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.fake.menu.TestMenuRepository;
import kitchenpos.fake.product.TestProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

    @Test
    @DisplayName("상품생성 성공 테스트")
    void create_product_success() {
        // given
        ProductService productService = new ProductService(
            new TestProductRepository(),
            new TestMenuRepository(),
            (name) -> false
        );
        UUID productId = UUID.randomUUID();
        BigDecimal price= BigDecimal.ONE;
        String name = "goodName";
        Product request = new Product();
        request.setId(productId);
        request.setPrice(price);
        request.setName(name);

        // when
        Product product = productService.create(request);

        // then
        assertAll(
            () -> assertThat(product.getId()).isEqualTo(productId),
            () -> assertThat(product.getPrice()).isEqualTo(price),
            () -> assertThat(product.getName()).isEqualTo(name)
        );
    }
}
