package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;


    static class create_source {
        public static Object[][] source_of_create_success() {
            return new Object[][]{
                    {"후라이드 치킨", BigDecimal.valueOf(18_000)},
                    {"양념 치킨", BigDecimal.valueOf(18_000)},
                    {"맛초킹", BigDecimal.valueOf(23_000)},
                    {"무료 감자튀김", ZERO},
            };
        }
    }

    @DisplayName("상품을 등록합니다.")
    @Nested
    class create {

        @DisplayName("[정상] 상품이 정상적으로 등록됩니다.")
        @ParameterizedTest
        @MethodSource("kitchenpos.application.ProductServiceTest#source_of_create_success")
        void create_success(String name, BigDecimal price) {
            Product product = ProductFixture.create(name, price);

            productService.create(product);
        }

        @DisplayName("[예외] 상품의 이름은 비속어일 수 없다.")
        @Test
        void create_fail_due_to_profanity_name() {
            Product product = ProductFixture.create(BigDecimal.valueOf(10000L));
            when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 상품의 이름은 null 일 수 없다")
        @NullSource
        @ParameterizedTest
        void create_fail_due_to_null_name(String name) {
            Product product = ProductFixture.create(name, BigDecimal.valueOf(10000L));

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 상품의 가격은 0원 이하 일 수 없다")
        @ValueSource(ints = {-1, -100})
        @ParameterizedTest
        void create_fail_dueTo_a_price_below_zero(int price) {
            BigDecimal parsedPrice = BigDecimal.valueOf(price);
            Product product = ProductFixture.create(parsedPrice);

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

}