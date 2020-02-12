package kitchenpos.bo;

import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductBoTest {

    @Autowired
    private ProductBo productBo;

    @Test
    @DisplayName("상품 정상 생성")
    void create() {
        String productName = "간장치킨";
        Product product = createProduct(productName, BigDecimal.valueOf(17000));

        Product savedProduct = productBo.create(product);

        assertThat(savedProduct.getName()).isEqualTo(productName);
    }

    @DisplayName("상품 가격이 0 보다 작을 경우 생성 실패")
    @ParameterizedTest
    @ValueSource(ints = -1000)
    void createFailByNegative(@ConvertWith(BigdecimalConverter.class) BigDecimal price) {
        Product product = createProduct("양념치킨", price);

        assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
    }

    @DisplayName("상품 가격이 Null 일 경우 생성 실패")
    @ParameterizedTest
    @NullSource
    void createFailByNull(BigDecimal price) {
        Product product = createProduct("후라이드치킨", price);

        assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
    }

    @Test
    @DisplayName("상품 리스트 조회 테스트")
    void list() {
        Product product1 = createProduct("양념치킨", BigDecimal.valueOf(17000));
        Product product2 = createProduct("후라이드치킨", BigDecimal.valueOf(16000));

        productBo.create(product1);
        productBo.create(product2);

        List<Product> list = productBo.list();

        assertThat(list.size()).isEqualTo(9);
    }

    private Product createProduct(String productName, BigDecimal price) {
        Product product = new Product();

        product.setName(productName);
        product.setPrice(price);

        return product;
    }

    static class BigdecimalConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object o, Class<?> aClass) throws ArgumentConversionException {
            return new BigDecimal(Integer.parseInt(o.toString()));
        }
    }
}
