package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static kitchenpos.MoneyConstants.만원;
import static kitchenpos.MoneyConstants.오천원;
import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.junit.jupiter.api.Assertions.*;
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
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("메뉴 신규 등록")
    class create {
        @Test
        @DisplayName("메뉴를 등록할 수 있다.")
        void success() {
            final var product = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu(오천원, product, menuGroup);
            final var response = createMenu(오천원, product, menuGroup);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
            given(menuRepository.save(any())).willReturn(response);

            Menu actual = menuService.create(menu);

            assertAll(
                    "메뉴 등록정보 그룹 Assertions",
                    () -> assertNotNull(actual.getId()),
                    () -> assertEquals(menu.getId(), actual.getId())
            );
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("금액 정보는 필수로 입력해야한다.")
        void priceFail1(final BigDecimal input) {
            final var product = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu(product, menuGroup);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(menu));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -10_000L})
        @DisplayName("0원보다 적은 금액을 입력하는 경우 메뉴를 등록할 수 없다.")
        void priceFail2(final long input) {
            final var menu = createMenu(input);

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @Test
        @DisplayName("등록되어있는 메뉴 그룹 정보가 필수로 입력해야한다.")
        void menuGroupFail1() {
            final var menu = new Menu();
            menu.setPrice(BigDecimal.valueOf(만원));

            assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("메뉴 이름은 필수로 입력해야한다.")
        void nameFail1(final String input) {
            final var product = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenuWithoutName(input, product, menuGroup);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @ParameterizedTest
        @ValueSource(strings = {"욕설", "욕설포함"})
        @DisplayName("메뉴 이름에 욕설이 포함되어있는 경우 등록할 수 없다.")
        void nameFail2(final String input) {
            final var product = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu(input, 만원, product, menuGroup);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("localParameters")
        @DisplayName("메뉴를 구성하는 상품정보를 필수로 입력해야한다.")
        void productFail(final MenuProduct input) {
            final var menuGroup = createMenuGroup();
            final var menu = new Menu();
            menu.setMenuGroup(menuGroup);
            menu.setPrice(BigDecimal.valueOf(만원));
            try {
                menu.setMenuProducts(List.of(input));
            } catch (NullPointerException e) {
                // null인 경우 List.of exception skip
            }

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }
        static Stream<Arguments> localParameters() {
            return Stream.of(
                    Arguments.of((String) null),
                    Arguments.of(new MenuProduct())
            );
        }

        @Test
        @DisplayName("상품정보는 하나 이상 입력이 가능하다.")
        void menuProduct() {
            final var product1 = createProduct(만원);
            final var product2 = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu("메뉴명", 만원, menuGroup, product1, product2);

            assertEquals(2, menu.getMenuProducts().size());
        }
    }
}
