package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static kitchenpos.fixture.application.MenuFixture.*;
import static kitchenpos.fixture.application.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.application.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.application.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    private Product 후라이드_치킨_상품;
    private Product 강정_치킨_상품;
    private MenuProduct 후라이드_치킨_메뉴상품;
    private MenuProduct 강정_치킨_메뉴상품;
    private MenuGroup 커플메뉴_메뉴그룹;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        커플메뉴_메뉴그룹 = createMenuGroup("커플 메뉴");
        후라이드_치킨_상품 = createProduct("후라이드 치킨", BigDecimal.valueOf(12000));
        강정_치킨_상품 = createProduct("강정 치킨", BigDecimal.valueOf(15000));
        후라이드_치킨_메뉴상품 = createMenuProduct(후라이드_치킨_상품, 1);
        강정_치킨_메뉴상품 = createMenuProduct(강정_치킨_상품, 1);
    }

    @Nested
    @DisplayName("등록")
    class MenuCreate {
        private static Stream<BigDecimal> lessThanZero() {
            return Stream.of(
                    BigDecimal.valueOf(-10),
                    BigDecimal.valueOf(-10000)
            );
        }

        private static Stream<List<MenuProduct>> menuProductIsNullAndEmpty() {
            return Stream.of(
                    null,
                    Collections.emptyList()
            );
        }

        @Test
        @DisplayName("메뉴를 등록한다")
        void menuCreate() {
            Menu request = 커플_강정_후라이드_메뉴(커플메뉴_메뉴그룹, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);
            stubMenu();
            when(menuRepository.save(any())).thenReturn(request);

            Menu result = menuService.create(request);

            assertAll(() -> {
                assertThat(result.getName()).isEqualTo(request.getName());
                assertThat(result.getPrice()).isEqualTo(request.getPrice());
                assertThat(result.getMenuProducts()).containsExactly(후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);
                assertThat(result.isDisplayed()).isTrue();
                assertThat(result.getMenuGroup()).isEqualTo(커플메뉴_메뉴그룹);
                assertThat(result.getMenuGroupId()).isEqualTo(커플메뉴_메뉴그룹.getId());
            });
        }

        @ParameterizedTest
        @NullSource
        @MethodSource("lessThanZero")
        @DisplayName("메뉴의 가격이 null 이거나 0보다 작으면 IllegalArgumentException을 반환한다.")
        void menuPriceIsNullOrLessThanZero(BigDecimal price) {
            Menu request = createMenu("커플 강정 + 후라이드", 커플메뉴_메뉴그룹, price, true, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴는 하나 이상의 메뉴 그룹에 속해 있지 않으면 NoSuchElementException을 반환한다.")
        void menuBelongToMenuGroup() {
            Menu request = withoutMenuGroup("커플 강정 + 후라이드", true, BigDecimal.valueOf(12000), 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);

            assertThat(request.getMenuGroup()).isNull();
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest
        @DisplayName("메뉴 상품이 없거나 null이라면 IllegalArgumentException을 반환한다.")
        @MethodSource("menuProductIsNullAndEmpty")
        void menuProductIsNullOrEmpty(List<MenuProduct> menuProducts) {
            Menu request = createMenu("커플 강정 + 후라이드", 커플메뉴_메뉴그룹, menuProducts, BigDecimal.valueOf(12000));
            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(커플메뉴_메뉴그룹));

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품이 상품으로 등록되어 있지 않으면 IllegalArgumentException.class를 반환한다.")
        void menuProductIsAlreadyRegisterProduct() {
            // given
            Menu request = 커플_강정_후라이드_메뉴(커플메뉴_메뉴그룹, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);
            stubMenuGroupAndProduct();

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(longs = {-1, -10})
        @DisplayName("메뉴에 등록하려하는 메뉴 상품의 수량은 음수가 될 수 없다.")
        void menuProductQuantityIsNotLessThanZero(long quantity) {
            // given
            Menu request = createMenu("커플 강정 + 후라이드", 커플메뉴_메뉴그룹, BigDecimal.valueOf(20000), true, createMenuProduct(후라이드_치킨_상품, quantity));
            stubMenuGroupAndProduct();

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격은 메뉴 상품의 가격의 합보다 클 수 없다.")
        void menuPriceIsGreaterThanMenuProductQuantityPrice() {
            Menu request = createMenu("커플 강정 + 후라이드", 커플메뉴_메뉴그룹, BigDecimal.valueOf(30000), true, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);
            stubMenu();

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("메뉴의 이름은 null이 될 수 없다.")
        void menuNameCannotBeNull(String name) {
            Menu request = createMenu(name, 커플메뉴_메뉴그룹, BigDecimal.valueOf(20000), true, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);
            stubMenu();

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"비속어1", "비속어2"})
        @DisplayName("메뉴의 이름은 비속어가 될 수 없다.")
        void menuNameCannotProfanity(String name) {
            Menu request = createMenu(name, 커플메뉴_메뉴그룹, BigDecimal.valueOf(20000), true, 후라이드_치킨_메뉴상품, 강정_치킨_메뉴상품);
            stubMenu();

            when(purgomalumClient.containsProfanity(name)).thenReturn(true);

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    private void stubMenuGroupAndProduct() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(후라이드_치킨_상품));
    }

    private void stubMenu() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(커플메뉴_메뉴그룹));
        when(productRepository.findById(후라이드_치킨_상품.getId())).thenReturn(Optional.of(후라이드_치킨_상품));
        when(productRepository.findById(강정_치킨_상품.getId())).thenReturn(Optional.of(강정_치킨_상품));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(후라이드_치킨_상품, 강정_치킨_상품));
    }
}
