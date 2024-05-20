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

import static kitchenpos.MoneyConstants.*;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuFixture.createMenuWithoutName;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
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
                    () -> assertEquals(response.getId(), actual.getId())
            );
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("금액 정보는 필수로 입력해야한다.")
        void priceFail1(final Long input) {
            final var product = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu(input, product, menuGroup);

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
            final var menu = createMenu(input, 만원, menuGroup, product);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
            given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @ParameterizedTest(name = "[{index}]: {arguments}")
        @MethodSource("localParameters")
        @DisplayName("메뉴를 구성하는 상품정보를 필수로 입력해야한다.")
        void productFail(final List<MenuProduct> input) {
            final var menuGroup = createMenuGroup();
            final var menu = new Menu();
            menu.setMenuGroup(menuGroup);
            menu.setPrice(BigDecimal.valueOf(만원));
            menu.setMenuProducts(input);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }
        static Stream<Arguments> localParameters() {
            return Stream.of(
                    Arguments.of((String) null),
                    Arguments.of(List.of(new MenuProduct()))
            );
        }

        @Test
        @DisplayName("상품정보는 하나 이상 입력이 가능하다.")
        void menuProductFail1() {
            final var product1 = createProduct(만원);
            final var product2 = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu("메뉴명", 만원, menuGroup, product1, product2);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product1, product2));
            given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
            given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));
            given(menuRepository.save(any())).willReturn(menu);

            Menu actual = menuService.create(menu);

            assertEquals(2, actual.getMenuProducts().size());
        }

        @Test
        @DisplayName("등록되어있지 않은 상품정보를 입력하는 경우 메뉴를 등록할 수 없다.")
        void menuProductFail2() {
            final var product1 = createProduct(만원);
            final var product2 = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu("메뉴명", 만원, menuGroup, product1);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product2));

            assertThrows(NoSuchElementException.class, () -> menuService.create(menu));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -100L})
        @DisplayName("상품정보의 수량이 0개보다 작은 경우 메뉴를 등록할 수 없다.")
        void menuProductFail3(final long input) {
            final var product = createProduct(만원);
            final var menuGroup = createMenuGroup();
            final var menuProduct = createMenuProduct(product, input);
            final var menu = createMenu("메뉴명", 만원, menuGroup, List.of(menuProduct));

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }

        @Test
        @DisplayName("상품정보의 총 합계 금액보다 메뉴의 가격이 비싼 경우 등록할 수 없다.")
        void menuProductFail4() {
            final var product1 = createProduct(오천원);
            final var product2 = createProduct(오천원);
            final var menuGroup = createMenuGroup();
            final var menu = createMenu("메뉴명", 이만원, menuGroup, product1, product2);

            given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any())).willReturn(List.of(product1, product2));
            given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
            given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));

            assertThrows(IllegalArgumentException.class, () -> menuService.create(menu));
        }
    }

    @Nested
    @DisplayName("메뉴 금액변경")
    class changeMenuPrice {
        @Test
        @DisplayName("메뉴는 금액을 변경할 수 있다.")
        void success() {
            final var menu = createMenu(오천원);
            menu.setPrice(BigDecimal.valueOf(만원));

            given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

            Menu actual = menuService.changePrice(menu.getId(), menu);
            assertEquals(BigDecimal.valueOf(만원), actual.getPrice());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("변경할 금액 정보가 없는 경우 변경이 불가하다.")
        void priceFail1(final BigDecimal input) {
            final var menu = createMenu(오천원);
            menu.setPrice(input);

            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -10_000L})
        @DisplayName("0원보다 작은 금액을 입력하는 경우 변경이 불가하다.")
        void priceFail2(final long input) {
            final var menu = createMenu(오천원);
            menu.setPrice(BigDecimal.valueOf(input));

            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        @Test
        @DisplayName("변경하려고 하는 메뉴 정보가 없는 경우 변경이 불가하다.")
        void notFound() {
            final var menu = createMenu(오천원);

            assertThrows(NoSuchElementException.class, () -> menuService.changePrice(menu.getId(), menu));
        }

        @Test
        @DisplayName("변경금액이 메뉴 상품의 총 합계 금액보다 비싼 경우 변경할 수 없다.")
        void priceFail3() {
            final var product = createProduct(오천원);
            final var menu = createMenu(오천원, product);
            menu.setPrice(BigDecimal.valueOf(만원));

            given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

            assertThrows(IllegalArgumentException.class, () -> menuService.changePrice(menu.getId(), menu));
        }
    }

    @Nested
    @DisplayName("메뉴 노출여부 변경")
    class changingDisplay {
        @Test
        @DisplayName("메뉴는 노출되도록 변경할 수 있다.")
        void success() {
            final var product = createProduct(만원);
            final var menu = createMenu(만원, product);
            menu.setDisplayed(false);

            given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

            Menu display = menuService.display(menu.getId());

            assertTrue(display.isDisplayed());
        }

        @Test
        @DisplayName("메뉴는 숨길 수 있다.")
        void success2() {
            final var menu = createMenu(만원);

            given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

            assertTrue(menu.isDisplayed());
            Menu display = menuService.hide(menu.getId());

            assertFalse(display.isDisplayed());
        }

        @Test
        @DisplayName("등록된적 없는 메뉴는 노출되도록 변경할 수 없다.")
        void fail1() {
            final var menu = createMenu(만원);

            assertThrows(NoSuchElementException.class, () -> menuService.display(menu.getId()));
        }

        @Test
        @DisplayName("등록된적 없는 메뉴는 노출되도록 변경할 수 없다.")
        void fail2() {
            final var menu = createMenu(만원);

            assertThrows(NoSuchElementException.class, () -> menuService.hide(menu.getId()));
        }

        @Test
        @DisplayName("노출하려는 메뉴의 금액이 상품정보의 총 합계 금액보다 높은 경우 변경할 수 없다.")
        void fail3() {
            final var product = createProduct(오천원);
            final var menu = createMenu(만원, product);

            given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

            assertThrows(IllegalStateException.class, () -> menuService.display(menu.getId()));
        }
    }
}
