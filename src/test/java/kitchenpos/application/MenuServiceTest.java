package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    private Product defaultProduct;
    private MenuGroup defaultMenuGroup;
    private MenuProduct defaultMenuProduct;

    @BeforeEach
    void setUp() {
        menuRepository = mock(MenuRepository.class);
        menuGroupRepository = mock(MenuGroupRepository.class);
        productRepository = mock(ProductRepository.class);
        purgomalumClient = mock(PurgomalumClient.class);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

        defaultProduct = createProduct("멕시칸 치킨", BigDecimal.valueOf(15000));
        defaultMenuGroup = createMenuGroup("치킨메뉴");

        defaultMenuProduct = new MenuProduct();
        defaultMenuProduct.setProduct(defaultProduct);
        defaultMenuProduct.setProductId(defaultProduct.getId());
        defaultMenuProduct.setQuantity(1);
    }

    @Nested
    @DisplayName("메뉴 생성")
    class CreateMenu {

        @Test
        @DisplayName("메뉴를 생성한다")
        void create() {
            // given
            Menu request = new Menu();
            request.setName("콜라");
            request.setPrice(BigDecimal.valueOf(3000));
            request.setMenuGroupId(defaultMenuGroup.getId());
            request.setDisplayed(true);
            request.setMenuProducts(List.of(defaultMenuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of(defaultProduct));
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));
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
            assertThat(created.getName()).isEqualTo("콜라");
            assertThat(created.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(3000));
            assertThat(created.getMenuGroup()).isEqualTo(defaultMenuGroup);
            assertThat(created.isDisplayed()).isTrue();
            assertThat(created.getMenuProducts()).hasSize(1);
        }

        @Test
        @DisplayName("메뉴명은 비어있으면 안된다.")
        void create_fail_no_name() {
            // given
            Menu request = MenuFixture.createDisplayedMenu(null, BigDecimal.valueOf(15000), defaultMenuGroup.getId(), List.of(defaultMenuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of(defaultProduct));
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));
            given(purgomalumClient.containsProfanity(request.getName()))
                .willReturn(false);
            given(menuRepository.save(any(Menu.class)))
                .willAnswer(invocation -> {
                    Menu saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 가격은 0원 이상이여야 한다.")
        void create_fail_no_price() {
            // given
            Menu request = createDisplayedMenu("멕시칸 치킨", BigDecimal.valueOf(-1), defaultMenuGroup.getId(), List.of(defaultMenuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of(defaultProduct));
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));
            given(purgomalumClient.containsProfanity(request.getName()))
                .willReturn(false);
            given(menuRepository.save(any(Menu.class)))
                .willAnswer(invocation -> {
                    Menu saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴는 메뉴 그룹을 설정해줘야 한다.")
        void create_fail_no_menuGroup() {
            // given
            Menu request = createDisplayedMenu("치킨", BigDecimal.valueOf(15000));

            given(menuGroupRepository.findById(any()))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴 생성 시, 존재하는 상품이여야 한다.")
        void create_fail_non_exist_product() {
            // given
            Menu request = MenuFixture.createDisplayedMenu("멕시칸", defaultProduct.getPrice(), defaultMenuGroup.getId(), List.of(defaultMenuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of());
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격은 메뉴 상품 가격의 총합 보다 높을 수 없다.")
        void create_fail_total_productPrice() {
            // given
            BigDecimal exceededPrice = defaultProduct.getPrice().add(BigDecimal.valueOf(2000));
            Menu request = MenuFixture.createDisplayedMenu("멕시칸", exceededPrice, defaultMenuGroup.getId(), List.of(defaultMenuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of(defaultProduct));
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 상품들의 수량은 0개 이상이여야 한다.")
        void create_fail_invalid_product_quantity() {
            // given
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(defaultProduct.getId());
            menuProduct.setQuantity(0);

            Menu request = MenuFixture.createDisplayedMenu("멕시칸", defaultProduct.getPrice(), defaultMenuGroup.getId(), List.of(menuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of(defaultProduct));
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));

            // when & then
            assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 이름은 비속어가 포함될 수 없다.")
        void create_fail_purgomalum_menu_name() {
            // given
            Menu request = MenuFixture.createDisplayedMenu("비속어", defaultProduct.getPrice(), defaultMenuGroup.getId(), List.of(defaultMenuProduct));

            given(menuGroupRepository.findById(defaultMenuGroup.getId()))
                .willReturn(Optional.of(defaultMenuGroup));
            given(productRepository.findAllByIdIn(List.of(defaultProduct.getId())))
                .willReturn(List.of(defaultProduct));
            given(productRepository.findById(defaultProduct.getId()))
                .willReturn(Optional.of(defaultProduct));
            given(purgomalumClient.containsProfanity(request.getName()))
                .willReturn(true);

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
            BigDecimal actualChangedPrice = defaultProduct.getPrice().subtract(BigDecimal.valueOf(2000));
            Menu menu = createMenu(actualChangedPrice, List.of(defaultMenuProduct), true);

            Menu request = new Menu();
            request.setPrice(actualChangedPrice);

            given(menuRepository.findById(menu.getId()))
                .willReturn(Optional.of(menu));

            // when
            Menu updated = menuService.changePrice(menu.getId(), request);

            // then
            assertThat(updated.getPrice()).isEqualByComparingTo(actualChangedPrice);
        }

        @DisplayName("메뉴의 가격을 변경 시 가격에 따른 실패")
        @ParameterizedTest(name = "변경할 메뉴 가격 : `{0}`")
        @ValueSource(ints = {-1, 17000})
        void changePrice_fail_illegal_price(final int price) {
            // given
            BigDecimal actualChangedPrice = defaultProduct.getPrice().add(BigDecimal.valueOf(2000));
            Menu menu = createMenu(actualChangedPrice, List.of(defaultMenuProduct), true);

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(price));

            given(menuRepository.findById(menu.getId()))
                .willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 전시 상태 변경")
    class DisplayOnOff {
        @Test
        @DisplayName("메뉴 전시 상태 ON 하기")
        void display() {
            // given
            Menu menu = createMenu(defaultProduct.getPrice(), List.of(defaultMenuProduct), false);

            given(menuRepository.findById(menu.getId()))
                .willReturn(Optional.of(menu));

            // when
            Menu displayed = menuService.display(menu.getId());

            // then
            assertThat(displayed.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("메뉴의 가격은 메뉴 상품 가격의 총합 보다 높을 수 없다.")
        void display_fail_total_productPrice() {
            // given
            UUID menuId = UUID.randomUUID();

            BigDecimal exceededPrice = defaultProduct.getPrice().add(BigDecimal.valueOf(2000));
            Menu menu = createMenu(exceededPrice, List.of(defaultMenuProduct), true);

            given(menuRepository.findById(menuId))
                .willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> menuService.display(menuId))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("메뉴 전시 상태 OFF 하기")
        void hide() {
            // given
            Menu menu = createMenu(true);

            given(menuRepository.findById(menu.getId()))
                .willReturn(Optional.of(menu));

            // when
            Menu hidden = menuService.hide(menu.getId());

            // then
            assertThat(hidden.isDisplayed()).isFalse();
        }
    }

    @Test
    @DisplayName("메뉴 전부 가져올 수 있다.")
    void findAll() {
        // given
        Menu menu1 = MenuFixture.createDisplayedMenu("멕시칸치킨", BigDecimal.valueOf(18000));
        Menu menu2 = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(23000));

        given(menuRepository.findAll())
            .willReturn(List.of(menu1, menu2));

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertThat(menus).hasSize(2);
        assertThat(menus.get(0).getName()).isEqualTo(menu1.getName());
        assertThat(menus.get(1).getName()).isEqualTo(menu2.getName());
    }
}
