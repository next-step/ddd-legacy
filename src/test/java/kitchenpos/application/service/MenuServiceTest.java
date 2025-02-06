package kitchenpos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import kitchenpos.application.MenuService;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private Menu chickenMenu;

    private Product chicken;

    @BeforeEach
    void setUp() {
        chickenMenu = MenuFixture.init().create();
        chicken = ProductFixture.init().create();
    }

    @Nested
    @DisplayName("메뉴 조회")
    class 메뉴_조회 {

        @Test
        @DisplayName("성공 : 특정 조건 없이 상품의 모든 목록을 조회할 수 있다.")
        void 메뉴목록_조회() {
            when(menuRepository.findAll()).thenReturn(List.of(chickenMenu));
            List<Menu> result = menuService.findAll();

            assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertEquals(result.size(), 1)
            );
        }
    }

    @Nested
    @DisplayName("메뉴 등록")
    class 메뉴_등록 {

        @Test
        @DisplayName("성공")
        void 메뉴등록_성공() {
            mockCreateMenu(false);

            mockSaveMenu();

            var result = menuService.create(chickenMenu);

            assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getName(), chickenMenu.getName()),
                () -> assertEquals(result.getPrice(), chickenMenu.getPrice()),
                () -> assertEquals(result.isDisplayed(), chickenMenu.isDisplayed()),
                () -> assertEquals(result.getMenuGroup(), chickenMenu.getMenuGroup()),
                () -> assertEquals(result.getMenuProducts(), chickenMenu.getMenuProducts())
            );

        }

        @DisplayName("메뉴가격은 0원 이상이어야 한다.")
        @ParameterizedTest
        @ValueSource(ints = {-10000, 0, 10000})
        void 메뉴가격_허용범위_검사(final int price) {
            chickenMenu = MenuFixture.test(
                null,
                BigDecimal.valueOf(price),
                null,
                true,
                null
            ).create();

            if (price < 0) {
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(chickenMenu));
            }
        }

        @Test
        @DisplayName("메뉴 그룹에 속해 있어야 한다.")
        void 메뉴그룹_검사() {

            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(chickenMenu));
        }

        @DisplayName("메뉴명도 상품명처럼 비속어를 포함하면 안된다.")
        @ParameterizedTest
        @ValueSource(strings = {"나쁜", "XXX"})
        void 메뉴명_비속어_검사(final String name) {
            mockCreateMenu(true);

            chickenMenu = MenuFixture.test(
                name,
                null,
                null,
                true,
                null
            ).create();

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(chickenMenu));
        }

        @DisplayName("등록 메뉴가격이 구성 상품의 총 금액보다 크지 않아야 한다.")
        @ParameterizedTest
        @CsvSource({"100000, 100"})
        void 메뉴_구성상품_금액_비교검사(final int price1, final int price2) {
            chicken = new ProductFixture(
                null,
                null,
                BigDecimal.valueOf(price2)
            ).create();

            mockFindByMenuGroup();
            mockFindAllByProduct();
            mockFindByProduct(chicken);

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(chickenMenu));
        }

        @Test
        @DisplayName("메뉴 상품 정보를 반드시 가진다.")
        void 메뉴구성상품_검사() {
            mockFindByMenuGroup();

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(chickenMenu));
        }

        @DisplayName("메뉴 상품 정보에 속한 상품의 수량은 0개 이상이어야 한다.")
        @ParameterizedTest
        @ValueSource(ints = {-100, 0, 100})
        void 메뉴구성상품_수량_검사(final int qty) {
            chickenMenu = MenuFixture.test(
                null,
                null,
                null,
                true,
                List.of(new MenuProductFixture(
                    new ProductFixture(
                        null,
                        null,
                        null
                    ).create(),
                    qty
                ).create())
            ).create();

            if (qty < 0) {
                mockFindByMenuGroup();
                mockFindAllByProduct();
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(chickenMenu));
            }
        }


    }

    @Nested
    @DisplayName("메뉴 노출")
    class 메뉴_노출 {

        @Test
        @DisplayName("성공")
        void 메뉴_노출_성공() {
            mockFindByMenu();

            assertThatCode(() -> {
                menuService.display(chickenMenu.getId());
            }).doesNotThrowAnyException();

        }

        @DisplayName("메뉴가격이 구성 상품 총 금액보다 크지 않아야 한다.")
        @ParameterizedTest
        @CsvSource({"100000, 100"})
        void 변경가격_비교_검사(final int price1, final int price2) {
            chickenMenu = MenuFixture.test(
                null,
                BigDecimal.valueOf(price1),
                null,
                true,
                List.of(new MenuProductFixture(
                    new ProductFixture(
                        null,
                        null,
                        BigDecimal.valueOf(price2)
                    ).create(),
                    100
                ).create())
            ).create();

            mockFindByMenu();

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> menuService.display(chickenMenu.getId()));
        }
    }

    @Nested
    @DisplayName("메뉴 숨김")
    class 메뉴_숨김 {

        @Test
        @DisplayName("성공 : 등록 메뉴를 숨긴다.")
        void 메뉴_숨김_성공() {
            mockFindByMenu();

            menuService.hide(chickenMenu.getId());

            assertThat(chickenMenu.isDisplayed()).isFalse();
        }
    }

    @Nested
    @DisplayName("메뉴 가격변경")
    class 메뉴_가격변경 {

        @ParameterizedTest
        @DisplayName("성공")
        @ValueSource(ints = {0, 1000, 10000})
        void 메뉴_가격변경_성공(final int price) {
            chickenMenu = MenuFixture.test(
                null,
                BigDecimal.valueOf(price),
                null,
                true,
                null
            ).create();

            mockFindByMenu();

            assertThatCode(() -> {
                menuService.changePrice(chickenMenu.getId(), chickenMenu);
            }).doesNotThrowAnyException();
        }

        @DisplayName("변경가격이 0원 보다 작으면 안된다.")
        @ParameterizedTest
        @ValueSource(ints = {-10000, 0, 10000})
        void 변경가격_허용범위_검사(final int price) {
            chickenMenu = MenuFixture.test(
                null,
                BigDecimal.valueOf(price),
                null,
                true,
                null
            ).create();

            if (price < 0) {
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.changePrice(chickenMenu.getId(), chickenMenu));
            }
        }

        @DisplayName("변경가격이 메뉴 구성 상품 총 금액보다 크지 않아야 한다.")
        @ParameterizedTest
        @CsvSource({"100000, 100"})
        void 변경가격_비교_검사(final int price1, final int price2) {
            chickenMenu = MenuFixture.test(
                null,
                BigDecimal.valueOf(price1),
                null,
                true,
                List.of(new MenuProductFixture(
                    new ProductFixture(
                        null,
                        null,
                        BigDecimal.valueOf(price2)
                    ).create(),
                    100
                ).create())
            ).create();

            mockFindByMenu();

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(chickenMenu.getId(), chickenMenu));
        }
    }

    private void mockCreateMenu(boolean isProfanity) {
        mockFindByMenuGroup();
        mockFindAllByProduct();
        mockFindByProduct(chicken);
        mockCheckMenuName(isProfanity);
    }

    private void mockFindByMenu() {
        when(menuRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(chickenMenu));
    }

    private void mockFindByMenuGroup() {
        when(menuGroupRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(chickenMenu.getMenuGroup()));
    }

    private void mockFindAllByProduct() {
        when(productRepository.findAllByIdIn(Mockito.any()))
            .thenReturn(chickenMenu.getMenuProducts()
                .stream()
                .map(MenuProduct::getProduct)
                .collect(Collectors.toList()));
    }

    private void mockFindByProduct(Product chicken) {
        when(productRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(chicken));
    }

    private void mockCheckMenuName(boolean isProfanity) {
        when(purgomalumClient.containsProfanity(anyString())).thenReturn(isProfanity);
    }

    private void mockSaveMenu() {
        when(menuRepository.save(Mockito.any(Menu.class))).thenReturn(chickenMenu);
    }

}
