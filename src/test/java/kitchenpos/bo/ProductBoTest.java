package kitchenpos.bo;

import kitchenpos.dao.DefaultProductDao;
import kitchenpos.dao.InMemoryProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductBoTest {
    DefaultProductDao defaultProductDao = new InMemoryProductDao();
    ProductBo productBo;

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(defaultProductDao);
    }

    @Test
    @DisplayName("요리는 추가될 수 있다.")
    void createTest() {
        Product product = new Product();
        product.setId(1L);
        product.setName("후라이드 반마리");
        product.setPrice(BigDecimal.valueOf(7000L));
        productBo.create(product);
    }

    @Test
    @DisplayName("요리의 가격은 0원 이상이다.")
    void createWithUnderZeroPriceException() {
        Product product = new Product();
        product.setId(1L);
        product.setName("후라이드 반마리");
        product.setPrice(BigDecimal.valueOf(-1L));
        assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
    }

    @Test
    @DisplayName("모든 요리 목록을 조회할 수 있다.")
    void readAllProductList() {
        Product product = new Product();
        product.setId(1L);
        product.setName("후라이드 반마리");
        product.setPrice(BigDecimal.valueOf(7000L));
        productBo.create(product);
        assertThat(productBo.list()).contains(product);
    }
}
