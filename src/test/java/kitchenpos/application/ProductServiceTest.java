package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private ProductService productService;

    private static final String 상품명 = "상품명";
    private static final long 빵원 = 0;
    private static final long 만원 = 10_000L;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("상품 등록 테스트")
    class SaveProduct {
        @ParameterizedTest
        @ValueSource(longs = {빵원, 만원})
        @DisplayName("상품을 정상적으로 등록할 수 있다.")
        void success(final long price) {
            var product = createProduct(상품명, price);
            var response = createProduct(상품명, price);

            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(productRepository.save(any())).willReturn(response);

            Product actual = productService.create(product);

            assertAll(
                    "상품 정보 그룹 Assertions",
                    () -> assertNotNull(actual.getId()),
                    () -> assertEquals(actual.getName(), response.getName()),
                    () -> assertEquals(actual.getPrice(), response.getPrice())
            );
        }

        @Test
        @DisplayName("[실패] 싱픔의 가격은 필수로 입력해야한다.")
        void priceFailTest() {
            var product = new Product();

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1_000L, -10_000L, -1L})
        @DisplayName("[실패] 0원보다 적게 입력하는 경우 등록할 수 없다.")
        void priceFailTest2(final long input) {
            final var product = createProduct(input);

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @Test
        @DisplayName("[실패] 상품 이름을 입력하지 않는 경우 등록할 수 없다.")
        void nameFailTest() {
            final var product = createProductWithoutName();

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @ParameterizedTest
        @ValueSource(strings = {"욕설", "욕설 포함된"})
        @DisplayName("[실패] 상품 이름에 욕설이 포함되어있는 경우 등록할 수 없다.")
        void nameFailTest2(final String name) {
            final var product = createProduct(name);

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        private Product createProductWithoutName() {
            return createProduct(null, 만원);
        }

        private static Product createProduct() {
            return createProduct(상품명, 만원);
        }

        private static Product createProduct(long price) {
            return createProduct(상품명, price);
        }

        private static Product createProduct(String name) {
            return createProduct(name, 만원);
        }

        private static Product createProduct(String name, long price) {
            final var product = new Product();
            product.setId(UUID.randomUUID());
            product.setName(name);
            product.setPrice(BigDecimal.valueOf(price));
            return product;
        }

    }
}
