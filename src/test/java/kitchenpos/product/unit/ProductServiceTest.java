package kitchenpos.product.unit;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.product.fixture.ProductFixture.A_제품;
import static kitchenpos.product.fixture.ProductFixture.가격_0원_제품;
import static kitchenpos.product.fixture.ProductFixture.가격미존재_제품;
import static kitchenpos.product.fixture.ProductFixture.가격이_마이너스인_제품;
import static kitchenpos.product.fixture.ProductFixture.욕설이름_제품;
import static kitchenpos.product.fixture.ProductFixture.이름미존재_제품;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        this.productService = new ProductService(
            productRepository, menuRepository, purgomalumClient
        );
    }

    @Nested
    class 등록 {

        @Test
        @DisplayName("[성공] 상품을 등록한다.")
        void create() {
            // given
            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(productRepository.save(any())).willReturn(A_제품);

            // when
            var saved = productService.create(A_제품);

            // then
            assertAll(
                    () -> then(productRepository).should(times(1)).save(any()),
                    () -> assertThat(saved.getName()).isEqualTo(A_제품.getName())
            );
        }

        @Nested
        class 이름검증 {

            @Test
            @DisplayName("[실패] 제품의 이름을 입력하지 않으면 등록이 되지 않는다.")
            void 제품_이름_null() {
                // when & then
                assertThatThrownBy(() -> productService.create(이름미존재_제품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 제품의 이름에 부적절한 단어(욕설 등)가 포함되면 등록이 되지 않는다.")
            void 제품_이름_욕설() {
                // given
                given(purgomalumClient.containsProfanity(any())).willReturn(true);

                // when & then
                assertThatThrownBy(() -> productService.create(욕설이름_제품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[성공] 가격이 0원인 제품을 등록한다.")
            void 제품_가격_0원() {
                // given
                given(purgomalumClient.containsProfanity(any())).willReturn(false);
                given(productRepository.save(any())).willReturn(가격_0원_제품);

                // when
                var saved = productService.create(가격_0원_제품);

                // then
                assertAll(
                        () -> then(productRepository).should(times(1)).save(any()),
                        () -> assertThat(saved.getName()).isEqualTo(가격_0원_제품.getName())
                );
            }

            @Test
            @DisplayName("[실패] 제품의 가격을 입력하지 않으면 등록이 되지 않는다.")
            void 제품_가격_null() {
                // when & then
                assertThatThrownBy(() -> productService.create(가격미존재_제품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 제품의 가격이 0원보다 적으면 등록이 되지 않는다.")
            void 제품_가격_빈문자열() {
                // when & then
                assertThatThrownBy(() -> productService.create(가격이_마이너스인_제품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

    }

    @Nested
    class 가격수정 {

    }

}
