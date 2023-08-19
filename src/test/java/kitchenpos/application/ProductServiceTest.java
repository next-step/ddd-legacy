package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.utils.fake.FakePurgomalumClient;
import kitchenpos.utils.fake.FakePurgomalumResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuRepository menuRepository;

    private final ProductService productService = new ProductService(productRepository, menuRepository, new FakePurgomalumClient());

    @DisplayName("[예외] 상품의 이름은 공백일 수 없다.")
    @ParameterizedTest
    @NullSource
    void name_test_1(String name) {
        //when
        Product product = ProductFixture.create(name);
        //then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("[예외] 상품의 이름은 비속어일 수 없다.")
    @Test
    void name_test_2() {
        //when
        Product product = ProductFixture.create(FakePurgomalumResponse.비속어.name());
        //then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 상품의 가격은 공백일 수 없다.")
    @ParameterizedTest
    @NullSource
    void price_test_1(BigDecimal price) {
        //when
        Product product = ProductFixture.create(price);
        //then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }


}
