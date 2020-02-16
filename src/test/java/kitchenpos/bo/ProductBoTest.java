package kitchenpos.bo;

import kitchenpos.bo.mock.TestProductDao;
import kitchenpos.dao.Interface.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.Fixture.defaultProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductBoTest {

    private ProductDao productDao = new TestProductDao();

    private ProductBo productBo = new ProductBo(productDao);

    @DisplayName("가격이 null 이거나 0보다 작으면 IllegalArgumentException 에러를 낸다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-15000"})
    void name(BigDecimal price) {
        Product sample = new Product();
        sample.setPrice(price);

        assertThrows(IllegalArgumentException.class, () -> productBo.create(sample));
    }

    @DisplayName("정상적으로 저장되는 경우")
    @Test
    void create() {
        Product result = productBo.create(defaultProduct());
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("맛있는 찌낑");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(3000));
    }

    @DisplayName("상품 리스트 조회")
    @Test
    void list() {
        productDao.save(defaultProduct());

        List<Product> result = productBo.list();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("맛있는 찌낑");
        assertThat(result.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(3000));
    }
}
