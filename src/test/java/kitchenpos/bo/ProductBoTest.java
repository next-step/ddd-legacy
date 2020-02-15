package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import kitchenpos.model.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

class ProductBoTest extends MockTest {
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("상품을 등록할 수 있다")
    @Test
    void createProduct() {
        Product expected = TestFixtures.customPriceProduct(new BigDecimal(10000));

        //given
        given(productDao.save(expected)).willReturn(expected);

        //when
        Product result = productBo.create(expected);

        //then
        assertAll(
                () -> assertThat(result.getName()).isEqualTo(expected.getName()),
                () -> assertThat(result.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @DisplayName("상품을 조회할 수 있다")
    @Test
    void listProduct() {
        List<Product> expected = new ArrayList<>();
        Product product = TestFixtures.customPriceProduct(new BigDecimal(10000));
        expected.add(product);

        //given
        given(productDao.findAll()).willReturn(expected);

        //when
        List<Product> result = productBo.list();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("가격이 올바르지 않은 상품의 등록")
    @ParameterizedTest
    @MethodSource("createInvalidPriceProduct")
    void invalidPriceProduct(BigDecimal price) {
        Product expected = TestFixtures.customPriceProduct(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(expected));
    }

    static Stream<BigDecimal> createInvalidPriceProduct() {
        return Stream.of(null, new BigDecimal(-10000));
    }
}
