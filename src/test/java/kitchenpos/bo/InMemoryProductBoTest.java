package kitchenpos.bo;

import kitchenpos.dao.InMemoryProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import kitchenpos.support.ProductBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @DisplayName("등록한 상품을 조회한다.")
    @Test
    void list (){
        List<Product> products = new ArrayList<>();
        Product product1 = new ProductBuilder()
            .id(1L)
            .name("뿌링클")
            .price(BigDecimal.valueOf(10000))
            .build();

        products.add(product1);
        productBo.create(product1);

        Product product2 = new ProductBuilder()
            .id(2L)
            .name("후라이드")
            .price(BigDecimal.valueOf(20000))
            .build();

        products.add(product2);
        productBo.create(product2);

        assertThat(productBo.list()).isEqualTo(products);
    }
}
