package kitchenpos.bo;

import kitchenpos.dao.InMemoryProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InMemoryProductBoTest {

    private final ProductDao productDao = new InMemoryProductDao();
    private ProductBo productBo;

    @BeforeEach
    void setup (){
        productBo = new ProductBo(productDao);
    }

    @DisplayName("상품 가격을 입력하지않으면 등록 할 수 없다.")
    @Test
    void createWithoutPrice (){
        Product product = new Product();
        product.setId(1L);
        product.setName("뿌링클");

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(()->productBo.create(product));
    }

    @DisplayName("상품 가격이 올바르지 않으면 등록 할 수 없다.")
    @ParameterizedTest
    @ValueSource(longs = {-1, -5000})
    void createWithWrongPrice (final long price){
        Product product = new Product();
        product.setId(1L);
        product.setName("뿌링클");
        product.setPrice(BigDecimal.valueOf(price));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(()-> productBo.create(product));
    }

    @DisplayName("상품이 등록된다.")
    @Test
    void create(){
        Product product = new Product();
        product.setId(1L);
        product.setName("뿌링클");
        product.setPrice(BigDecimal.valueOf(10000));

        assertThat(productBo.create(product)).isEqualToComparingFieldByField(product);
    }
}
