package kitchenpos.application;

import static kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum.SLANG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import kitchenpos.domain.Product;
import kitchenpos.testHelper.SpringBootTestHelper;
import kitchenpos.testHelper.fake.PurgomalumClientFake;
import kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceTest extends SpringBootTestHelper {

    @Autowired
    ProductService productService;

    @Autowired
    PurgomalumClientFake purgomalumClient;

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

    @DisplayName("등록할 상품의 이름은 비속어를 넣을 수 없다")
    @Test
    void test3 (){
        //given
        purgomalumClient.setReturn(SLANG);
        Product request = new Product("name", BigDecimal.valueOf(1L));

        //when && //then
        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}