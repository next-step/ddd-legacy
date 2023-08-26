package kitchenpos.application;

import kitchenpos.ApplicationServiceTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductServiceTest extends ApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;


    public static class create_source {
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
        @MethodSource("kitchenpos.application.ProductServiceTest$create_source#source_of_create_success")
        void create_success(String name, BigDecimal price) {
            Product product = ProductFixture.create(name, price);

            productService.create(product);
        }

        @DisplayName("[예외] 상품의 이름은 비속어일 수 없다.")
        @Test
        void create_fail_because_profanity_name() {
            Product product = ProductFixture.create(BigDecimal.valueOf(10000L));
            when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 상품의 이름은 null 일 수 없다")
        @NullSource
        @ParameterizedTest
        void create_fail_because_null_name(String name) {
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


    public static class changePrice_source {

        public static Object[][] changePrice_success() {
            Product product = ProductFixture.create(BigDecimal.valueOf(10000));
            MenuProduct menuProduct = MenuProductFixture.create(product, 3);
            Menu menu = MenuFixture.create("후라이드 치킨", BigDecimal.valueOf(30000), menuProduct);

            return new Object[][]{
                    {"상품의 가격이 20,000원으로 오른 경우", product, menu, BigDecimal.valueOf(20000), true},
                    {"상품의 가격이 10,000원으로 동일한 경우", product, menu, BigDecimal.valueOf(10000), true},
                    {"상품의 가격이 8,000원으로 내려간 경우", product, menu, BigDecimal.valueOf(8000), false},
                    {"상품의 가격이 0원으로 내려간 경우", product, menu, BigDecimal.valueOf(0), false},
            };
        }
        public static Object[][] changePrice_fail_because_illegal_price() {
            return new Object[][]{
                    {"상품의 가격을 -1으로 변경 요청한 경우", ProductFixture.create(BigDecimal.valueOf(-1))},
                    {"상품의 가격을 null로 변경 요청한 경우", ProductFixture.create(null)},
            };
        }

    }

    @DisplayName("가격을 변경합니다.")
    @Nested
    class changePrice {

        @DisplayName("[정상] 상품의 가격을 변경합니다.")
        @MethodSource("kitchenpos.application.ProductServiceTest$changePrice_source#changePrice_success")
        @ParameterizedTest(name = "{0}")
        void changePrice_success(String testName, Product product, Menu menu, BigDecimal changingPrice, boolean isDisplayed) {
            when(productRepository.findById(any())).thenReturn(Optional.of(product));
            when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

            Product changingProduct = ProductFixture.create(changingPrice);

            productService.changePrice(UUID.randomUUID(), changingProduct);

            assertEquals(changingProduct.getPrice(), product.getPrice());
            assertEquals(menu.isDisplayed(), isDisplayed);
        }

        @DisplayName("[예외] 변경되는 상품의 가격은 null이거나 0미만 일 수 없습니다.")
        @MethodSource("kitchenpos.application.ProductServiceTest$changePrice_source#changePrice_fail_because_illegal_price")
        @ParameterizedTest(name = "{0}")
        void changePrice_fail_because_illegal_price(String testName, Product product) {
            assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

}
