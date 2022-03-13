package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuFixture;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProductFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.domain.MenuFixture.CHICKEN_MENU;
import static kitchenpos.domain.MenuGroupFixture.CHICKEN_MENU_GROUP;
import static kitchenpos.domain.MenuProductFixture.*;
import static kitchenpos.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService sut;

    @ParameterizedTest
    @MethodSource("invalidPriceArguments")
    @NullSource
    @DisplayName("메뉴 생성 시 가격이 비어있거나 0 미만이면 예외 발생")
    void throwExceptionIfInvalidPrice(BigDecimal price) {
        // given
        Menu request = new Menu();
        request.setPrice(price);
        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(request));
    }

    static Stream<Arguments> invalidPriceArguments() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(-1)),
            Arguments.of(BigDecimal.valueOf(-5000))
        );
    }

    @Test
    @DisplayName("메뉴 생성 시 올바른 메뉴 그룹을 입력하지 않았으면 예외 발생")
    void throwExceptionIfInvalidMenuGroup() {
        // given
        Menu request = MenuFixture.builder()
                                  .price(BigDecimal.valueOf(5000L))
                                  .menuGroupId(UUID.randomUUID())
                                  .build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 시 포함 상품을 입력하지 않으면 오류")
    void throwExceptionIfEmptyMenuProducts() {
        // given
        Menu request = MenuFixture.builder()
                                  .price(BigDecimal.valueOf(5000L))
                                  .menuGroupId(CHICKEN_MENU_GROUP.getId())
                                  .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 시 입력했던 상품이 미리 등록되지 않았다면 예외 발생")
    void throwExceptionIfNotAddedMenuProducts() {
        // given
        Menu request =
            MenuFixture.builder()
                       .price(BigDecimal.valueOf(5000L))
                       .menuGroupId(CHICKEN_MENU_GROUP.getId())
                       .menuProducts(Collections.singletonList(
                           MenuProductFixture.builder()
                                             .productId(UUID.randomUUID())
                                             .build()))
                       .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        given(productRepository.findAllByIdIn(any())).willReturn(Collections.emptyList());

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 시 입력했던 상품 중 개수가 0개 미만인 상품이 있으면 예외 발생")
    void throwExceptionIfZeroQuantity() {
        // given
        UUID productId = UUID.randomUUID();

        Menu request =
            MenuFixture.builder()
                       .price(BigDecimal.valueOf(5000L))
                       .menuGroupId(CHICKEN_MENU_GROUP.getId())
                       .menuProducts(Collections.singletonList(
                           MenuProductFixture.builder()
                                             .productId(productId)
                                             .quantity(-1)
                                             .build()))
                       .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        given(productRepository.findAllByIdIn(any()))
            .willReturn(Collections.singletonList(new Product()));

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 시 입력한 가격이 입력한 상품했던 상품의 합보다 작지 않으면 예외 발생")
    void throwExceptionIfPriceBiggerThenSum() {
        // given
        UUID productId = UUID.randomUUID();

        Menu request =
            MenuFixture.builder()
                       .price(BigDecimal.valueOf(5000L))
                       .menuGroupId(CHICKEN_MENU_GROUP.getId())
                       .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                       .price(BigDecimal.valueOf(38001L)) // 프라이드치킨과 허니콤보와 가격 합은 38000
                       .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        given(productRepository.findAllByIdIn(any()))
            .willReturn(Arrays.asList(FRIED_CHICKEN, HONEY_COMBO));

        given(productRepository.findById(MP_FRIED_CHICKEN.getProductId()))
            .willReturn(Optional.of(FRIED_CHICKEN));

        given(productRepository.findById(MP_HONEY_COMBO.getProductId()))
            .willReturn(Optional.of(HONEY_COMBO));

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 시 이름이 null이면 예외 발생")
    void throwExceptionIfEmptyName() {
        // given
        UUID productId = UUID.randomUUID();

        Menu request =
            MenuFixture.builder()
                       .price(BigDecimal.valueOf(5000L))
                       .menuGroupId(CHICKEN_MENU_GROUP.getId())
                       .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                       .price(BigDecimal.valueOf(20000L))
                       .name(null)
                       .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        given(productRepository.findAllByIdIn(any()))
            .willReturn(Arrays.asList(FRIED_CHICKEN, HONEY_COMBO));

        given(productRepository.findById(MP_FRIED_CHICKEN.getProductId()))
            .willReturn(Optional.of(FRIED_CHICKEN));

        given(productRepository.findById(MP_HONEY_COMBO.getProductId()))
            .willReturn(Optional.of(HONEY_COMBO));

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 시 이름에 욕설이 포함되면 예외 발생")
    void throwExceptionIfNameContainsProfanity() {
        // given
        UUID productId = UUID.randomUUID();

        Menu request =
            MenuFixture.builder()
                       .price(BigDecimal.valueOf(5000L))
                       .menuGroupId(CHICKEN_MENU_GROUP.getId())
                       .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                       .price(BigDecimal.valueOf(20000L))
                       .name("profanity")
                       .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        given(productRepository.findAllByIdIn(any()))
            .willReturn(Arrays.asList(FRIED_CHICKEN, HONEY_COMBO));

        given(productRepository.findById(MP_FRIED_CHICKEN.getProductId()))
            .willReturn(Optional.of(FRIED_CHICKEN));

        given(productRepository.findById(MP_HONEY_COMBO.getProductId()))
            .willReturn(Optional.of(HONEY_COMBO));

        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(request));
    }

    @Test
    @DisplayName("메뉴 생성 성공")
    void successCreateMenu() {
        // given
        UUID productId = UUID.randomUUID();

        Menu request =
            MenuFixture.builder()
                       .price(BigDecimal.valueOf(5000L))
                       .menuGroupId(CHICKEN_MENU_GROUP.getId())
                       .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                       .price(BigDecimal.valueOf(20000L))
                       .name("치킨 메뉴")
                       .build();

        given(menuGroupRepository.findById(request.getMenuGroupId()))
            .willReturn(Optional.of(CHICKEN_MENU_GROUP));

        given(productRepository.findAllByIdIn(any()))
            .willReturn(Arrays.asList(FRIED_CHICKEN, HONEY_COMBO));

        given(productRepository.findById(MP_FRIED_CHICKEN.getProductId()))
            .willReturn(Optional.of(FRIED_CHICKEN));

        given(productRepository.findById(MP_HONEY_COMBO.getProductId()))
            .willReturn(Optional.of(HONEY_COMBO));

        given(purgomalumClient.containsProfanity(any())).willReturn(false);

        // when
        assertThatCode(() -> sut.create(request)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("invalidPriceArguments")
    @DisplayName("가격 변경 시 입력한 가격이 null이거나 음수면 예외 발생")
    void throwExceptionChangePriceIfInvalidPrice(BigDecimal price) {
        // given
        Menu request = MenuFixture.builder()
                                  .price(price)
                                  .build();

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.changePrice(CHICKEN_MENU.getId(), request));
    }

    @Test
    @DisplayName("가격 변경 시 입력한 가격이 상품 가격의 합보다 크면 예외 발생")
    void throwExceptionChangePriceIfTooBigPrice() {
        // given
        Menu request = MenuFixture.builder()
                                  .price(BigDecimal.valueOf(38001L))
                                  .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                                  .build();

        given(menuRepository.findById(any())).willReturn(Optional.of(CHICKEN_MENU));

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.changePrice(CHICKEN_MENU.getId(), request));
    }

    @Test
    @DisplayName("가격 변경 성공")
    void successChangePrice() {
        // given
        Menu request = MenuFixture.builder()
                                  .price(BigDecimal.valueOf(38000L))
                                  .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                                  .build();

        given(menuRepository.findById(any())).willReturn(Optional.of(CHICKEN_MENU));

        // when
        assertThatCode(() -> sut.changePrice(CHICKEN_MENU.getId(), request))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("메뉴를 판매상태로 변경 시 가격이 부적절한 상태면 예외 발생")
    void failDisplayCase01() {
        // given
        Menu menu = MenuFixture.builder()
                               .menuProducts(Arrays.asList(MP_FRIED_CHICKEN, MP_HONEY_COMBO))
                               .price(BigDecimal.valueOf(50000L))
                               .build();

        given(menuRepository.findById(any()))
            .willReturn(Optional.of(menu));

        // when
        assertThatIllegalStateException()
            .isThrownBy(() -> sut.display(CHICKEN_MENU.getId()));
    }

    @Test
    @DisplayName("메뉴를 판매상태로 변경 성공")
    void successDisplay() {
        // given
        given(menuRepository.findById(CHICKEN_MENU.getId()))
            .willReturn(Optional.of(CHICKEN_MENU));

        // when
        sut.display(CHICKEN_MENU.getId());
    }

    @Test
    @DisplayName("메뉴를 비매상태로 변경 성공")
    void successHide() {
        // given
        given(menuRepository.findById(CHICKEN_MENU.getId()))
            .willReturn(Optional.of(CHICKEN_MENU));

        // when
        sut.hide(CHICKEN_MENU.getId());
    }
}
