package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private Product expected = null;

    @BeforeEach
    void setUp() {
        expected = new Product();
        expected.setName("coke");
        expected.setPrice(BigDecimal.valueOf(1000L));

    }

    @DisplayName("상품을 등록할수있다.")
    @Test
    void createProduct() {
        //given
        given(productDao.save(any(Product.class)))
                .willReturn(expected);
        //when
        final Product actual = productBo.create(expected);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @DisplayName("상품 금액은 0원보다 커야한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-10000"})
    void validPrice(final BigDecimal price) {
        //given
        expected.setPrice(price);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productBo.create(expected));
    }

    @DisplayName("상품 목록을 확인할 수 있다.")
    @Test
    void getProducts() {
        //given
        given(productDao.findAll()).willReturn(
                Arrays.asList(expected)
        );
        //when
        List<Product> actual = productBo.list();

        //then
        assertThat(actual).isEqualTo(productDao.findAll());
    }
}
