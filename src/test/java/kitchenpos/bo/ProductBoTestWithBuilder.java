package kitchenpos.bo;

import kitchenpos.builder.ProductBuilder;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class ProductBoTestWithBuilder extends MockTest {
    @Mock
    private ProductDao productDao;
    @InjectMocks
    private ProductBo productBo;

    @DisplayName("상품을 생성할 때 상품의 가격을 반드시 입력해야 한다.")
    @ParameterizedTest
    @NullSource
    void createProductWithoutPriceTest(BigDecimal price) {
        //given
        Product givenProduct = ProductBuilder.product()
                .withPrice(price)
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ productBo.create(givenProduct); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품을 생성할 때 상품의 가격은 반드시 양수를 입력해야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1,-2})
    void createProductWithNegativePriceTest(int price) {
        //given
        Product givenProduct = ProductBuilder.product()
                .withPrice(BigDecimal.valueOf(price))
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ productBo.create(givenProduct); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 목록을 볼 수 있다.")
    @Test
    void list() {
        //given
        Product givenProduct = ProductBuilder.product()
                .build();
        List<Product> givenProducts = Arrays.asList(givenProduct);
        given(productDao.findAll())
                .willReturn(givenProducts);

        //when
        List<Product> actualProducts = productBo.list();

        //then
        assertThat(actualProducts.size())
                .isEqualTo(givenProducts.size());
    }
}
