package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static kitchenpos.fixture.ProductFixture.createProducts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Menu")
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

    @Nested
    @DisplayName("메뉴를 등록할 수 있다.")
    class create {

        @Nested
        @DisplayName("가격은 비어있거나 0보다 적을 수 없다.")
        class create_1 {

            @Test
            @DisplayName("비어있는 경우")
            void create_1_1() {
                // When
                Menu menu = createMenuWithPrice(null);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("0보다 작은 경우")
            void create_1_2() {
                // When
                Menu menu = createMenuWithPrice(BigDecimal.valueOf(-1));

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("메뉴는 미리 등록된 메뉴그룹에 속한다.")
        void create_2() {
            // Given
            Menu menu = createMenu();
            when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Nested
        @DisplayName("메뉴상품은 미리 등록되어 있어야 한다.")
        class create_3 {

            @Test
            @DisplayName("null 인 경우")
            void create_3_1() {
                // Given
                MenuGroup menuGroup = createMenuGroup();
                when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

                // When
                Menu menu = createMenuWithMenuProducts(null);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("비어있는 경우")
            void create_3_2() {
                // Given
                MenuGroup menuGroup = createMenuGroup();
                when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

                // When
                Menu menu = createMenuWithMenuProducts(Collections.emptyList());

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("메뉴는 1개 이상의 상품를 가진다.")
        void create_4() {
            // Given
            Menu menu = createMenu();

            MenuGroup menuGroup = createMenuGroup();
            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // When
            List<Product> products = createProducts();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(products);

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴상품의 수량은 0 이하일 수 없다.")
        void create_5() {
            // Given
            MenuGroup menuGroup = createMenuGroup();
            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            List<Product> products = createProducts();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(products);

            // When
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setQuantity(-1);
            Menu menu = createMenuWithMenuProducts(List.of(menuProduct));

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품 미리 등록되어 있어야 한다.")
        void create_6() {
            // Given
            Menu menu = createMenu();

            MenuGroup menuGroup = createMenuGroup();
            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            Product product = createProduct();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            // When
            when(productRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("가격은 상품들의 수량 * 가격 보다 클 수 없다.")
        void create_7() {
            // Given
            Menu menu = createMenu();

            MenuGroup menuGroup = createMenuGroup();
            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            Product product = createProduct();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));

            // When
            menu.setPrice(BigDecimal.valueOf(100));

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Nested
        @DisplayName("메뉴의 이름이 없거나 비속어 이면 안된다.")
        class create_8 {

            @Test
            @DisplayName("없는 경우")
            void create_8_1() {
                // Given
                MenuGroup menuGroup = createMenuGroup();
                when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

                Product product = createProduct();
                when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
                when(productRepository.findById(any())).thenReturn(Optional.of(product));

                // When
                Menu menu = createMenuWithName(null);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("비속어인 경우")
            void create_8_2() {
                // Given
                MenuGroup menuGroup = createMenuGroup();
                when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

                Product product = createProduct();
                when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
                when(productRepository.findById(any())).thenReturn(Optional.of(product));

                // When
                when(purgomalumClient.containsProfanity("비속어")).thenReturn(true);
                Menu menu = createMenuWithName("비속어");

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("등록")
        void create_9() {
            // Given
            Menu menu = createMenu();

            MenuGroup menuGroup = createMenuGroup();
            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            Product product = createProduct();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));

            // When
            when(menuRepository.save(any())).thenReturn(menu);
            Menu result = menuService.create(menu);

            // Then
            assertThat(menu).isEqualTo(result);
        }
    }

    @Nested
    @DisplayName("메뉴의 가격은 변경할 수 있다.")
    class changePrice {

        @Nested
        @DisplayName("변경할 가격은 비어있거나 0보다 적을 수 없다.")
        class changePrice_1 {

            @Test
            @DisplayName("비어있는 경우")
            void changePrice_1_1() {
                // When
                Menu menu = createMenuWithPrice(null);

                // Then
                assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("0보다 작은 경우")
            void changePrice_1_2() {
                // When
                Menu menu = createMenuWithPrice(BigDecimal.valueOf(-1));

                // Then
                assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("존재하는 메뉴이어야 한다.")
        void changePrice_2() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("변경할 가격은 상품들의 수량 * 가격 보다 클 수 없다.")
        void changePrice_3() {
            // Given
            Menu menu = createMenuWithPrice(BigDecimal.valueOf(100));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // Then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("변경")
        void changePrice_4() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // When
            Menu result = menuService.changePrice(menu.getId(), menu);

            // Then
            assertThat(result).isEqualTo(menu);
        }
    }

    @Test
    @DisplayName("메뉴의 전체목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<Menu> menus = createMenus();
        when(menuRepository.findAll()).thenReturn(menus);

        // When
        List<Menu> findAllMenus = menuService.findAll();

        // Then
        assertThat(findAllMenus).hasSize(menus.size());
    }

    @Nested
    @DisplayName("메뉴를 노출할 수 있다.")
    class display {

        @Test
        @DisplayName("존재하는 메뉴이어야 한다.")
        void display_1() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.display(menu.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 상품들의 수량 * 가격 이하일 경우에만 노출한다.")
        void display_2() {
            // Given
            Menu menu = createMenuWithPrice(BigDecimal.valueOf(100));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // Then
            assertThatThrownBy(() -> menuService.display(menu.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("노출")
        void display_3() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // When
            Menu result = menuService.display(menu.getId());

            // Then
            assertThat(result.isDisplayed()).isTrue();
        }
    }

    @Nested
    @DisplayName("메뉴를 숨길 수 있다.")
    class hide {

        @Test
        @DisplayName("존재하는 메뉴이어야 한다.")
        void hide_1() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.hide(menu.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("숨김")
        void hide_2() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // When
            Menu result = menuService.hide(menu.getId());

            // Then
            assertThat(result.isDisplayed()).isFalse();
        }
    }

}