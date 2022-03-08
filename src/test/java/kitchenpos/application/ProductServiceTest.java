package kitchenpos.application;

import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

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
}
