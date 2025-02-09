package mission.step3;

import kitchenpos.application.MenuService;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    @Nested
    @DisplayName("메뉴 생성")
    class CreateMenu {

        @Test
        @DisplayName("메뉴를 생성한다")
        void create() {
            // given
            UUID productId = UUID.randomUUID();
            UUID menuGroupId = UUID.randomUUID();

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(menuGroupId);
            menuGroup.setName("음료");

            Product product = new Product();
            product.setId(productId);
            product.setPrice(BigDecimal.valueOf(4000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);

            Menu request = new Menu();
            request.setName("아메리카노");
            request.setPrice(BigDecimal.valueOf(4000));
            request.setMenuGroupId(menuGroupId);
            request.setDisplayed(true);
            request.setMenuProducts(List.of(menuProduct));

            given(menuGroupRepository.findById(menuGroupId))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(productId)))
                    .willReturn(List.of(product));
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(request.getName()))
                    .willReturn(false);
            given(menuRepository.save(any(Menu.class)))
                    .willAnswer(invocation -> {
                        Menu saved = invocation.getArgument(0);
                        saved.setId(UUID.randomUUID());
                        return saved;
                    });

            // when
            Menu created = menuService.create(request);

            // then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("아메리카노");
            assertThat(created.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4000));
            assertThat(created.getMenuGroup()).isEqualTo(menuGroup);
            assertThat(created.isDisplayed()).isTrue();
            assertThat(created.getMenuProducts()).hasSize(1);
        }

        @Test
        @DisplayName("가격이 메뉴 상품의 가격 합계보다 크면 예외가 발생한다")
        void createWithPriceGreaterThanTotalProductPrice() {
            // given
            UUID menuGroupId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(menuGroupId);

            Product product = new Product();
            product.setId(productId);
            product.setPrice(BigDecimal.valueOf(4000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(1);

            Menu request = new Menu();
            request.setName("아메리카노");
            request.setPrice(BigDecimal.valueOf(5000));
            request.setMenuGroupId(menuGroupId);
            request.setMenuProducts(List.of(menuProduct));

            given(menuGroupRepository.findById(menuGroupId))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(productId)))
                    .willReturn(List.of(product));
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 그룹이 존재하지 않으면 예외가 발생한다")
        void createWithNonExistentMenuGroup() {
            // given
            Menu request = new Menu();
            request.setName("아메리카노");
            request.setPrice(BigDecimal.valueOf(4000));
            request.setMenuGroupId(UUID.randomUUID());

            given(menuGroupRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴 상품의 수량이 0 이하면 예외가 발생한다")
        void createWithInvalidProductQuantity() {
            // given
            UUID menuGroupId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(menuGroupId);

            Product product = new Product();
            product.setId(productId);
            product.setPrice(BigDecimal.valueOf(4000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(productId);
            menuProduct.setQuantity(0);

            Menu request = new Menu();
            request.setName("아메리카노");
            request.setPrice(BigDecimal.valueOf(4000));
            request.setMenuGroupId(menuGroupId);
            request.setMenuProducts(List.of(menuProduct));

            given(menuGroupRepository.findById(menuGroupId))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(productId)))
                    .willReturn(List.of(product));
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangePrice {
        @Test
        @DisplayName("메뉴의 가격을 변경한다")
        void changePrice() {
            // given
            UUID menuId = UUID.randomUUID();
            Product product = new Product();
            product.setPrice(BigDecimal.valueOf(4000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setId(menuId);
            menu.setPrice(BigDecimal.valueOf(4000));
            menu.setMenuProducts(List.of(menuProduct));

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(4000));

            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when
            Menu updated = menuService.changePrice(menuId, request);

            // then
            assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4000));
        }
    }

    @Nested
    @DisplayName("메뉴 표시 상태 변경")
    class DisplayStatus {
        @Test
        @DisplayName("메뉴를 보임 상태로 변경한다")
        void display() {
            // given
            UUID menuId = UUID.randomUUID();
            Product product = new Product();
            product.setPrice(BigDecimal.valueOf(4000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setId(menuId);
            menu.setPrice(BigDecimal.valueOf(4000));
            menu.setMenuProducts(List.of(menuProduct));
            menu.setDisplayed(false);

            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when
            Menu displayed = menuService.display(menuId);

            // then
            assertThat(displayed.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("메뉴 가격이 구성 상품들의 총합보다 높으면 보임 상태로 변경되지 않고 예외가 발생한다")
        void displayFailsWhenMenuPriceExceedsTotalProductPrice() {

            // given
            UUID menuId = UUID.randomUUID();
            Product product = new Product();
            product.setPrice(BigDecimal.valueOf(4000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setId(menuId);

            menu.setPrice(BigDecimal.valueOf(5000));
            menu.setMenuProducts(List.of(menuProduct));
            menu.setDisplayed(false);

            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> menuService.display(menuId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("메뉴를 숨김 상태로 변경한다")
        void hide() {
            // given
            UUID menuId = UUID.randomUUID();
            Menu menu = new Menu();
            menu.setId(menuId);
            menu.setDisplayed(true);

            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when
            Menu hidden = menuService.hide(menuId);

            // then
            assertThat(hidden.isDisplayed()).isFalse();
        }
    }

    @Test
    @DisplayName("전체 메뉴를 조회한다")
    void findAll() {
        // given
        Menu menu1 = new Menu();
        menu1.setId(UUID.randomUUID());
        menu1.setName("아메리카노");
        menu1.setPrice(BigDecimal.valueOf(4000));

        Menu menu2 = new Menu();
        menu2.setId(UUID.randomUUID());
        menu2.setName("카페라떼");
        menu2.setPrice(BigDecimal.valueOf(4500));

        given(menuRepository.findAll())
                .willReturn(List.of(menu1, menu2));

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertThat(menus).hasSize(2);
        assertThat(menus.get(0).getName()).isEqualTo("아메리카노");
        assertThat(menus.get(1).getName()).isEqualTo("카페라떼");
    }
}
