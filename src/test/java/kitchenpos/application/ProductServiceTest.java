package kitchenpos.application;

import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Nested
    class createTest {
    }

    @Nested
    class changePriceTest {
    }

    @Nested
    class findAllTest {
        @DisplayName("상품 목록을 조회한다.")
        @Test
        void findAllSuccessTest() {
            Product friedChickenProduct = createProduct("후라이드 치킨", BigDecimal.valueOf(16000));
            Product seasonedChickenProduct = createProduct("양념 치킨", BigDecimal.valueOf(16000));

            friedChickenProduct = productService.create(friedChickenProduct);
            seasonedChickenProduct = productService.create(seasonedChickenProduct);

            final List<Product> products = productService.findAll();
            List<UUID> productIds = products.stream()
                    .map(Product::getId)
                    .toList();
            
            assertThat(products).hasSize(2);
            assertThat(productIds).contains(friedChickenProduct.getId(), seasonedChickenProduct.getId());
        }
    }
}
