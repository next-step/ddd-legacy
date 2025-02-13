package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.util.MenuBuilder;
import kitchenpos.util.MenuGroupBuilder;
import kitchenpos.util.MenuProductBuilder;
import kitchenpos.util.ProductBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    MenuService menuService;

    @Mock
    MenuRepository menuRepository;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    PurgomalumClient purgomalumClient;

    @DisplayName("메뉴 생성")
    @Nested
    class MenuCreate {

        @DisplayName("메뉴의 가격이 null이먄 예외를 던진다.")
        @Test
        void if_price_is_null_then_throw_exception() {
            // given
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(null)
                    .build();

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 가격이 음수이면 예외를 던진다.")
        @Test
        void if_price_is_negative_then_throw_exception() {
            // given
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(-1))
                    .build();

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 그룹이 없으면 예외를 던진다.")
        @Test
        void if_menu_group_is_null_then_throw_exception() {
            // given
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(null)
                    .build();

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 메뉴 상품 목록이 없으면 예외를 던진다.")
        @Test
        void if_menu_products_is_null_then_throw_exception() {
            // given
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 메뉴 상품 목록이 비어 있으면 예외를 던진다.")
        @Test
        void if_menu_products_is_empty_then_throw_exception() {
            // given
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of())
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("각 메뉴 상품의 상품이 하나라도 없으면 예외를 던진다.")
        @Test
        void if_any_product_is_null_then_throw_exception() {
            // given
            var product1 = ProductBuilder.id(UUID.randomUUID())
                    .build();
            var menuProduct1 = MenuProductBuilder
                    .productId(product1.getId())
                    .product(product1)
                    .build();
            var product2 = ProductBuilder.id(UUID.randomUUID())
                    .build();
            var menuProduct2 = MenuProductBuilder.productId(product2.getId())
                    .product(product2)
                    .build();
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct1, menuProduct2))
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(product1.getId(), product2.getId())))
                    .willReturn(List.of(menuProduct1.getProduct()));

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품의 개수가 음수이면 예외를 던진다.")
        @Test
        void if_product_quantity_is_negative_then_throw_exception() {
            // given
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var product = ProductBuilder.id(UUID.randomUUID())
                    .build();
            var menuProduct = MenuProductBuilder.productId(product.getId())
                    .product(product)
                    .quantity(-1)
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct))
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));

            given(productRepository.findAllByIdIn(List.of(product.getId()))).willReturn(List.of(product));

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품이 존재하지 않으면 예외를 던진다.")
        @Test
        void if_product_does_not_exist_then_throw_exception() {
            // given
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var product = ProductBuilder.id(UUID.randomUUID())
                    .build();
            var menuProduct = MenuProductBuilder.productId(product.getId())
                    .product(product)
                    .quantity(1)
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct))
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(product.getId())))
                    .willReturn(List.of(product));
            given(productRepository.findById(product.getId()))
                    .willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 가격이 상품의 가격과 개수의 곱의 합보다 크면 예외를 던진다.")
        @ParameterizedTest
        @CsvSource(value = {"3,1,1,1,1", "11,1,2,2,4"})
        void if_price_is_greater_than_sum_of_product_price_and_quantity_then_throw_exception(
                int menuPrice, int product1Price, int product1Quantity, int product2Price, int product2Quantity
        ) {
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var product1 = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(product1Price))
                    .build();
            var menuProduct1 = MenuProductBuilder.productId(product1.getId())
                    .product(product1)
                    .quantity(product1Quantity)
                    .build();
            var product2 = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(product2Price))
                    .build();
            var menuProduct2 = MenuProductBuilder.productId(product2.getId())
                    .product(product2)
                    .quantity(product2Quantity)
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(menuPrice))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct1, menuProduct2))
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(product1.getId(), product2.getId())))
                    .willReturn(List.of(product1, product2));
            given(productRepository.findById(product1.getId()))
                    .willReturn(Optional.of(product1));
            given(productRepository.findById(product2.getId()))
                    .willReturn(Optional.of(product2));

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름이 null이면 예외를 던진다.")
        @Test
        void if_name_is_null_then_throw_exception() {
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var product = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .build();
            var menuProduct = MenuProductBuilder.productId(product.getId())
                    .product(product)
                    .quantity(1)
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct))
                    .name(null)
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(product.getId())))
                    .willReturn(List.of(product));
            given(productRepository.findById(product.getId()))
                    .willReturn(Optional.of(product));

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름에 부적절한 단어가 포함되어 있으면 예외를 던진다.")
        @Test
        void if_name_contains_inappropriate_word_then_throw_exception() {
            // given
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var product = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .build();
            var menuProduct = MenuProductBuilder.productId(product.getId())
                    .product(product)
                    .quantity(1)
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct))
                    .name("bad word")
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(product.getId())))
                    .willReturn(List.of(product));
            given(productRepository.findById(product.getId()))
                    .willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(request.getName()))
                    .willReturn(true);

            // when
            assertThatThrownBy(() -> menuService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 생성 성공하면 메뉴를 반환한다.")
        @Test
        void if_menu_create_is_successful_then_return_menu() {
            // given
            var menuGroup = MenuGroupBuilder.name("치킨")
                    .id(UUID.randomUUID())
                    .build();
            var product = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .build();
            var menuProduct = MenuProductBuilder.productId(product.getId())
                    .product(product)
                    .quantity(1)
                    .build();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuGroupId(menuGroup.getId())
                    .menuProducts(List.of(menuProduct))
                    .name("good word")
                    .build();

            given(menuGroupRepository.findById(menuGroup.getId()))
                    .willReturn(Optional.of(menuGroup));
            given(productRepository.findAllByIdIn(List.of(product.getId())))
                    .willReturn(List.of(product));
            given(productRepository.findById(product.getId()))
                    .willReturn(Optional.of(product));
            given(purgomalumClient.containsProfanity(request.getName()))
                    .willReturn(false);
            given(menuRepository.save(any(Menu.class)))
                    .will(invocation -> invocation.getArgument(0));

            // when
            var menu = menuService.create(request);

            // then
            assertThat(menu).isNotNull();
            Assertions.assertAll(
                    () -> assertThat(menu.getId()).isNotNull(),
                    () -> assertThat(menu.getName()).isEqualTo(request.getName()),
                    () -> assertThat(menu.getPrice()).isEqualTo(request.getPrice()),
                    () -> assertThat(menu.getMenuGroup()).isEqualTo(menuGroup),
                    () -> assertThat(menu.isDisplayed()).isFalse(), () -> assertThat(menu.getMenuProducts()).hasSize(1)
            );
        }
    }

    @DisplayName("메뉴 가격 변경")
    @Nested
    class MenuChangePrice {

        @DisplayName("메뉴의 가격이 null이면 예외를 던진다.")
        @Test
        void if_price_is_null_then_throw_exception() {
            // given
            var menuId = UUID.randomUUID();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(null)
                    .build();

            // when
            assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 가격이 음수이면 예외를 던진다.")
        @Test
        void if_price_is_negative_then_throw_exception() {
            // given
            var menuId = UUID.randomUUID();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(-1))
                    .build();

            // when
            assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 존재하지 않으면 예외를 던진다.")
        @Test
        void if_menu_does_not_exist_then_throw_exception() {
            // given
            var menuId = UUID.randomUUID();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .build();

            given(menuRepository.findById(menuId))
                    .willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 가격이 상품의 가격과 개수의 곱의 합보다 크면 예외를 던진다.")
        @ParameterizedTest
        @CsvSource(value = {"3,1,1,1,1", "11,1,2,2,4"})
        void if_price_is_greater_than_sum_of_product_price_and_quantity_then_throw_exception(
                int menuPrice, int product1Price, int product1Quantity, int product2Price, int product2Quantity
        ) {
            // given
            var menuId = UUID.randomUUID();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(menuPrice))
                    .build();
            var product1 = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(product1Price))
                    .build();
            var menuProduct1 = MenuProductBuilder.productId(product1.getId())
                    .product(product1)
                    .quantity(product1Quantity)
                    .build();
            var product2 = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(product2Price))
                    .build();
            var menuProduct2 = MenuProductBuilder.productId(product2.getId())
                    .product(product2)
                    .quantity(product2Quantity)
                    .build();
            var menu = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuProducts(List.of(menuProduct1, menuProduct2))
                    .build();

            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when
            assertThatThrownBy(() -> menuService.changePrice(menuId, request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 가격 변경 성공하면 메뉴를 반환한다.")
        @Test
        void if_menu_change_price_is_successful_then_return_menu() {
            // given
            var menuId = UUID.randomUUID();
            var request = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(0))
                    .build();
            var product = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .build();
            var menuProduct = MenuProductBuilder.productId(product.getId())
                    .product(product)
                    .quantity(1)
                    .build();
            var menu = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(2))
                    .menuProducts(List.of(menuProduct))
                    .build();

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            var changedMenu = menuService.changePrice(menuId, request);

            // then
            assertThat(changedMenu).isNotNull();
            assertThat(changedMenu.getPrice()).isEqualTo(request.getPrice());
        }
    }

    @DisplayName("메뉴 활성화")
    @Nested
    class MenuDisplay {

        @DisplayName("메뉴가 존재하지 않으면 예외를 던진다.")
        @Test
        void if_menu_does_not_exist_then_throw_exception() {
            // given
            var menuId = UUID.randomUUID();
            given(menuRepository.findById(menuId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> menuService.display(menuId))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴의 가격이 상품의 가격과 개수의 곱의 합보다 크면 예외를 던진다.")
        @ParameterizedTest
        @CsvSource(value = {"3,1,1,1,1", "11,1,2,2,4"})
        void if_price_is_greater_than_sum_of_product_price_and_quantity_then_throw_exception(
                int menuPrice, int product1Price, int product1Quantity, int product2Price, int product2Quantity
        ) {
            // given
            var menuId = UUID.randomUUID();
            var product1 = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(product1Price))
                    .build();
            var menuProduct1 = MenuProductBuilder.productId(product1.getId())
                    .product(product1)
                    .quantity(product1Quantity)
                    .build();
            var product2 = ProductBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(product2Price))
                    .build();
            var menuProduct2 = MenuProductBuilder.productId(product2.getId())
                    .product(product2)
                    .quantity(product2Quantity)
                    .build();
            var menu = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(menuPrice))
                    .menuProducts(List.of(menuProduct1, menuProduct2))
                    .build();

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            assertThatThrownBy(() -> menuService.display(menuId))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("메뉴 활성화 성공하면 메뉴를 반환한다.")
        @Test
        void if_menu_display_is_successful_then_return_menu() {
            // given
            var menuId = UUID.randomUUID();
            var menu = MenuBuilder.id(menuId)
                    .price(BigDecimal.valueOf(0))
                    .menuProducts(List.of())
                    .build();

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            var displayedMenu = menuService.display(menuId);

            // then
            assertThat(displayedMenu).isNotNull();
            assertThat(displayedMenu.isDisplayed()).isTrue();
        }
    }

    @DisplayName("메뉴 비활성화")
    @Nested
    class MenuHide {

        @DisplayName("메뉴가 존재하지 않으면 예외를 던진다.")
        @Test
        void if_menu_does_not_exist_then_throw_exception() {
            // given
            var menuId = UUID.randomUUID();
            given(menuRepository.findById(menuId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> menuService.hide(menuId))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴 비활성화 성공하면 메뉴를 반환한다.")
        @Test
        void if_menu_hide_is_successful_then_return_menu() {
            // given
            var menuId = UUID.randomUUID();
            var menu = MenuBuilder.id(menuId)
                    .price(BigDecimal.valueOf(0))
                    .menuProducts(List.of())
                    .displayed(true)
                    .build();

            given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));

            // when
            var hiddenMenu = menuService.hide(menuId);

            // then
            assertThat(hiddenMenu).isNotNull();
            assertThat(hiddenMenu.isDisplayed()).isFalse();
        }
    }

    @DisplayName("메뉴 목록 조회")
    @Nested
    class MenuFindAll {

        @DisplayName("메뉴가 있으면 메뉴 목록을 반환한다.")
        @Test
        void if_menu_exists_then_return_menu_list() {
            // given
            var menu1 = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(1))
                    .menuProducts(List.of())
                    .build();
            var menu2 = MenuBuilder.id(UUID.randomUUID())
                    .price(BigDecimal.valueOf(2))
                    .menuProducts(List.of())
                    .build();

            given(menuRepository.findAll()).willReturn(List.of(menu1, menu2));

            // when
            var menus = menuService.findAll();

            // then
            assertThat(menus).hasSize(2);
        }
    }
}

