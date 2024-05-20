package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        purgomalumClient = new FakePurgomalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("메뉴 생성")
    class create {

        private MenuGroup menuGroup;
        private Product product;
        private MenuProduct menuProduct;

        @BeforeEach
        void setUp() {
            menuGroup = MenuGroupTestFixture.createMenuGroup("치킨류");
            menuGroupRepository.save(menuGroup);
            product = ProductTestFixture.createProduct("후라이드치킨", 18000L);
            productRepository.save(product);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        }

        @Test
        @DisplayName("메뉴생성 성공")
        void success() {
            //given
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(menuProduct));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());

            //when
            Menu response = menuService.create(request);

            //then
            assertThat(request.getName()).isEqualTo(response.getName());
            assertThat(request.isDisplayed()).isEqualTo(response.isDisplayed());
        }

        @Test
        @DisplayName("메뉴가격은 0이상만 가능하다.")
        void canNotPriceNullOrMinus() {
            //given
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", -1000L, true, List.of(menuProduct));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());

            //when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);

            request.setPrice(null);
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);

        }

        @Test
        @DisplayName("유효한 메뉴그룹이 없으면 메뉴를 생성할 수 없다.")
        void canNotCreateWhenNoMenuGroup() {
            //given
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(menuProduct));
            request.setMenuGroup(new MenuGroup());
            request.setMenuGroupId(UUID.randomUUID());

            //when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("메뉴상품이 없으면 메뉴를 생성할 수 없다.")
        void canNotCreateWhenNoMenuProduct() {
            //given
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, null);
            Menu request2 = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(new MenuProduct()));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());
            request2.setMenuGroup(menuGroup);
            request2.setMenuGroupId(menuGroup.getId());

            //when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> menuService.create(request2))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴상품에는 상품이 중복되어 올 수 없다.")
        void canNotDuplicateProductInMenuProduct() {
            //given
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(menuProduct, menuProduct));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());

            //when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }


        @Test
        @DisplayName("메뉴상품에는 유효한 상품만 있어야 한다.")
        void onlyValidProductInMenuProduct() {

            //given
            Product product2 = ProductTestFixture.createProduct("없는치킨", 18000L);
            MenuProduct menuProduct2 = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product2);
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(menuProduct, menuProduct, menuProduct2));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());

            //when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);

        }


        @Test
        @DisplayName("메뉴상품의 상품 개수가 음수일 수 없다.")
        void canNotMinusQuantity() {
            //given
            menuProduct.setQuantity(-1L);
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(menuProduct));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());

            //when then
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격은 메뉴상품들의 가격의 합보다 낮아야 한다.")
        void MenuPriceLower() {
            //given
            menuProduct.getProduct().setPrice(BigDecimal.valueOf(15000));
            Menu request = MenuTestFixture.createMenuRequest("후라이드치킨", 18000L, true, List.of(menuProduct));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());


            //when
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 이름에 비속어가 있어서는 안된다.")
        void canNotUseProfanity() {
            //given
            Menu request = MenuTestFixture.createMenuRequest("비속어치킨", 18000L, true, List.of(menuProduct));
            request.setMenuGroup(menuGroup);
            request.setMenuGroupId(menuGroup.getId());
            //when
            assertThatThrownBy(() -> menuService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴가격 변경")
    class changePrice {

        private MenuGroup menuGroup;
        private Product product;
        private MenuProduct menuProduct;
        private Menu menu;

        @BeforeEach
        void setUp() {
            menuGroup = MenuGroupTestFixture.createMenuGroup("치킨류");
            menuGroupRepository.save(menuGroup);
            product = ProductTestFixture.createProduct("후라이드치킨", 18000L);
            productRepository.save(product);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
            menu = MenuTestFixture.createMenu("후라이드치킨", 18000L, true, List.of(menuProduct));
            menuRepository.save(menu);
        }

        @Test
        @DisplayName("메뉴가격 변경 성공")
        void success() {
            //given
            menu.setPrice(BigDecimal.valueOf(17000));

            //when
            Menu response = menuService.changePrice(menu.getId(), menu);

            //then
            assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
        }

        @Test
        @DisplayName("없는 메뉴의 가격을 변경할 수 없다.")
        void canNotChangeNoMenu() {
            //given
            menu.setPrice(BigDecimal.valueOf(17000));
            menu.setId(UUID.randomUUID());

            //when then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("변경한 메뉴 가격이 메뉴상품들 가격의 합보다 커서는 안된다.")
        void canNotChangePriceHigher() {
            //given
            menu.setPrice(BigDecimal.valueOf(20000));

            //when then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 표시하기")
    class display {

        private MenuGroup menuGroup;
        private Product product;
        private MenuProduct menuProduct;

        @BeforeEach
        void setUp() {
            menuGroup = MenuGroupTestFixture.createMenuGroup("치킨류");
            menuGroupRepository.save(menuGroup);
            product = ProductTestFixture.createProduct("후라이드치킨", 18000L);
            productRepository.save(product);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);

        }

        @Test
        @DisplayName("메뉴 표시하기 성공")
        void success() {
            // given
            Menu request = MenuTestFixture.createMenu("후라이드치킨", 18000L, false, List.of(menuProduct));
            menuRepository.save(request);
            // when
            Menu response = menuService.display(request.getId());

            // then
            assertThat(response.isDisplayed()).isTrue();
        }

        @Test
        @DisplayName("메뉴의 금액이 메뉴상품들 가격의 합보다 크면 표시할수 없다.")
        void canNotDisplayHigherPrice() {
            // given
            Menu request = MenuTestFixture.createMenu("후라이드치킨", 30000L, false, List.of(menuProduct));
            menuRepository.save(request);

            // when then
            assertThatThrownBy(() -> menuService.display(request.getId()))
                    .isExactlyInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("유효하지 않는 메뉴를 표시할 수 없다.")
        void canNotDisplayNoMenu() {
            // given
            Menu request = MenuTestFixture.createMenu("후라이드치킨", 30000L, false, List.of(menuProduct));

            // when then
            assertThatThrownBy(() -> menuService.hide(request.getId()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 숨기기")
    class hide {

        private MenuGroup menuGroup;
        private Product product;
        private MenuProduct menuProduct;

        @BeforeEach
        void setUp() {
            menuGroup = MenuGroupTestFixture.createMenuGroup("치킨류");
            menuGroupRepository.save(menuGroup);
            product = ProductTestFixture.createProduct("후라이드치킨", 18000L);
            productRepository.save(product);
            menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);

        }

        @Test
        @DisplayName("메뉴 표시하기 성공")
        void success() {
            // given
            Menu request = MenuTestFixture.createMenu("후라이드치킨", 18000L, true, List.of(menuProduct));
            menuRepository.save(request);
            // when
            Menu response = menuService.hide(request.getId());

            // then
            assertThat(response.isDisplayed()).isFalse();
        }

        @Test
        @DisplayName("유효하지 않는 메뉴를 숨길 수 없다.")
        void canNotHideNoMenu() {
            // given
            Menu request = MenuTestFixture.createMenu("후라이드치킨", 30000L, true, List.of(menuProduct));

            // when then
            assertThatThrownBy(() -> menuService.hide(request.getId()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 조회")
    class find {
        @Test
        @DisplayName("모든 메뉴 조회")
        void findAll() {

            //given
            Menu menu1 = MenuTestFixture.createMenu("menu1", 10000L, true, List.of(new MenuProduct()));
            Menu menu2 = MenuTestFixture.createMenu("menu2", 14000L, true, List.of(new MenuProduct()));

            menuRepository.save(menu1);
            menuRepository.save(menu2);

            //when
            List<Menu> response = menuService.findAll();

            //then
            assertThat(response).hasSize(2);
            assertThat(response)
                    .filteredOn(Menu::getId, menu1.getId())
                    .containsExactly(menu1);
            assertThat(response)
                    .filteredOn(Menu::getId, menu2.getId())
                    .containsExactly(menu2);
        }
    }

}