package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private PurgomalumClient purgomalumClient;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("상품 생성")
    class ProductCreation {

        @Test
        @DisplayName("가격이 비어있으면 예외가 발생한다.")
        void throwIfPriceIsNull() {
            // given
            Product request = ProductFixture.상품_생성("치킨", null);

            // when & then
            Assertions.assertThatThrownBy(() -> productService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격이 0원 이상이지 않으면 예외가 발생한다.")
        void throwIfPriceIsNotPositive() {
            // given
            Product request = ProductFixture.상품_생성("치킨", new BigDecimal(-10_000L));

            // when & then
            Assertions.assertThatThrownBy(() -> productService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품명이 비어있으면 예외가 발생한다.")
        void throwIfNameIsEmpty() {
            // given
            Product request = ProductFixture.상품_생성(null, new BigDecimal(10_000L));

            // when & then
            Assertions.assertThatThrownBy(() -> productService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품명에 욕설이 포함되어있으면 예외가 발생한다.")
        void throwIfNameContainsProfanity() {
            // given
            Product request = ProductFixture.상품_생성("ㅅㅂ", new BigDecimal(10_000L));
            given(purgomalumClient.containsProfanity(any())).willReturn(true);

            // when & then
            Assertions.assertThatThrownBy(() -> productService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품을 생성할 수 있다.")
        void createProduct() {
            // given
            Product request = ProductFixture.기본_상품();
            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(productRepository.save(any())).willReturn(new Product());

            // when & then
            productService.create(request);
        }
    }

    @Nested
    @DisplayName("상품 가격 변경")
    class ProductPriceChange {

        @Test
        @DisplayName("가격이 비어있으면 예외가 발생한다.")
        void throwIfPriceIsNull() {
            // given
            Product request = ProductFixture.상품_생성("치킨", null);

            // when & then
            Assertions.assertThatThrownBy(() -> productService.changePrice(request.getId(), request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격이 0원 이상이지 않으면 예외가 발생한다.")
        void throwIfPriceIsNotPositive() {
            // given
            Product request = ProductFixture.상품_생성("치킨", new BigDecimal(-10_000L));

            // when & then
            Assertions.assertThatThrownBy(() -> productService.changePrice(request.getId(), request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 속해있는 상품들의 총합계 가격보다 높을 경우 메뉴를 숨김 처리한다.")
        void hideMenuIfPriceExceedsSum() {
            // given
            Product request = ProductFixture.상품_생성(new BigDecimal(10_000L));
            MenuProduct menuProduct = MenuProductFixture.메뉴_상품_생성(request, 1L);
            Menu menu = MenuFixture.메뉴_생성(BigDecimal.valueOf(20_000L), List.of(menuProduct));

            given(productRepository.findById(any())).willReturn(Optional.of(request));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            productService.changePrice(request.getId(), request);

            // then
            Assertions.assertThat(menu.isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("상품 가격을 변경할 수 있다.")
        void changeProductPrice() {
            // given
            Product request = ProductFixture.상품_생성(new BigDecimal(10_000L));
            MenuProduct menuProduct = MenuProductFixture.메뉴_상품_생성(request, 1L);
            Menu menu = MenuFixture.메뉴_생성(BigDecimal.valueOf(10_000L), List.of(menuProduct));

            given(productRepository.findById(any())).willReturn(Optional.of(request));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when & then
            productService.changePrice(request.getId(), request);
        }
    }
}
