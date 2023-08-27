package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupHelper;
import kitchenpos.helper.MenuHelper;
import kitchenpos.helper.ProductHelper;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static kitchenpos.helper.MenuHelper.DEFAULT_PRICE;
import static kitchenpos.helper.NameHelper.NAME_OF_255_CHARACTERS;
import static kitchenpos.helper.NameHelper.NAME_OF_256_CHARACTERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Transactional
class MenuServiceTest extends ApplicationTest {

    private static List<Product> createdProducts;
    private static List<MenuProduct> createdMenuProducts;
    private static MenuGroup createdMenuGroup;

    @Autowired
    private MenuService menuService;

    @SpyBean
    private PurgomalumClient purgomalumClient;

    @BeforeAll
    static void beforeAll(@Autowired ProductService productService,
                          @Autowired MenuGroupService menuGroupService) {

        createdProducts = IntStream.range(1, 5)
                .mapToObj(n -> productService.create(ProductHelper.create(BigDecimal.valueOf(n * 1000L))))
                .collect(toUnmodifiableList());
        createdMenuProducts = IntStream.range(0, createdProducts.size())
                .mapToObj(i -> createMenuProduct(i, i + 1))
                .collect(toUnmodifiableList());
        createdMenuGroup = menuGroupService.create(MenuGroupHelper.create());
    }

    private static MenuProduct createMenuProduct(int index, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq((long) index);
        menuProduct.setProductId(createdProducts.get(index).getId());
        menuProduct.setProduct(createdProducts.get(index));
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private List<UUID> collectMenuProductIds(List<MenuProduct> menuProducts) {
        return menuProducts.stream()
                .map(m -> m.getProduct().getId())
                .collect(toUnmodifiableList());
    }

    private List<String> collectMenuNames(List<MenuProduct> menuProducts) {
        return menuProducts.stream()
                .map(m -> m.getProduct().getName())
                .collect(toUnmodifiableList());
    }

    @DisplayName("새로운 메뉴를 등록한다.")
    @Nested
    class CreateMenu {

        @DisplayName("메뉴 가격은 0원 이상이어야 한다.")
        @Nested
        class Policy1 {
            @DisplayName("메뉴에 대한 가격은 0원 이상인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {0, 1, 1000})
            void success1(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);
                Menu menu = MenuHelper.create(price, createdMenuGroup.getId(), createdMenuProducts);

                // When
                Menu createdMenu = menuService.create(menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(price);
            }

            @DisplayName("메뉴에 대한 가격은 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(BigDecimal price) {
                // When
                Menu menu = MenuHelper.create(price, createdMenuGroup.getId(), createdMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("메뉴에 대한 가격은 0원 미만인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
            void fail2(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);

                // When
                Menu menu = MenuHelper.create(price, createdMenuGroup.getId(), createdMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("등록할 메뉴 그룹이 있어야 한다.")
        @Nested
        class Policy2 {
            @DisplayName("등록할 메뉴 그룹이 있는 경우 (성공)")
            @Test
            void success1() {
                // Given
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts);

                // When
                Menu createdMenu = menuService.create(menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(DEFAULT_PRICE);
                assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(createdMenuGroup.getId());
            }

            @DisplayName("등록할 메뉴 그룹 ID가 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(UUID menuGroupId) {
                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, menuGroupId, createdMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(InvalidDataAccessApiUsageException.class);
            }

            @DisplayName("등록할 메뉴 그룹이 없는 경우 (실패)")
            @Test
            void fail2() {
                // Given
                final UUID menuGroupId = UUID.randomUUID();

                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, menuGroupId, createdMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @DisplayName("메뉴에 등록할 상품이 1개 이상 있어야 한다.")
        @Nested
        class Policy3 {
            @DisplayName("메뉴에 등록할 상품이 1개 이상 있는 경우 (성공)")
            @Test
            void success1() {
                // Given
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts);

                // When
                Menu createdMenu = menuService.create(menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(DEFAULT_PRICE);
                assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(createdMenuGroup.getId());
                assertThat(createdMenu.getMenuProducts().size()).isEqualTo(createdMenuProducts.size());
                assertThat(collectMenuProductIds(createdMenu.getMenuProducts()))
                        .containsAll(collectMenuProductIds(createdMenuProducts));
            }

            @DisplayName("메뉴에 등록할 상품이 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(List<MenuProduct> menuProducts) {
                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), menuProducts);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("메뉴에 등록할 상품이 없는 경우 (실패)")
            @Test
            void fail2() {
                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), List.of());

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("메뉴에 등록할 상품의 수량은 0개 이상이어야 한다.")
        @Nested
        class Policy4 {
            @DisplayName("메뉴에 등록할 상품의 수량이 1개 이상 있는 경우 (성공)")
            @Test
            void success1() {
                // Given
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts);

                // When
                Menu createdMenu = menuService.create(menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(DEFAULT_PRICE);
                assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(createdMenuGroup.getId());
                assertThat(createdMenu.getMenuProducts().size()).isEqualTo(createdMenuProducts.size());
                assertThat(collectMenuProductIds(createdMenu.getMenuProducts()))
                        .containsAll(collectMenuProductIds(createdMenuProducts));
            }

            @DisplayName("메뉴에 등록할 상품의 수량이 1개 미만인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(longs = {0, -1, -10})
            void fail1(final long productQuantity) {
                // Given
                List<MenuProduct> modifiedMenuProducts = IntStream.range(0, createdProducts.size())
                        .mapToObj(i -> createMenuProduct(i, productQuantity))
                        .collect(toUnmodifiableList());

                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), modifiedMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("메뉴의 가격은 메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합보다 클 수 없다.")
        @Nested
        class Policy5 {
            @DisplayName("메뉴의 가격이 (메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합)보다 작거나 같은 경우 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {0, 1, 10, 100})
            void success1(final int price) {
                // Given
                BigDecimal totalPrice = createdMenuProducts.parallelStream()
                        .map(menuProduct -> menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal menuPrice = totalPrice.subtract(BigDecimal.valueOf(price));

                Menu menu = MenuHelper.create(menuPrice, createdMenuGroup.getId(), createdMenuProducts);

                // When
                Menu createdMenu = menuService.create(menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(menuPrice);
                assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(createdMenuGroup.getId());
                assertThat(createdMenu.getMenuProducts().size()).isEqualTo(createdMenuProducts.size());
                assertThat(collectMenuProductIds(createdMenu.getMenuProducts()))
                        .containsAll(collectMenuProductIds(createdMenuProducts));
            }

            @DisplayName("메뉴의 가격이 (메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합)보다 큰 경우 (실패)")
            @ParameterizedTest
            @ValueSource(ints = {1, 10, 100})
            void fail1(final int price) {
                // Given
                BigDecimal totalPrice = createdMenuProducts.parallelStream()
                        .map(menuProduct -> menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal menuPrice = totalPrice.add(BigDecimal.valueOf(price));

                Menu menu = MenuHelper.create(menuPrice, createdMenuGroup.getId(), createdMenuProducts);

                // When
                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("메뉴명은 비어있을 수 없고, 255자를 초과할 수 없다.")
        @Nested
        class Policy6 {
            @DisplayName("메뉴명이 0자 이상 255자 이하인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(strings = {"", "한", "a", "1", "메뉴명", "menu name", "메뉴 A", NAME_OF_255_CHARACTERS})
            void success1(final String name) {
                // Given
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts, name);

                // When
                Menu createdMenu = menuService.create(menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(DEFAULT_PRICE);
                assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(createdMenuGroup.getId());
                assertThat(createdMenu.getMenuProducts().size()).isEqualTo(createdMenuProducts.size());
                assertThat(collectMenuProductIds(createdMenu.getMenuProducts()))
                        .containsAll(collectMenuProductIds(createdMenuProducts));
                assertThat(collectMenuNames(createdMenu.getMenuProducts()))
                        .containsAll(collectMenuNames(createdMenuProducts));
            }

            @DisplayName("메뉴명이 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(final String name) {
                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts, name);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("메뉴명이 255자를 초과한 경우 (실패)")
            @ParameterizedTest
            @ValueSource(strings = {NAME_OF_256_CHARACTERS})
            void fail2(final String name) {
                // When
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts, name);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(DataIntegrityViolationException.class);
            }
        }

        @DisplayName("메뉴명에는 비속어가 포함되어 있으면 안 된다.")
        @Nested
        class Policy7 {
            @DisplayName("메뉴명이 비속어인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(strings = {"나쁜놈", "fuck"})
            void fail1(final String name) {
                // Given
                when(purgomalumClient.containsProfanity(name)).thenReturn(true);
                Menu menu = MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts, name);

                // When
                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @DisplayName("기존 메뉴의 가격을 변경한다.")
    @Nested
    class ChangeMenuPrice {

        private Menu beforeCreatedMenu;

        @BeforeEach
        void beforeEach() {
            beforeCreatedMenu = menuService.create(MenuHelper.create(DEFAULT_PRICE, createdMenuGroup.getId(), createdMenuProducts));
        }

        @DisplayName("메뉴 가격은 0원 이상이어야 한다.")
        @Nested
        class Policy1 {
            @DisplayName("메뉴에 대한 가격은 0원 이상인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {0, 1, 1000})
            void success1(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);
                Menu menu = MenuHelper.create(price, createdMenuGroup.getId(), createdMenuProducts);

                // When
                Menu createdMenu = menuService.changePrice(beforeCreatedMenu.getId(), menu);

                // Then
                assertThat(createdMenu.getPrice()).isEqualTo(price);
            }

            @DisplayName("메뉴에 대한 가격은 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(BigDecimal price) {
                // When
                Menu menu = MenuHelper.create(price, createdMenuGroup.getId(), createdMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.changePrice(beforeCreatedMenu.getId(), menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("메뉴에 대한 가격은 0원 미만인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
            void fail2(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);

                // When
                Menu menu = MenuHelper.create(price, createdMenuGroup.getId(), createdMenuProducts);

                // Then
                assertThatThrownBy(() -> menuService.changePrice(beforeCreatedMenu.getId(), menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

}