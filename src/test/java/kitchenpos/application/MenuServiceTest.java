package kitchenpos.application;

import kitchenpos.application.testFixture.MenuFixture;
import kitchenpos.application.testFixture.MenuGroupFixture;
import kitchenpos.application.testFixture.ProductFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("메뉴(Menu) 서비스 테스트")
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴 생성시")
    @Nested
    class CreateTest {

        @DisplayName("메뉴가 정상 생성된다.")
        @Test
        void createTest() {
            // given
            var id = UUID.randomUUID();
            var menu = MenuFixture.newOne(id);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(MenuGroupFixture.newOne()));
            var product = ProductFixture.newOne();
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(menuRepository.save(any())).willReturn(menu);

            // when
            var actual = menuService.create(menu);

            // then
            assertThat(actual.getName()).isEqualTo("양념치킨");
        }

        static Stream<Arguments> createMenuInvalidPrice() {
            return Stream.of(
                    Arguments.arguments(MenuFixture.newOne(BigDecimal.valueOf(-1000))),
                    Arguments.arguments(MenuFixture.newOne((BigDecimal) null))
            );
        }

        @DisplayName("[예외] 가격은 음수이거나 null일수 없다.")
        @ParameterizedTest
        @MethodSource("createMenuInvalidPrice")
        void invalidPriceExceptionTest(Menu menu) {
            // when & then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 존재하지 않는 메뉴 그룹이면 예외가 발생한다.")
        @Test
        void notFoundMenuGroupExceptionTest() {
            // given
            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.empty());

            // then
            var menu = MenuFixture.newOne();
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 존재하지 않는 상품일 경우 예외가 발생한다.")
        @Test
        void notFoundProductExceptionTest() {
            // given
            var id = UUID.randomUUID();
            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(MenuGroupFixture.newOne()));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(Collections.EMPTY_LIST);

            // when & then
            var product = ProductFixture.newOne(id);
            var menu = MenuFixture.newOne(product);
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴를 구성하는 상품의 총 가격 < 메뉴의 가격 이면 예외가 발생한다.")
        @Test
        void productTotalPriceExceptionTest() {
            // given
            var product = ProductFixture.newOne(5000);
            var menu = MenuFixture.newOne(5001, List.of(product));

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(MenuGroupFixture.newOne()));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when & then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴명은 null일 수 없다")
        @Test
        void menuNameNullExceptionTest() {
            // given
            var menu = MenuFixture.newOne((String) null);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(MenuGroupFixture.newOne()));
            var product = ProductFixture.newOne();
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            // when & then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 메뉴명은 비속어를 포함할 수 없다")
        @Test
        void menuNameProfanityExceptionTest() {
            // given
            var menu = MenuFixture.newOne("비속어를 포함한 메뉴명");

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(MenuGroupFixture.newOne()));
            var product = ProductFixture.newOne();
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            given(purgomalumClient.containsProfanity(any())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("메뉴의 가격 변경시,")
    @Nested
    class ChangeMenuPriceTest {

        static Stream<Arguments> changePriceInvalidPrice() {
            return Stream.of(
                    Arguments.arguments(MenuFixture.newOne(BigDecimal.valueOf(-1000))),
                    Arguments.arguments(MenuFixture.newOne((BigDecimal) null))
            );
        }

        @DisplayName("[예외] 가격은 음수이거나 null일수 없다.")
        @ParameterizedTest
        @MethodSource("changePriceInvalidPrice")
        void invalidPriceExceptionTest(Menu menu) {
            // given
            var id = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> menuService.changePrice(id, menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }
    @Test
    void display() {
    }

    @Test
    void hide() {
    }

    @Test
    void findAll() {
    }
}
