package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품에는 반드시 이름이 있어야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nameIsMandatory(String name) {

        // given
        Product product = new Product();

        // when
        product.setName(name);

        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에는 비속어를 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"stupid"})
    void nameContainsProfanity(String name) {
        // given
        Product product = new Product();

        // when
        product.setName(name);

        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품에는 반드시 가격이 있어야 한다.")
    @Test
    void priceIsMandatory() {
        // given
        Product product = new Product();

        // when
        product.setName("후라이드치킨");
        product.setPrice(null);

        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격은 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-1000"})
    void negativePrice(String price) {
        // given
        Product product = new Product();

        // when
        product.setName("후라이드치킨");
        product.setPrice(new BigDecimal(price));

        // then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {

        // given
        Product productRequest = new Product();
        productRequest.setName("후라이드치킨");
        productRequest.setPrice(BigDecimal.ZERO);
        Product product = productService.create(productRequest);

        // when
        Product priceChangeRequest = new Product();
        priceChangeRequest.setPrice(new BigDecimal("1000"));
        productService.changePrice(product.getId(), priceChangeRequest);

        // then
        Optional<Product> changedProduct = productRepository.findById(product.getId());
        assertThat(changedProduct).isPresent();
        assertThat(changedProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("1000"));
    }
}
