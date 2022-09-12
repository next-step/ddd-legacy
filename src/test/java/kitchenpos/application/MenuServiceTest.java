package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    public static final UUID COFFEE_GROUP_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID AMERICANO_PRODUCT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID LATTE_PRODUCT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    @Mock
    private MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final ProfanityChecker profanityChecker = new TestProfanityChecker();

    private MenuService testService;

    @BeforeEach
    void setUp() {
        persistMenuGroup();
        persistProducts();

        this.testService = new MenuService(
                menuRepository,
                menuGroupRepository,
                productRepository,
                profanityChecker
        );
    }

    private void persistMenuGroup() {
        menuGroupRepository.save(MenuTestFixture.coffeeMenuGroup());
    }

    private void persistProducts() {
        productRepository.save(MenuTestFixture.americano2000Won());
        productRepository.save(MenuTestFixture.latte3000Won());
    }

    @Nested
    @DisplayName("메뉴 등록")
    class Create {
        @DisplayName("가격은 음수가 아니어야 한다.")
        @Test
        void negativePrice() {
            // given
            final var request = MenuTestFixture.requestOfPrice(-500);

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록된 메뉴그룹을 선택해야 한다.")
        @Test
        void menuGroupNotFound() {
            final var nonExistingMenuGroupId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            final var request = MenuTestFixture.requestOfMenuGroup(nonExistingMenuGroupId);

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("상품을 하나 이상 가지고 있어야 한다.")
        @Test
        void nullOrEmptyMenuProducts() {
            final var request = MenuTestFixture.requestEmptyProduct();

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록하려는 메뉴의 상품라스트에 중복된 상품이 없어야 한다.")
        @Test
        void duplicatedProducts() {
            final var request = MenuTestFixture.requestOfProductWithQuantity(
                    AMERICANO_PRODUCT_ID,
                    1,
                    AMERICANO_PRODUCT_ID,
                    1
            );

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록하려는 메뉴의 상품 개수는 음수가 아니어야 한다.")
        @Test
        void negativeProductQuantity() {
            final var request = MenuTestFixture.requestOfProductWithQuantity(AMERICANO_PRODUCT_ID, -1);

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록하려는 메뉴의 상품이 존재해야 한다.")
        @Test
        void productNotFound() {
            final var nonExistingProductId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            final var request = MenuTestFixture.requestOfProductWithQuantity(nonExistingProductId, 1);

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 가격은 메뉴에 속한 상품 가격의 합보다 작거나 같아야 한다.")
        @Test
        void menuPriceShouldLessThanOrEqualToProductSumPrice() {
            final var request = MenuTestFixture.requestOfPrice(10000);

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 비어 있지 않아야 한다.")
        @ParameterizedTest(name = "메뉴 이름이 [{0}]이 아니어야 한다.")
        @NullSource
        void nullName(String name) {
            final var request = MenuTestFixture.requestOfName(name);

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 비속어가 포함되지 않아야 한다.")
        @Test
        void nameContainsProfanity() {
            final var request = MenuTestFixture.requestOfName("나쁜말");

            assertThatThrownBy(() -> testService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴를 등록할 수 있다.")
        @Test
        void create() {
            final var request = MenuTestFixture.request(
                    "아메리카노",
                    2000,
                    COFFEE_GROUP_ID,
                    AMERICANO_PRODUCT_ID,
                    1
            );
            given(menuRepository.save(any())).willAnswer((invocation -> invocation.getArgument(0)));

            final var result = testService.create(request);

            assertAll(
                    () -> assertThat(result.getId()).as("메뉴아이디").isNotNull(),
                    () -> assertThat(result.getPrice()).as("메뉴가격").isEqualTo(new BigDecimal(2000)),
                    () -> assertThat(result.getName()).as("메뉴이름").isEqualTo("아메리카노"),
                    () -> assertThat(result.getMenuGroup().getId()).as("커피").isEqualTo(COFFEE_GROUP_ID),
                    () -> assertThat(result.getMenuProducts()).as("상품목록")
                            .extracting((menuProduct1 -> menuProduct1.getProduct().getId()))
                            .containsExactly(AMERICANO_PRODUCT_ID)
            );
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangePrice {
        @DisplayName("변경하려는 가격은 비어 있지 않아야 한다.")
        @ParameterizedTest(name = "변경하려는 가격이 [{0}]이 아니어야 한다.")
        @NullSource
        void nullPrice(BigDecimal targetPrice) {
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = MenuTestFixture.requestOfPrice(targetPrice);

            assertThatThrownBy(() -> testService.changePrice(menuId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경하려는 메뉴 가격은 음수가 아니어야 한다.")
        @Test
        void negativePrice() {
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = MenuTestFixture.requestOfPrice(-100);

            assertThatThrownBy(() -> testService.changePrice(menuId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("등록된 메뉴여야 한다.")
        @Test
        void menuNotFound() {
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = MenuTestFixture.requestOfPrice(1000);

            given(menuRepository.findById(menuId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> testService.changePrice(menuId, request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("변경하려는 메뉴 가격은 메뉴에 포함된 품목별 가격의 총합보다 작거나 같아야 한다.")
        @Test
        void menuPriceMustLessThanOrEqualToMenuProductSumPrice() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var request = MenuTestFixture.requestOfPrice(2_500);

            given(menuRepository.findById(menuId)).willReturn(Optional.of(MenuTestFixture.coffeeSetMenu(2_000)));

            // when
            assertThatThrownBy(() -> testService.changePrice(menuId, request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 가격을 변경할 수 있다.")
        @Test
        void changePrice() {
            // given
            final var menuId = MenuTestFixture.COFFEE_SET_MENU_ID;
            final var request = MenuTestFixture.requestOfPrice(1_000);
            final var expectedPrice = new BigDecimal(1_000);

            given(menuRepository.findById(menuId)).willReturn(Optional.of(MenuTestFixture.coffeeSetMenu(2_000)));

            // when
            final var actualPrice = testService.changePrice(menuId, request).getPrice();

            // then
            assertThat(actualPrice).isEqualTo(expectedPrice);
        }

    }

    @DisplayName("메뉴 공개")
    @Nested
    class Display {
        @DisplayName("등록된 메뉴여야 한다.")
        @Test
        void menuNotFound() {
            final var nonExistingMenuId = UUID.fromString("00000000-0000-0000-0000-000000000000");

            given(menuRepository.findById(nonExistingMenuId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> testService.display(nonExistingMenuId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("공개하려는 메뉴의 가격이 메뉴에 포함된 상품 가격의 총합보다 크지 않아야 한다.")
        @Test
        void menuPriceShouldLessThanOrEqualToProductSumPrice() {
            final var menuId = MenuTestFixture.COFFEE_SET_MENU_ID;
            final var menuPrice = 100_000;
            final var menu = MenuTestFixture.coffeeSetMenu(menuPrice);

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            assertThatThrownBy(() -> testService.display(menuId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("메뉴를 공개할 수 있다.")
        @ParameterizedTest(name = "기존 메뉴 공개여부가 [{0}]일 때, 메뉴를 공개할 수 있다.")
        @ValueSource(booleans = {false, true})
        void display(boolean displayedBeforeChanged) {
            given(menuRepository.findById(MenuTestFixture.COFFEE_SET_MENU_ID))
                    .willReturn(Optional.of(MenuTestFixture.coffeeSetMenu(displayedBeforeChanged)));

            final var menu = testService.display(MenuTestFixture.COFFEE_SET_MENU_ID);

            assertThat(menu.isDisplayed()).isTrue();
        }
    }

    @DisplayName("메뉴 비공개")
    @Nested
    class Hide {
        @DisplayName("등록된 메뉴여야 한다.")
        @Test
        void menuNotFound() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            given(menuRepository.findById(menuId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.hide(menuId))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴를 비공개할 수 있다.")
        @ParameterizedTest(name = "기존 메뉴 공개여부가 [{0}]일 때, 메뉴를 비공개할 수 있다.")
        @ValueSource(booleans = {false, true})
        void hide(boolean displayedBeforeChanged) {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(displayedBeforeChanged);

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menuInRepo));

            // when
            testService.hide(menuId);

            // then
            assertThat(menuInRepo.isDisplayed()).isFalse();
        }
    }

    @DisplayName("모든 메뉴를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final var menu1 = new Menu();
        menu1.setId(LATTE_PRODUCT_ID);
        final var menu2 = new Menu();
        menu2.setId(AMERICANO_PRODUCT_ID);
        final var menusInRepo = List.of(menu1, menu2);
        given(menuRepository.findAll()).willReturn(menusInRepo);

        // when
        final var result = testService.findAll();

        // then
        assertThat(result).hasSize(2)
                .extracting(Menu::getId)
                .containsExactlyInAnyOrder(
                        LATTE_PRODUCT_ID,
                        AMERICANO_PRODUCT_ID
                );
    }
}
