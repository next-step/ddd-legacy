package kitchenpos.bo;

import kitchenpos.dao.InMemoryProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import kitchenpos.model.ProductTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductBoTest {
    private final ProductDao productDao = new InMemoryProductDao();
    private final ProductBo productBo = new ProductBo(productDao);

    @Test
    @DisplayName("요리는 추가될 수 있다.")
    void createTest() {
        final Product product = ProductTest.ofHalfFried();
        final Product productResult = productBo.create(product);
        assertAll(
                () -> assertThat(productResult.getId()).isEqualTo(product.getId()),
                () -> assertThat(productResult.getName()).isEqualTo(product.getName()),
                () -> assertThat(productResult.getPrice()).isEqualTo(product.getPrice())
        );
    }

    @Test
    @DisplayName("요리의 가격은 0원 이상이다.")
    void createWithUnderZeroPriceException() {
        final Product product = ProductTest.ofHalfFried();
        product.setPrice(BigDecimal.valueOf(-1000));
        assertThrows(IllegalArgumentException.class,
                () -> productBo.create(product));
    }

    @Test
    @DisplayName("모든 요리 목록을 조회할 수 있다.")
    void readAllProductList() {
        final Product product = productDao.save(ProductTest.ofHalfFried());
        assertThat(productBo.list()).contains(product);
    }
}
