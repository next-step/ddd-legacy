package kitchenpos.application;

import static kitchenpos.application.fixture.MenuFixtures.createMenu;
import static kitchenpos.application.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.application.fixture.MenuFixtures.십원짜리_상품_2개인_menuProduct;
import static kitchenpos.application.fixture.ProductFixtures.십원_상품;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Menu 는")
class MenuServiceTest {

    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);

    private final MenuService menuService = new MenuService(
        menuRepository,
        menuGroupRepository,
        productRepository,
        purgomalumClient
    );

    @Nested
    @DisplayName("추가할 수 있다.")
    class 추가할_수_있다 {

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("가격은 0원보다 작은 경우 추가할 수 없다.")
        @NullSource
        @CsvSource("-1")
        void 가격은_0원보다_작은_경우_추가할_수_없다(BigDecimal price) {
            // given
            final Menu request = createMenu(price, true, UUID.randomUUID(), new MenuProduct());

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("존재하지 않는 메뉴 그룹에는 속할 수 없다.")
        void 존재하지_않는_메뉴_그룹에는_속할_수_없다() {
            // given
            final UUID menuGroupId = UUID.randomUUID();
            final Menu request = createMenu(BigDecimal.ONE, true, menuGroupId);

            doReturn(Optional.empty()).when(menuGroupRepository).findById(menuGroupId);

            // when // then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("MenuProduct가 1개 보다 작으면 추가할 수 없다.")
        @NullAndEmptySource
        void menuProduct가_1개_보다_작으면_추가할_수_없다(List<MenuProduct> menuProducts) {
            // given
            final UUID menuGroupId = UUID.randomUUID();
            final Menu request = createMenu(BigDecimal.ONE, true, menuGroupId, menuProducts);

            doReturn(Optional.of(new MenuGroup())).when(menuGroupRepository).findById(menuGroupId);

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("MenuProduct은 존재하는 메뉴 상품으로 이루어져야 한다. 존재하지 않는 경우 추가 불가능")
        void 존재하지_않는_경우_생성_불가능() {
            // given
            final UUID menuGroupId = UUID.randomUUID();
            final Product 십원_상품 = 십원_상품();
            final MenuProduct menuProduct = createMenuProduct(십원_상품);
            final Menu request = createMenu(BigDecimal.ONE, true, menuGroupId, menuProduct);

            doReturn(Optional.of(new MenuGroup())).when(menuGroupRepository).findById(menuGroupId);
            doReturn(Arrays.asList()).when(productRepository).findAllByIdIn(
                request.getMenuProducts().stream()
                    .map(MenuProduct::getProductId)
                    .collect(Collectors.toList())
            );

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("MenuProduct의 상품 갯수는 0개 이상 이다. 작은 경우 추가 불가능")
        void MenuProduct의_상품_갯수는_0개_이상_이다() {
            // given
            final UUID menuGroupId = UUID.randomUUID();
            final Product 십원_상품 = 십원_상품();
            final MenuProduct menuProduct = createMenuProduct(0L, 십원_상품, -1);
            final Menu request = createMenu(BigDecimal.ONE, true, menuGroupId, menuProduct);

            doReturn(Optional.of(new MenuGroup())).when(menuGroupRepository).findById(menuGroupId);
            doReturn(Arrays.asList(십원_상품)).when(productRepository).findAllByIdIn(
                request.getMenuProducts().stream()
                    .map(MenuProduct::getProductId)
                    .collect(Collectors.toList())
            );

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("가격이 각 MenuProduct의 가격의 합보다 클 수 없다. ")
        void 가격이_각_MenuProduct의_가격의_합보다_클_수_없다() {
            // given
            final BigDecimal price = BigDecimal.valueOf(100L);
            final UUID menuGroupId = UUID.randomUUID();
            final Product 십원_상품 = 십원_상품();
            final MenuProduct menuProduct = createMenuProduct(0L, 십원_상품, 1);
            final Menu request = createMenu(price, true, menuGroupId, menuProduct);

            doReturn(Optional.of(new MenuGroup())).when(menuGroupRepository).findById(menuGroupId);
            doReturn(Arrays.asList(십원_상품)).when(productRepository).findAllByIdIn(
                request.getMenuProducts().stream()
                    .map(MenuProduct::getProductId)
                    .collect(Collectors.toList())
            );
            doReturn(Optional.of(십원_상품)).when(productRepository).findById(십원_상품.getId());

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("이름은 비어있거나 욕설이 포함될 수 없다.")
        @NullAndEmptySource
        @ValueSource(strings = "나쁜말")
        void 이름은_비어있거나_욕설이_포함될_수_없다(String name) {
            // given
            final UUID menuGroupId = UUID.randomUUID();
            final Product 십원_상품 = 십원_상품();
            final MenuProduct menuProduct = createMenuProduct(0L, 십원_상품, 1);
            final Menu request = createMenu(BigDecimal.ONE, name, true, menuGroupId, menuProduct);

            doReturn(Optional.of(new MenuGroup())).when(menuGroupRepository).findById(menuGroupId);
            doReturn(Arrays.asList(십원_상품)).when(productRepository).findAllByIdIn(
                request.getMenuProducts().stream()
                    .map(MenuProduct::getProductId)
                    .collect(Collectors.toList())
            );
            doReturn(Optional.of(십원_상품)).when(productRepository).findById(십원_상품.getId());
            doReturn(true).when(purgomalumClient).containsProfanity(name);

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
        }

        @Test
        @DisplayName("정상 추가")
        void 정상_추가() {
            // given
            final String menuName = "좋은말";
            final UUID menuGroupId = UUID.randomUUID();
            final Product 십원_상품 = 십원_상품();
            final BigDecimal menuPrice = BigDecimal.ONE;
            final Menu menu = createMenu(
                menuPrice,
                menuName,
                true,
                menuGroupId,
                createMenuProduct(0L, 십원_상품, 1)
            );

            doReturn(Optional.of(new MenuGroup())).when(menuGroupRepository).findById(menuGroupId);
            doReturn(Arrays.asList(십원_상품)).when(productRepository).findAllByIdIn(
                menu.getMenuProducts().stream()
                    .map(MenuProduct::getProductId)
                    .collect(Collectors.toList())
            );
            doReturn(Optional.of(십원_상품)).when(productRepository).findById(십원_상품.getId());
            doReturn(false).when(purgomalumClient).containsProfanity(menuName);
            doReturn(menu).when(menuRepository).save(any());

            // when
            final Menu actual = menuService.create(menu);

            // then
            assertAll(
                () -> assertThat(actual.getName()).isEqualTo(menuName),
                () -> assertThat(actual.getPrice()).isEqualTo(menuPrice)
            );
        }
    }

    @Nested
    @DisplayName("가격을 변경할 수 있다.")
    class 가격을_변경할_수_있다 {

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("변경할 가격이 0보다 작다면 변경 불가능하다.")
        @NullSource
        @CsvSource("-1")
        void 변경할_가격이_0보다_작다면_변경_불가능하다(BigDecimal price) {
            // given
            final UUID menuId = UUID.randomUUID();
            final Menu request = createMenu(menuId, price, true, UUID.randomUUID());

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.changePrice(menuId, request));
        }

        @Test
        @DisplayName("변경할 Menu가 없다면 변경 불가능하다.")
        void 변경할_Menu가_없다면_변경_불가능하다() {
            // given
            final UUID menuId = UUID.randomUUID();
            final Menu request = createMenu(menuId, BigDecimal.ONE, true, UUID.randomUUID());

            doReturn(Optional.empty()).when(menuRepository).findById(menuId);

            // when // then
            assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("정상 변경")
        void 정상_변경() {
            // given
            final UUID menuId = UUID.randomUUID();
            final BigDecimal changeRequestPrice = BigDecimal.TEN;
            final Menu request = new Menu();
            request.setPrice(changeRequestPrice);

            final Menu menu = createMenu(
                menuId,
                BigDecimal.ONE,
                true,
                UUID.randomUUID(),
                createMenuProduct(0L, 십원_상품(), 1)
            );

            doReturn(Optional.of(menu)).when(menuRepository).findById(menuId);

            // when
            final Menu actual = menuService.changePrice(menuId, request);

            // then
            assertAll(
                () -> assertThat(actual.getPrice()).isEqualTo(changeRequestPrice)
            );
        }
    }

    @Nested
    @DisplayName("전시할 수 있다.")
    class 전시할_수_있다 {

        @Test
        @DisplayName("Menu 존재하지 않는 경우 전시할 수 없다.")
        void menu가_존재하지_않는_경우_전시할_수_없다() {
            // given
            final UUID menuId = UUID.randomUUID();

            doReturn(Optional.empty()).when(menuRepository).findById(menuId);
            // when // then
            assertThatThrownBy(() -> menuService.display(menuId))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("가격은 0원 이상이어야 한다.")
        void 가격은_0원_이상이어야_한다() {
            // given
            final UUID menuId = UUID.randomUUID();

            final Menu menu = createMenu(
                menuId,
                BigDecimal.ZERO,
                false,
                UUID.randomUUID(),
                십원짜리_상품_2개인_menuProduct()
            );

            doReturn(Optional.of(menu)).when(menuRepository).findById(menuId);

            // when
            final Menu actual = menuService.display(menuId);
            // then
            assertThat(actual.isDisplayed()).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("숨길 수 있다")
    class 숨길_수_있다 {

        @Test
        @DisplayName("Menu 존재하지 않는 경우 숨길 수 없다.")
        void menu가_존재하지_않는_경우_숨길_수_없다() {
            // given
            final UUID menuId = UUID.randomUUID();

            doReturn(Optional.empty()).when(menuRepository).findById(menuId);
            // when // then
            assertThatThrownBy(() -> menuService.hide(menuId))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("Menu 존재하는 경우 숨길 수 있다.")
        void menu가_존재하는_경우_숨길_수_있다() {
            // given
            final UUID menuId = UUID.randomUUID();
            final Menu menu = createMenu(
                menuId,
                BigDecimal.TEN,
                true,
                UUID.randomUUID()
            );

            doReturn(Optional.of(menu)).when(menuRepository).findById(menuId);

            // when
            final Menu actual = menuService.hide(menuId);

            // then
            assertThat(actual.isDisplayed()).isEqualTo(false);
        }
    }
}
