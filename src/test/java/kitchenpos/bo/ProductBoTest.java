package kitchenpos.bo;

import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.Nullable;


@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("상품은 하나씩 등록 할 수 있다.")
    @Test
    void createOne() {
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        product.setName("마이상품");

        given(productDao.save(product)).willReturn(product);

        Assertions.assertThat(productBo.create(product)).isEqualTo(product);
    }

    @DisplayName("상품 가격이 올바르지 못하면 등록할수 없다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-10000"})
    void create(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);
        product.setName("마이상품");

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> productBo.create(product));
    }

    @DisplayName("전체 상품을 목록을 조회 할수 있다.")
    @Test
    void list() {
        List<Product> products = new ArrayList<>();
        products.add(new Product());

        given(productDao.findAll()).willReturn(products);

        Assertions.assertThat(productBo.list()).containsAll(products);
    }
}
