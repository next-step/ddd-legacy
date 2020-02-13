package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Stream;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private static Stream<BigDecimal> prices() {
        return Stream.of(null, BigDecimal.valueOf(-1));
    }

    @DisplayName("상품을 생성할 수 있다.")
    @Test
    void create() {
        Product product = new Product();
        product.setName("name");
        product.setPrice(BigDecimal.ONE);

        when(productDao.save(product)).thenReturn(product);
        assertThat(productBo.create(product)).isEqualTo(product);
    }

    @DisplayName("상품의 가격이 책정되지 않았거나 `0`보다 작은 값인 경우, 예외를 발생시킨다.")
    @ParameterizedTest
    @MethodSource("prices")
    void exceptionWhenCreate(BigDecimal price) {
        Product product = new Product();
        product.setName("name");
        product.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("상품 목록을 조회할 수 있다.")
    @Test
    void list() {
        when(productDao.findAll()).thenReturn(new ArrayList<>());
        assertThat(productBo.list()).isEmpty();
    }
}
