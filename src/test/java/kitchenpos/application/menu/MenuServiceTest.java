package kitchenpos.application.menu;


import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuServiceTest {
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    @Nested
    @DisplayName("메뉴 등록")
    class CreateMenu {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            UUID menuGroupId = UUID.randomUUID();
            MenuGroup menuGroup = MenuGroupFixture.menuGroup("음료");
            menuGroup.setId(menuGroupId);

            Product cola = ProductFixture.product("콜라", 1000);
            cola.setId(UUID.randomUUID());

            Menu request = MenuFixture.menu("콜라", 1000, menuGroupId);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(cola.getId());
            menuProduct.setQuantity(1);
            request.setMenuProducts(List.of(menuProduct));

            when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(cola));
            when(productRepository.findById(cola.getId())).thenReturn(Optional.of(cola));
            when(purgomalumClient.containsProfanity(any())).thenReturn(false);

            // when
            Menu created = menuService.create(request);

            // then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("콜라");
            assertThat(created.getPrice()).isEqualByComparingTo("1000");
        }

        @Test
        @DisplayName("메뉴 가격이 0원 미만이면 실패")
        void failWithNegativePrice() {
            Menu request = MenuFixture.menu("콜라", -1000, UUID.randomUUID());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 그룹이 존재하지 않으면 실패")
        void failWithNonExistentMenuGroup() {
            Menu request = MenuFixture.menu("콜라", 1000, UUID.randomUUID());

            when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴 상품이 비어있으면 실패")
        void failWithEmptyMenuProducts() {
            UUID menuGroupId = UUID.randomUUID();
            MenuGroup menuGroup = MenuGroupFixture.menuGroup("음료");
            when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(menuGroup));

            Menu request = MenuFixture.menu("콜라", 1000, menuGroupId);
            request.setMenuProducts(List.of());


            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품이 등록되어 있지 않으면 실패")
        void failWithNonExistentProduct() {
            UUID menuGroupId = UUID.randomUUID();
            MenuGroup menuGroup = MenuGroupFixture.menuGroup("음료");
            when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(menuGroup));

            Menu request = MenuFixture.menu("콜라", 1000, menuGroupId);

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(UUID.randomUUID());
            menuProduct.setQuantity(1);
            request.setMenuProducts(List.of(menuProduct));

            when(productRepository.findAllByIdIn(any())).thenReturn(List.of());

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 상품 수량이 0개 미만이면 실패")
        void failWithNegativeQuantity() {
            UUID menuGroupId = UUID.randomUUID();
            MenuGroup menuGroup = MenuGroupFixture.menuGroup("음료");
            when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(menuGroup));

            Product cola = ProductFixture.product("콜라", 1000);
            cola.setId(UUID.randomUUID());

            Menu request = MenuFixture.menu("콜라", 1000, menuGroupId);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(cola.getId());
            menuProduct.setQuantity(-1);
            request.setMenuProducts(List.of(menuProduct));

            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(cola));

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 가격이 구성 상품 가격 총합보다 크면 실패")
        void failWithPriceGreaterThanSum() {
            UUID menuGroupId = UUID.randomUUID();
            MenuGroup menuGroup = MenuGroupFixture.menuGroup("음료");
            when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(menuGroup));

            Product cola = ProductFixture.product("콜라", 1000);
            cola.setId(UUID.randomUUID());

            Menu request = MenuFixture.menu("콜라", 2000, menuGroupId);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(cola.getId());
            menuProduct.setQuantity(1);
            request.setMenuProducts(List.of(menuProduct));

            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(cola));
            when(productRepository.findById(any())).thenReturn(Optional.of(cola));
            when(purgomalumClient.containsProfanity(any())).thenReturn(false);

            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 이름에 비속어가 포함되면 실패")
        void failWithProfanity() {
            // given
            UUID menuGroupId = UUID.randomUUID();
            MenuGroup menuGroup = MenuGroupFixture.menuGroup("음료");
            when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(menuGroup));

            Product product = ProductFixture.product("콜라", 1000);
            product.setId(UUID.randomUUID());
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);

            Menu request = MenuFixture.menu("비속어", 1000, menuGroupId);
            request.setMenuProducts(List.of(menuProduct));

            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));
            when(purgomalumClient.containsProfanity(any())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 가격 변경")
    class ChangePrice {
        @Test
        @DisplayName("성공")
        void success() {
            Menu menu = MenuFixture.menu("콜라", 1000, UUID.randomUUID());
            Product cola = ProductFixture.product("콜라", 1000);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(cola);
            menuProduct.setQuantity(1);
            menu.setMenuProducts(List.of(menuProduct));
            menuRepository.save(menu);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(1000));

            Menu updated = menuService.changePrice(menu.getId(), request);

            assertThat(updated.getPrice()).isEqualByComparingTo("1000");
        }

        @Test
        @DisplayName("가격이 0원 미만이면 실패")
        void failWithNegativePrice() {
            Menu menu = MenuFixture.menu("콜라", 1000, UUID.randomUUID());
            menuRepository.save(menu);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(-1000));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("가격이 구성 상품 가격 총합보다 크면 실패")
        void failWithPriceGreaterThanSum() {
            Menu menu = MenuFixture.menu("콜라", 1000, UUID.randomUUID());
            Product cola = ProductFixture.product("콜라", 1000);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(cola);
            menuProduct.setQuantity(1);
            menu.setMenuProducts(List.of(menuProduct));
            menuRepository.save(menu);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(2000));

            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 노출")
    class DisplayMenu {
        @Test
        @DisplayName("성공")
        void success() {
            Menu menu = MenuFixture.menu("콜라", 1000, UUID.randomUUID());
            Product cola = ProductFixture.product("콜라", 1000);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(cola);
            menuProduct.setQuantity(1);
            menu.setMenuProducts(List.of(menuProduct));
            menu.setDisplayed(false);
            menuRepository.save(menu);

            Menu displayed = menuService.display(menu.getId());

            assertThat(displayed.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("메뉴 가격이 구성 상품 가격 총합보다 크면 실패")
        void failWithPriceGreaterThanSum() {
            Menu menu = MenuFixture.menu("콜라", 2000, UUID.randomUUID());
            Product cola = ProductFixture.product("콜라", 1000);
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(cola);
            menuProduct.setQuantity(1);
            menu.setMenuProducts(List.of(menuProduct));
            menu.setDisplayed(false);
            menuRepository.save(menu);

            assertThatThrownBy(() -> menuService.display(menu.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 숨김")
    class HideMenu {
        @Test
        @DisplayName("성공")
        void success() {
            Menu menu = MenuFixture.menu("콜라", 1000, UUID.randomUUID());
            menu.setDisplayed(true);
            menuRepository.save(menu);

            Menu hidden = menuService.hide(menu.getId());

            assertThat(hidden.isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 메뉴는 숨길 수 없다")
        void failWithNonExistentMenu() {
            assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    @DisplayName("메뉴 목록 조회")
    void findAll() {
        Menu cola = MenuFixture.menu("콜라", 1000, UUID.randomUUID());
        Menu sprite = MenuFixture.menu("사이다", 1000, UUID.randomUUID());
        menuRepository.save(cola);
        menuRepository.save(sprite);

        List<Menu> menus = menuService.findAll();

        assertThat(menus).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("콜라", "사이다");
    }
}