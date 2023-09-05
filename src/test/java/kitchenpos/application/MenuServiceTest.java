package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.spy.SpyMenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private MenuService menuService;

    @Spy
    private SpyMenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void beforeEach() {
        menuService = new MenuService(menuRepository,
                menuGroupRepository,
                productRepository,
                purgomalumClient);
    }

    @DisplayName("메뉴 등록")
    @Nested
    class CreateTestGroup {

        @DisplayName("메뉴의 가격이 없으면 등록할 수 없다.")
        @Test
        void createTest1() {

            // given
            final Menu request = MenuFixture.createMenuWithPrice(null);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("메뉴의 가격이 음수 값이면 등록할 수 없다.")
        @Test
        void createTest2() {

            // given
            final Menu request = MenuFixture.createMenuWithPrice(-1);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("등록된 메뉴 그룹이 아니면 등록할 수 없다.")
        @Test
        void createTest3() {

            // given
            final Menu request = MenuFixture.createMenu();

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("상품 항목이 없으면 등록할 수 없다.")
        @Test
        void createTest4() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Menu request = MenuFixture.createMenuWithMenuProducts(null);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("등록된 상품의 항목과 맞지 않으면 등록할 수 없다.")
        @Test
        void createTest5() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Menu request = MenuFixture.createMenuWithMenuProducts(Collections.emptyList());

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("등록된 상품 항목과 맞지 않으면 등록할 수 없다.")
        @Test
        void createTest6() {

            // given
            final Product product1 = ProductFixture.createProduct();
            final Product product2 = ProductFixture.createProduct();
            final Product product3 = ProductFixture.createProduct();
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Menu request = MenuFixture.createMenu();

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product1, product2, product3));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("메뉴의 상품 수량이 음수 값이면 등록할 수 없다.")
        @Test
        void createTest7() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProduct();
            final Menu request = MenuFixture.createMenuWithProductAndMenuQuantity(product, -1);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("등록된 상품이 아니면 등록할 수 없다.")
        @Test
        void createTest8() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProduct();
            final Menu request = MenuFixture.createMenu();

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("메뉴 가격이 상품의 가격과 수량을 곱한 가격과 같거나 높지 않으면 등록할 수 없다.")
        @Test
        void createTest9() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProductWithPrice(500);
            final Menu request = MenuFixture.createMenuWithProductAndMenuQuantityAndPrice(product, 1, 700);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any()))
                    .willReturn(Optional.of(product));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("메뉴 이름이 없으면 등록할 수 없다.")
        @Test
        void createTest10() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProductWithPrice(500);
            final Menu request = MenuFixture.createMenuWithProductAndNameAndPriceAndMenuQuantity(product, null, 500, 1);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any()))
                    .willReturn(Optional.of(product));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("메뉴 이름이 비속어라면 등록할 수 없다.")
        @Test
        void createTest11() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProductWithPrice(500);
            final Menu request = MenuFixture.createMenuWithProductAndMenuQuantityAndPrice(product, 1, 500);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any()))
                    .willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(any()))
                    .willReturn(true);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("메뉴 등록 완료")
        @Test
        void createTest12() {

            // given
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProductWithPrice(500);
            final Menu request = MenuFixture.createMenuWithProductAndMenuQuantityAndPrice(product, 1, 500);

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(any()))
                    .willReturn(List.of(product));
            given(productRepository.findById(any()))
                    .willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(any()))
                    .willReturn(false);

            // when
            Menu actual = menuService.create(request);

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).getId()).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).getName()).isEqualTo(request.getName()),
                    () -> assertThat(Objects.requireNonNull(actual).getPrice()).isEqualTo(request.getPrice()),
                    () -> assertThat(Objects.requireNonNull(actual).isDisplayed()).isEqualTo(request.isDisplayed())
            );
        }
    }

    @DisplayName("메뉴 가격 변경")
    @Nested
    class ChangePriceTestGroup {

        @DisplayName("메뉴 가격이 없다면 메뉴 가격을 변경할 수 없다.")
        @Test
        void changePriceTest1() {

            // given
            final Menu request = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, null);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("메뉴 가격이 음수 값이면 메뉴 가격을 변경할 수 없다.")
        @Test
        void changePriceTest2() {

            // given
            final Menu request = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, -1);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("등록된 메뉴가 아니면 메뉴 가격을 변경할 수 없다.")
        @Test
        void changePriceTest3() {

            // given
            final Menu request = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, 700);

            given(menuRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("메뉴 가격이 상품의 가격과 수량을 곱한 가격과 같거나 높지 않으면 메뉴 가격을 변경할 수 없다.")
        @Test
        void changePriceTest4() {

            // given
            final Menu menu = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, 500);
            final Menu request = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, 700);

            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("메뉴 가격 완료")
        @Test
        void changePriceTest5() {

            // given
            final Menu request = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, 400);

            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(request));

            // when
            Menu actual = menuService.changePrice(UUID.randomUUID(), request);

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).getPrice()).isEqualTo(request.getPrice())
            );
        }
    }

    @DisplayName("메뉴 전시")
    @Nested
    class DisplayTestGroup {

        @DisplayName("등록된 메뉴가 아니면 메뉴를 전시할 수 없다.")
        @Test
        void displayTest1() {

            // given
            given(menuRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.display(UUID.randomUUID()));
        }

        @DisplayName("메뉴 가격이 상품의 가격과 수량을 곱한 가격과 같거나 높지 않으면 메뉴를 전시할 수 없다.")
        @Test
        void displayTest2() {

            // given
            final Menu menu = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, 700);

            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> menuService.display(UUID.randomUUID()));
        }

        @DisplayName("메뉴 전시 완료")
        @Test
        void displayTest3() {

            // given
            final Menu menu = MenuFixture.createMenuWithProductPriceAndMenuQuantityAndPrice(500, 1, 500);

            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));

            // when
            Menu actual = menuService.display(UUID.randomUUID());

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(actual).isDisplayed()).isTrue()
            );
        }
    }

    @DisplayName("메뉴 숨김 완료")
    @Test
    void hideTest() {

        // given
        final Menu menu = MenuFixture.createMenuWithDisplayed(true);

        given(menuRepository.findById(any()))
                .willReturn(Optional.of(menu));

        // when
        Menu actual = menuService.hide(UUID.randomUUID());

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(Objects.requireNonNull(actual).isDisplayed()).isFalse()
        );

    }

    @DisplayName("등록된 메뉴를 모두 조회")
    @Test
    void findAllTest() {

        // given
        final Menu menu = MenuFixture.createMenu();

        given(menuRepository.findAll())
                .willReturn(List.of(menu));

        // when
        List<Menu> actual = menuService.findAll();

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(Objects.requireNonNull(actual).size()).isOne()
        );
    }
}