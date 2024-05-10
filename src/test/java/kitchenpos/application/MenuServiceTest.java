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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @Nested
    @DisplayName("메뉴 생성")
    class MenuCreation {
        @Test
        @DisplayName("가격이 비어있으면 예외가 발생한다.")
        void shouldThrowExceptionIfPriceIsNull() {
            // given
            BigDecimal price = null;
            Menu request = MenuFixture.메뉴_생성(price);

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격이 0원 미만이면 예외가 발생한다.")
        void shouldThrowExceptionIfPriceIsNegative() {
            // given
            BigDecimal price = new BigDecimal(-10_000L);
            Menu request = MenuFixture.메뉴_생성(price);

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴가 속한 메뉴 그룹이 존재하지 않으면 예외가 발생한다.")
        void shouldThrowExceptionIfMenuGroupIsMissing() {
            // given
            Menu request = MenuFixture.그룹_없는_메뉴();

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest(name = ("메뉴에 속하는 상품이 하나라도 존재하지 않으면 예외가 발생한다. : menuProducts = {0}"))
        @NullAndEmptySource
        void shouldThrowExceptionIfMenuHasNoProducts(List<MenuProduct> menuProducts) {
            // given
            Menu request = MenuFixture.메뉴_생성(menuProducts);
            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("해당하는 상품이 존재하지 않으면 예외가 발생한다.")
        void shouldThrowExceptionIfAnyProductDoesNotExist() {
            // given
            Menu request = MenuFixture.기본_메뉴();
            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of());

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴에 속한 상품의 갯수가 0개 미만일 경우 생성할 수 없다.")
        void shouldThrowExceptionIfAnyMenuProductHasNegativeQuantity() {
            // given
            Product product = ProductFixture.기본_상품();
            MenuProduct 수량이_0_미만인_메뉴_상품 = MenuProductFixture.메뉴_상품_생성(product, -1L);
            Menu request = MenuFixture.메뉴_생성(List.of(수량이_0_미만인_메뉴_상품));

            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 상품의 가격의 합보다 크면 예외가 발생한다.")
        void shouldThrowExceptionIfMenuPriceIsGreaterThanSumOfProductPrices() {
            // given
            Product 만원짜리_상품 = ProductFixture.기본_상품();
            MenuProduct 메뉴_상품 = MenuProductFixture.메뉴_상품_생성(만원짜리_상품, 1L);
            Menu request = MenuFixture.메뉴_생성(BigDecimal.valueOf(20_000L), List.of(메뉴_상품));

            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(만원짜리_상품));
            given(productRepository.findById(만원짜리_상품.getId())).willReturn(Optional.of(만원짜리_상품));

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);

        }

        @Test
        @DisplayName("메뉴의 이름이 비어있으면 예외가 발생한다.")
        void shouldThrowExceptionIfMenuNameIsEmpty() {
            String name = null;
            Menu request = MenuFixture.메뉴_생성(name);
            Product product = ProductFixture.기본_상품();

            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 이름에 욕설이 포함되어 있으면 예외가 발생한다.")
        void shouldThrowExceptionIfMenuNameContainsProfanity() {
            // given
            Menu request = MenuFixture.기본_메뉴();
            Product product = ProductFixture.기본_상품();

            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(any())).willReturn(true);

            // when & then
            Assertions.assertThatThrownBy(() -> menuService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴를 생성할 수 있다.")
        void shouldSuccessfullyCreateMenu() {
            // given
            Menu request = MenuFixture.기본_메뉴();
            Product product = ProductFixture.기본_상품();

            given(menuGroupRepository.findById(request.getMenuGroupId())).willReturn(Optional.of(request.getMenuGroup()));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(any())).willReturn(false);

            // when & then
            menuService.create(request);
        }
    }

    @Nested
    @DisplayName("가격 변경")
    class ChangePrice {


    }

    @Nested
    @DisplayName("메뉴 노출 상태 변경")
    class ChangeDisplay {
    }

}