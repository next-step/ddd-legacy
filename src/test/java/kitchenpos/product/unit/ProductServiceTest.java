package kitchenpos.product.unit;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.support.util.assertion.AssertUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.menu.fixture.MenuFixture.봉골레_파스타_세트_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.토마토_파스타_단품_메뉴;
import static kitchenpos.product.fixture.ProductFixture.가격미존재_제품;
import static kitchenpos.product.fixture.ProductFixture.가격이_마이너스인_제품;
import static kitchenpos.product.fixture.ProductFixture.김치찜;
import static kitchenpos.product.fixture.ProductFixture.수제_마늘빵;
import static kitchenpos.product.fixture.ProductFixture.욕설이름_제품;
import static kitchenpos.product.fixture.ProductFixture.이름미존재_제품;
import static kitchenpos.product.fixture.ProductFixture.토마토_파스타;
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
        @DisplayName("[성공] 제품을 등록한다.")
        void create() {
            // given
            given(purgomalumClient.containsProfanity(any())).willReturn(false);

            given(productRepository.save(any())).willReturn(김치찜);

            // when
            var saved = productService.create(김치찜);

            // then
            assertAll(
                    () -> then(productRepository).should(times(1)).save(any()),
                    () -> assertThat(saved.getName()).isEqualTo(김치찜.getName())
            );
        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[실패] 제품의 가격을 입력하지 않으면 등록이 되지 않는다.")
            void 제품_가격_null() {
                // when & then
                assertThatThrownBy(() -> productService.create(가격미존재_제품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 제품의 가격이 0원보다 낮으면 등록이 되지 않는다.")
            void 제품_가격_마이너스() {
                // when & then
                assertThatThrownBy(() -> productService.create(가격이_마이너스인_제품))
                        .isInstanceOf(IllegalArgumentException.class);
            }

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

    }

    @Nested
    class 가격수정 {

        @Test
        @DisplayName("[성공] 제품의 가격을 수정한다.")
        void changePrice() {
            // given
            given(productRepository.findById(any())).willReturn(Optional.of(수제_마늘빵));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(봉골레_파스타_세트_메뉴));

            // when
            var 수정_가격 = new BigDecimal(1_500);

            var 수정할_내용 = new Product();
            수정할_내용.setPrice(수정_가격);

            var updated = productService.changePrice(UUID.randomUUID(), 수정할_내용);

            // then
            AssertUtils.가격이_동등한가(updated.getPrice(), 수정_가격);
        }

        @Nested
        class 제품등록여부검증 {

            @Test
            @DisplayName("[실패] 등록되지않은 제품 아이디인 경우 제품 가격이 수정되지 않는다.")
            void 제품_미등록() {
                // given
                given(productRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                var 수정할_내용 = new Product();
                수정할_내용.setPrice(new BigDecimal(5_000));

                assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), 수정할_내용))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        @Nested
        class 가격검증 {

            @Test
            @DisplayName("[성공] 메뉴의 가격이 변경된 제품 목록의 가격 합계보다 높으면 메뉴는 숨김 처리된다.")
            void 메뉴_가격_제품_목록의_가격_합계보다_높음() {
                // given
                given(productRepository.findById(any())).willReturn(Optional.of(토마토_파스타));
                given(menuRepository.findAllByProductId(any())).willReturn(List.of(토마토_파스타_단품_메뉴));

                var 수정_가격 = new BigDecimal(10_500);

                var 수정할_내용 = new Product();
                수정할_내용.setPrice(수정_가격);

                var updated = productService.changePrice(UUID.randomUUID(), 수정할_내용);

                assertAll(
                        () -> AssertUtils.가격이_동등한가(updated.getPrice(), 수정_가격),
                        () -> assertThat(토마토_파스타_단품_메뉴.isDisplayed()).isFalse()
                );
            }

        }

    }

}
