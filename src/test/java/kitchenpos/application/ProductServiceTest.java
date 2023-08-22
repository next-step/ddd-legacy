package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import kitchenpos.domain.Product;
import kitchenpos.testHelper.SpringBootTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceTest extends SpringBootTestHelper {

    @Autowired
    ProductService productService;

    @BeforeEach
    public void init() {
        super.init();
    }

    @DisplayName("등록할 상품의 가격이 0보다 작으면 에러를 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3})
    void test1(int price) {
        //given
        Product request = new Product("name", BigDecimal.valueOf(price));

        //when && then
        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("등록할 상품의 이름은 반드시 있어야 한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test2(String name) {
        //given
        Product request = new Product(name, BigDecimal.valueOf(1L));

        //when && //then
        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);

    }
}