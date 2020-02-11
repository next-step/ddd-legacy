package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @InjectMocks
    private ProductBo cut;
    @Mock
    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        lenient().when(productDao.save(argThat((product) -> product.getName() == null
                                                            || product.getPrice() == null)))
                 .thenThrow(RuntimeException.class);
    }

    @DisplayName("상품 등록 시 가격은 필수이고, 0보다 커야한다.")
    @Test
    void create_when_product_price_is_invalid() {
        SoftAssertions softly = new SoftAssertions();

        softly.assertThatThrownBy(() -> cut.create(validProductWithPrice(null)))
              .isInstanceOf(IllegalArgumentException.class);
        softly.assertThatThrownBy(() -> cut.create(validProductWithPrice(BigDecimal.ONE.negate())))
              .isInstanceOf(IllegalArgumentException.class);

        softly.assertAll();
    }

    @DisplayName("상품 등록 시 상품의 이름은 필수이다.")
    @Test
    void create_when_product_name_is_null() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> cut.create(validProductWithName(null)));
    }

    private static Product validProductWithName(String name) {
        Product product = validProduct();
        product.setName(name);
        return product;
    }

    private static Product validProductWithPrice(BigDecimal price) {
        Product product = validProduct();
        product.setPrice(price);
        return product;
    }

    private static Product validProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("상품");
        product.setPrice(BigDecimal.ONE);
        return product;
    }
}