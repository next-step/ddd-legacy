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

import static kitchenpos.product.fixture.ProductFixture.A_상품;
import static kitchenpos.product.fixture.ProductFixture.가격미존재_상품;
import static kitchenpos.product.fixture.ProductFixture.가격이_마이너스인_상품;
import static kitchenpos.product.fixture.ProductFixture.욕설이름_상품;
import static kitchenpos.product.fixture.ProductFixture.이름미존재_상품;
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
            given(productRepository.save(any())).willReturn(A_상품);

            // when
            var saved = productService.create(A_상품);

            // then
            assertAll(
                    () -> then(productRepository).should(times(1)).save(any()),
                    () -> assertThat(saved.getName()).isEqualTo(A_상품.getName())
            );
        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[실패] 상품의 가격을 입력하지 않으면 등록이 되지 않는다.")
            void 상품_가격_null() {
                // when & then
                assertThatThrownBy(() -> productService.create(가격미존재_상품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 상품의 가격이 0원보다 낮으면 등록이 되지 않는다.")
            void 상품_가격_마이너스() {
                // when & then
                assertThatThrownBy(() -> productService.create(가격이_마이너스인_상품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 이름검증 {

            @Test
            @DisplayName("[실패] 상품의 이름을 입력하지 않으면 등록이 되지 않는다.")
            void 상품_이름_null() {
                // when & then
                assertThatThrownBy(() -> productService.create(이름미존재_상품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 상품의 이름에 부적절한 단어(욕설 등)가 포함되면 등록이 되지 않는다.")
            void 상품_이름_욕설() {
                // given
                given(purgomalumClient.containsProfanity(any())).willReturn(true);

                // when & then
                assertThatThrownBy(() -> productService.create(욕설이름_상품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

    }

    @Nested
    class 가격수정 {

        @Test
        @DisplayName("[성공] 상품의 가격을 수정한다.")
        void changePrice() {

        }

        @Nested
        class 상품등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 상품 아이디인 경우 상품 가격이 수정되지 않는다.")
            void 상품_미등록() {

            }

        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[성공] 메뉴의 가격이 변경된 상품 목록의 가격 합계보다 높으면 메뉴는 숨김 처리된다.")
            void 메뉴_가격_상품_목록의_가격_합계보다_높음() {

            }

        }

    }

}
