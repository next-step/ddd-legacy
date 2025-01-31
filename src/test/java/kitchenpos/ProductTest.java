package kitchenpos;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName(value = "ProductService 테스트")
@Import(PurgomalumConfiguration.class)
public class ProductTest {

    public static final BigDecimal BIG_DECIMAL_MINUS_ONE = BigDecimal.valueOf(-1);
    public static final String TEST_PRODUCT_NAME = "TEST치킨";
    public static final UUID RANDOM_UUID = UUID.randomUUID();

    @Autowired
    private ProductService productService;
    @SpyBean
    private ProductRepository productRepository;
    @Autowired
    private PurgomalumClient mockPurgomalumClient;

    @DisplayName(value = "상품 등록 기능")
    @Nested
    class ProductCreateTest {

        @DisplayName(value = "가격은 0원 이상이어야 한다.")
        @Test
        void createProduct() {
            //상품명이나 가격이 없을때 에러처리
            Product product = Product(RANDOM_UUID, "", BIG_DECIMAL_MINUS_ONE);

            ThrowingCallable throwingCallable = () -> productService.create(product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "상품명이 없거나 비속어가 있으면 안됩니다.")
        @ParameterizedTest
        @CsvSource(value = {",", "fucking 맛있는 치킨"})
        void productInvalidName(final String productName) {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = Product(RANDOM_UUID, productName, BigDecimal.ONE);
            Mockito.when(mockPurgomalumClient.containsProfanity(productName)).thenReturn(true);

            //에러처리
            ThrowingCallable throwingCallable = () -> productService.create(product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "상품을 등록합니다.")
        @Test
        void productCreate() {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = Product(RANDOM_UUID, TEST_PRODUCT_NAME, BigDecimal.ONE);
            //에러처리
            productService.create(product);

            //행위검증
            verify(productRepository, times(1)).save(ArgumentCaptor.forClass(Product.class).capture());
        }

    }

    @DisplayName(value = "상품 가격 변경 기능")
    @Nested
    class ProductPriceChangeTest {

        @DisplayName(value = "변경할 상품의 가격은 0원 이상이어야 한다.")
        @Test
        void zeroProductPrice() {
            //상품명이나 가격이 없을때 에러처리
            Product product = Product(RANDOM_UUID, TEST_PRODUCT_NAME, BIG_DECIMAL_MINUS_ONE);

            ThrowingCallable throwingCallable = () -> productService.changePrice(RANDOM_UUID, product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

    }
















    private static Product Product(String name, int price) {
        return Product(null, name, new BigDecimal(price));
    }

    private static Product Product(UUID uuid, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
