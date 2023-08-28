package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.ProductFixture.TEST_PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @Nested
    @DisplayName("새로운 상품을 등록한다.")
    class createTestClass {

        @Test
        @DisplayName("새로운 상품을 정상적으로 등록한다.")
        void createTest() {
            // given
            Product createRequest = TEST_PRODUCT();
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
            given(productRepository.save(any(Product.class))).willReturn(createRequest);

            // when
            Product actual = productService.create(createRequest);

            // then
            verify(productRepository, times(1)).save(any());
            assertThat(actual).isEqualTo(createRequest);
        }

        @Test
        @DisplayName("이름은 비어있을 수 없다.")
        void createNameEmptyTest() {
            // given
            Product createRequest  = TEST_PRODUCT();

            // when
            createRequest.setName(null);

            // then
            assertThatThrownBy(() -> productService.create(createRequest ))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 외설적이거나 욕설이 포함된 영어 이름은 사용 할 수 없다")
        @ParameterizedTest
        @CsvSource(value = {"fuck", "shit"})
        void createNameTest(String name) {
            // given
            Product createRequest = TEST_PRODUCT();
            createRequest.setName(name);
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
            Product productRequest = TEST_PRODUCT();
            UUID productId = productRequest.getId();
            Menu menu = TEST_MENU_BY_PRODUCT(productRequest);
            given(productRepository.findById(productId)).willReturn(Optional.of(productRequest));
            given(menuRepository.findAllByProductId(productId)).willReturn(List.of(menu));

            // when
            productRequest.setPrice(MAX_PRICE);
            Product actual = productService.changePrice(productId, productRequest);

            // then
            assertThat(actual.getPrice()).isEqualTo(productRequest.getPrice());
            assertThat(menu.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("가격 변경 후, 메뉴의 가격이 (메뉴에 포함된 상품들의 가격 x 개수) 총 합보다 높다면 메뉴를 비활성화 한다.")
        void changePriceAndHideTest() {
            // given
            Product productRequest = TEST_PRODUCT();
            UUID productId = productRequest.getId();
            Menu menu = TEST_MENU_BY_PRODUCT(productRequest);
            given(productRepository.findById(productId)).willReturn(Optional.of(productRequest));
            given(menuRepository.findAllByProductId(productId)).willReturn(List.of(menu));

            // when
            productRequest.setPrice(MINIMUM_PRICE);
            Product actual = productService.changePrice(productId, productRequest);

            // then
            assertThat(actual.getPrice()).isEqualTo(productRequest.getPrice());
            assertThat(menu.isDisplayed()).isFalse();
        }
    }
}
