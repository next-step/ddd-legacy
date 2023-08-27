package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupHelper;
import kitchenpos.helper.MenuHelper;
import kitchenpos.helper.ProductHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static kitchenpos.helper.MenuHelper.DEFAULT_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class MenuServiceTest extends ApplicationTest {

    private static List<Product> createdProducts;
    private static List<MenuProduct> createdMenuProducts;
    private static MenuGroup createdMenuGroup;

    @Autowired
    private MenuService menuService;

    @BeforeAll
    static void beforeAll(@Autowired ProductService productService,
                          @Autowired MenuGroupService menuGroupService) {

        createdProducts = IntStream.range(1, 5)
                .mapToObj(n -> productService.create(ProductHelper.create(BigDecimal.valueOf(n * 1000L))))
                .collect(toUnmodifiableList());
        createdMenuProducts = IntStream.range(0, createdProducts.size())
                .mapToObj(i -> createMenuProduct(i, i))
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
    }

}