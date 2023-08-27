package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    private static final UUID MENU_ID = UUID.randomUUID();
    private static final UUID MENU_GROUP_ID = UUID.randomUUID();
    private static final String MENU_NAME = "name";
    private static final BigDecimal MENU_PRICE = BigDecimal.ONE;
    private Menu menu;
    private MenuGroup menuGroup;
    private MenuProduct menuProduct;
    private Product product;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup();

        product = new Product();
        product.setPrice(BigDecimal.ONE);

        menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        menu = new Menu();
        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        menu.setPrice(MENU_PRICE);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setMenuGroupId(MENU_GROUP_ID);
    }

    @Nested
    @DisplayName("메뉴를 등록할 수 있다.")
    class create {

        @Nested
        @DisplayName("가격은 비어있거나 0보다 작으면 예외가 발생한다.")
        class create_1 {

            @Test
            @DisplayName("비어있는 경우")
            void create_1_1() {
                // When
                menu.setPrice(null);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("0보다 작은 경우")
            void create_1_2() {
                // When
                menu.setPrice(BigDecimal.valueOf(-1));

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("메뉴그룹은 미리 등록되어 있지 않으면 예외가 발생한다.")
        void create_2() {
            // Given
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Nested
        @DisplayName("미리 존재하는 메뉴상품이 아니면 예외가 발생한다.")
        class create_3 {

            @Test
            @DisplayName("null 인 경우")
            void create_3_1() {
                // Given
                when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));

                // When
                menu.setMenuProducts(null);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("비어있는 경우")
            void create_3_2() {
                // Given
                when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));

                // When
                menu.setMenuProducts(Collections.emptyList());

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("메뉴상품 수량은 실제 상품의 수량과 일치하지 않으면 예외가 발생한다.")
        void create_4() {
            // Given
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));

            // When
            menu.setMenuProducts(List.of(menuProduct, menuProduct));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴상품의 수량이 0보다 작으면 예외가 발생한다.")
        void create_5() {
            // Given
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            // When
            menuProduct.setQuantity(-1);
            menu.setMenuProducts(List.of(menuProduct));

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품 ID로 상품을 찾을 수 없을 경우 예외가 발생한다.")
        void create_6() {
            // Given
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            // When
            when(productRepository.findById(menuProduct.getProductId())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("가격은 상품들의 수량 * 가격 보다 크면 예외가 발생한다.")
        void create_7() {
            // Given
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
            when(productRepository.findById(menuProduct.getProductId())).thenReturn(Optional.of(product));

            // When
            menu.setPrice(BigDecimal.TEN);

            // Then
            assertThatThrownBy(() -> menuService.create(menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Nested
        @DisplayName("메뉴의 이름이 없거나 비속어 이면 예외가 발생한다.")
        class create_8 {

            @Test
            @DisplayName("없는 경우")
            void create_8_1() {
                // Given
                when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
                when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
                when(productRepository.findById(menuProduct.getProductId())).thenReturn(Optional.of(product));

                // When
                menu.setName(null);

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("비속어인 경우")
            void create_8_2() {
                // Given
                when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
                when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
                when(productRepository.findById(menuProduct.getProductId())).thenReturn(Optional.of(product));

                // When
                when(purgomalumClient.containsProfanity("비속어")).thenReturn(true);
                menu.setName("비속어");

                // Then
                assertThatThrownBy(() -> menuService.create(menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("등록")
        void create_9() {
            // Given
            when(menuGroupRepository.findById(menu.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
            when(productRepository.findById(menuProduct.getProductId())).thenReturn(Optional.of(product));

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
        @DisplayName("가격은 비어있거나 0보다 작으면 예외가 발생한다.")
        class changePrice_1 {

            @Test
            @DisplayName("비어있는 경우")
            void changePrice_1_1() {
                // When
                menu.setPrice(null);

                // Then
                assertThatThrownBy(() -> menuService.changePrice(MENU_ID, menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("0보다 작은 경우")
            void changePrice_1_2() {
                // When
                menu.setPrice(BigDecimal.valueOf(-1));

                // Then
                assertThatThrownBy(() -> menuService.changePrice(MENU_ID, menu))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        @DisplayName("미리 존재하지 않는 메뉴일 경우 예외가 발생한다.")
        void changePrice_2() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.changePrice(MENU_ID, menu))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("변경할 가격이 상품들의 수량 * 가격 보다 크면 예외가 발생한다.")
        void changePrice_3() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));

            // When
            menu.setPrice(BigDecimal.TEN);

            // Then
            assertThatThrownBy(() -> menuService.changePrice(MENU_ID, menu))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("변경")
        void changePrice_4() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));

            // When
            Menu result = menuService.changePrice(MENU_ID, menu);

            // Then
            assertThat(result).isEqualTo(menu);
        }
    }

    @Test
    @DisplayName("메뉴의 전체목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<Menu> menus = List.of(menu, menu);
        when(menuRepository.findAll()).thenReturn(menus);

        // When
        List<Menu> findAllMenus = menuService.findAll();

        // Then
        assertThat(findAllMenus.size()).isEqualTo(2);
    }

    @Nested
    @DisplayName("메뉴를 노출할 수 있다.")
    class display {

        @Test
        @DisplayName("미리 존재하지 않는 메뉴일 경우 예외가 발생한다.")
        void display_1() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.display(MENU_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 상품들의 수량 * 가격보다 클 경우 예외가 발생한다.")
        void display_2() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));

            // When
            menu.setPrice(BigDecimal.TEN);

            // Then
            assertThatThrownBy(() -> menuService.display(MENU_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("노출")
        void display_3() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));

            // When
            Menu result = menuService.display(MENU_ID);

            // Then
            assertThat(result.isDisplayed()).isTrue();
        }
    }

    @Nested
    @DisplayName("메뉴를 숨길 수 있다.")
    class hide {

        @Test
        @DisplayName("미리 존재하지 않는 메뉴일 경우 예외가 발생한다.")
        void hide_1() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> menuService.hide(MENU_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("숨김")
        void hide_2() {
            // Given
            when(menuRepository.findById(MENU_ID)).thenReturn(Optional.of(menu));

            // When
            Menu result = menuService.hide(MENU_ID);

            // Then
            assertThat(result.isDisplayed()).isFalse();
        }
    }

}