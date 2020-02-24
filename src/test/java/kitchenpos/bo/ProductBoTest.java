package kitchenpos.bo;

import kitchenpos.dao.InMemoryProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import kitchenpos.model.ProductTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductBoTest {
    private ProductDao productDao = new InMemoryProductDao();
    private ProductBo productBo;

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(productDao);
    }

    @Test
    @DisplayName("요리는 추가될 수 있다.")
    void createTest() {
        Product product = ProductTest.ofHalfFried();
        Product productResult;

        assertThat(productResult = productBo.create(product));
        assertAll(
                () -> assertThat(productResult.getId()).isEqualTo(product.getId()),
                () -> assertThat(productResult.getName()).isEqualTo(product.getName()),
                () -> assertThat(productResult.getPrice()).isEqualTo(product.getPrice())
        );
    }

    @Test
    @DisplayName("요리의 가격은 0원 이상이다.")
    void createWithUnderZeroPriceException() {
        Product product = ProductTest.ofHalfFried();
        product.setPrice(BigDecimal.valueOf(-1000));
        assertThrows(IllegalArgumentException.class,
                () -> productBo.create(product));
    }

    @Test
    @DisplayName("모든 요리 목록을 조회할 수 있다.")
    void readAllProductList() {
        Product product = ProductTest.ofHalfFried();
        product = productDao.save(product);
        assertThat(productBo.list()).contains(product);
    }
}
