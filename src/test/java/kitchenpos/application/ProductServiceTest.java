package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.ProductFixture.CREATE_TEST_PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    @Mock
    private PurgomalumClient purgomalumClient;

    private ProductService productService;

    @BeforeEach
    void setup() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("새로운 상품을 등록한다.")
    class createTestClass {

        @Test
        @DisplayName("새로운 상품을 정상적으로 등록한다.")
        void createTest() {
            // given
            Product createRequest = CREATE_TEST_PRODUCT();
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when
            Product actual = productService.create(createRequest);

            // then
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getPrice()).isEqualTo(createRequest.getPrice());
            assertThat(actual.getName()).isEqualTo(createRequest.getName());
        }

        @Test
        @DisplayName("이름은 비어있을 수 없다.")
        void createNameEmptyTest() {
            // given
            Product createRequest  = CREATE_TEST_PRODUCT(new BigDecimal(500), null);

            // when && then
            assertThatThrownBy(() -> productService.create(createRequest ))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 외설적이거나 욕설이 포함된 영어 이름은 사용 할 수 없다")
        @ParameterizedTest
        @CsvSource(value = {"fuck", "shit"})
        void createNameTest(String name) {
            // given
            Product createRequest = CREATE_TEST_PRODUCT(name);
            given(purgomalumClient.containsProfanity(name)).willReturn(true);

            // when && then
            assertThatThrownBy(() -> productService.create(createRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상품의 가격을 변경한다")
    class changePriceTestClass {

        @Test
        @DisplayName("상품의 가격을 변경하면서 메뉴가 비활성화 되지 않는다.")
        void changePriceTest() {
            // given
            Product productRequest = CREATE_TEST_PRODUCT();
            Product product = productService.create(productRequest);
            Menu menu = CREATE_TEST_MENU(product);
            menuRepository.save(menu);

            // when
            product.setPrice(MAX_PRICE);
            Product actual = productService.changePrice(product.getId(), product);

            // then
            assertThat(actual.getPrice()).isEqualTo(product.getPrice());
            assertThat(menu.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("가격 변경 후, 메뉴의 가격이 (메뉴에 포함된 상품들의 가격 x 개수) 총 합보다 높다면 메뉴를 비활성화 한다.")
        void changePriceAndHideTest() {
            // given
            Product productRequest = CREATE_TEST_PRODUCT();
            Product product = productService.create(productRequest);
            Menu menu = CREATE_TEST_MENU(product);
            menuRepository.save(menu);

            // when
            product.setPrice(MINIMUM_PRICE);
            Product actual = productService.changePrice(product.getId(), product);

            // then
            assertThat(actual.getPrice()).isEqualTo(product.getPrice());
            assertThat(menu.isDisplayed()).isFalse();
        }
    }
}
