package kitchenpos.application.menu;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@DisplayName("Application: 메뉴 서비스 테스트")
@ExtendWith(MockitoExtension.class)
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

    private MenuGroup menuGroup;
    private Product product_1;
    private Product product_2;
    private MenuProduct menuProduct_1;
    private MenuProduct menuProduct_2;
    private List<MenuProduct> menuProducts;
    private Menu menu;

    @BeforeEach
    void setUp() {
        menuGroup = MenuTestFixture.aMenuGroup();

        product_1 = MenuTestFixture.aProduct("레드 반마리 순살", 12000L);
        product_2 = MenuTestFixture.aProduct("허니 반마리 순살", 12000L);

        menuProduct_1 = MenuTestFixture.aMenuProduct(1L, product_1, 1L);
        menuProduct_2 = MenuTestFixture.aMenuProduct(2L, product_2, 1L);
        menuProducts = List.of(menuProduct_1, menuProduct_2);

        menu = MenuTestFixture.aMenu("레허반반순살", 24000L, menuGroup, menuProducts);
    }


    @Test
    @DisplayName("메뉴를 등록 할 수 있다.")
    void menu_create() {
        // given
        Menu response = MenuTestFixture.aMenu("레허반반순살", 24000L, menuGroup, menuProducts);
        List<UUID> productIds = List.of(product_1.getId(), product_2.getId());

        // when

        //1. 메뉴그룹 조회
        when(menuGroupRepository.findById(menu.getMenuGroupId()))
                .thenReturn(Optional.ofNullable(menuGroup));

        //2. 상품 조회
        when(productRepository.findAllByIdIn(productIds))
                .thenReturn(List.of(product_1, product_2));

        //3. 메뉴 프로턱트 조회
        menuProducts.forEach(menuProduct -> {
            when(productRepository.findById(menuProduct.getProductId()))
                    .thenReturn(Optional.ofNullable(menuProduct.getProduct()));
        });

        when(menuRepository.save(any(Menu.class))).thenReturn(response);

        Menu created = menuService.create(menu);

        // then
        assertEquals(created.getId(), response.getId());
        assertEquals(created.getName(), response.getName());
        assertEquals(created.getPrice(), response.getPrice());
        assertEquals(created.getMenuGroup(), response.getMenuGroup());
    }

    @Nested
    @DisplayName("메뉴를 등록에 실패 할 수 있다.")
    class menu_create_fail_case {

        @ParameterizedTest(name = "{index}: {0}")
        @DisplayName("가격이 null 이거나 0 미만일 경우")
        @NullSource
        @ValueSource(longs = {-1L})
        void create_1(Long price) {
            // given
            Menu menu_without_menu_product = MenuTestFixture.aMenuJustPrice("레허반반순살", price);

            // when & then
            assertThrows(IllegalArgumentException.class,
                    () -> menuService.create(menu_without_menu_product));
        }

        @Test
        @DisplayName("메뉴그룹이 없는 경우")
        void create_2() {
            // given
            Menu menu_without_menu_group = MenuTestFixture.aMenuWithOutMenuGroup("레허반반순살", 24000L, menuProducts);

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> menuService.create(menu_without_menu_group));
        }

        @Test
        @DisplayName("상품메뉴가 없는 경우")
        void create_3() {
            // given
            Menu menu_without_menu_product = MenuTestFixture.aMenu("레허반반순살", 24000L, menuGroup, List.of());

            //1. 메뉴그룹 조회
            when(menuGroupRepository.findById(menu.getMenuGroupId()))
                    .thenReturn(Optional.ofNullable(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class,
                    () -> menuService.create(menu_without_menu_product));
        }

        @Test
        @DisplayName("가격이 음수일 경우")
        void create_4() {
            // given
            List<UUID> productIds = List.of(product_1.getId());
            MenuProduct menuProduct = MenuTestFixture.aMenuProduct(1L, product_1, -1L);
            Menu menu_without_menu_product = MenuTestFixture.aMenu("레허반반순살", 24000L, menuGroup, List.of(menuProduct));

            //1. 메뉴그룹 조회
            when(menuGroupRepository.findById(menu.getMenuGroupId()))
                    .thenReturn(Optional.ofNullable(menuGroup));

            //2. 상품 조회
            when(productRepository.findAllByIdIn(productIds))
                    .thenReturn(List.of(product_1));

            // when & then
            IllegalArgumentException assertThrows = assertThrows(IllegalArgumentException.class,
                    () -> menuService.create(menu_without_menu_product));

            System.out.println(assertThrows.getMessage());
            assertEquals(assertThrows.getMessage(), "수량은 0보다 작을 수 없습니다.");
        }

        @Test
        @DisplayName("총 가격이 총 상품 가격보다 작을 경우")
        void create_5() {
            // given
            List<UUID> productIds = List.of(product_1.getId());
            MenuProduct menuProduct = MenuTestFixture.aMenuProduct(1L, product_1, 1L);
            Menu menu_without_menu_product = MenuTestFixture.aMenu("레허반반순살", 24000L, menuGroup, List.of(menuProduct));

            // when
            //1. 메뉴그룹 조회
            when(menuGroupRepository.findById(menu.getMenuGroupId()))
                    .thenReturn(Optional.ofNullable(menuGroup));

            //2. 상품 조회
            when(productRepository.findAllByIdIn(productIds))
                    .thenReturn(List.of(product_1));

            //3. 메뉴 프로턱트 조회
            when(productRepository.findById(menuProduct.getProductId()))
                    .thenReturn(Optional.ofNullable(menuProduct.getProduct()));

            //  then
            assertThrows(IllegalArgumentException.class,
                    () -> menuService.create(menu_without_menu_product));
        }

        @ParameterizedTest(name = "{index}: {0}")
        @DisplayName("메뉴명이 null이거나 적합하지 않을 경우")
        @NullSource
        @ValueSource(strings = {"파인애플올린피자", "민트초코시카고피자"})
        void create_6(String menuName) {
            // given
            List<UUID> productIds = List.of(product_1.getId(), product_2.getId());
            Menu not_good_name_menu = MenuTestFixture.aMenu(menuName, 24000L, menuGroup, menuProducts);
            // when

            //1. 메뉴그룹 조회
            when(menuGroupRepository.findById(menu.getMenuGroupId()))
                    .thenReturn(Optional.ofNullable(menuGroup));

            //2. 상품 조회
            when(productRepository.findAllByIdIn(productIds))
                    .thenReturn(List.of(product_1, product_2));

            //3. 메뉴 프로턱트 조회
            menuProducts.forEach(menuProduct -> when(productRepository.findById(menuProduct.getProductId()))
                    .thenReturn(Optional.ofNullable(menuProduct.getProduct())));

            lenient().when(purgomalumClient.containsProfanity(menuName)).thenReturn(true);

            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> menuService.create(not_good_name_menu));
        }

    }

    @Nested
    @DisplayName("메뉴의 가격을 수정할 수 있다.")
    class changePrice {
        @Test
        @DisplayName("수정할 수 있다.")
        void case_1() {
            //given
            BigDecimal beforePrice = menu.getPrice(); //수정 전 가격
            Menu aMenu = MenuTestFixture.aMenuJustPrice("레허반반순살", 23000L);
            UUID id = menu.getId();

            //when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
            Menu changedMenu = menuService.changePrice(id, aMenu);
            BigDecimal afterPrice = changedMenu.getPrice();

            //then
            assertEquals(changedMenu.getId(), menu.getId());
            assertEquals(changedMenu.getName(), menu.getName());
            assertEquals(changedMenu.getPrice(), aMenu.getPrice());
            assertNotEquals(beforePrice, afterPrice);
        }

        @Test
        @DisplayName("수정할 가격이 입력되지 않으면 IllegalArgumentException이 발생한다.")
        void case_2() {
            //given
            Menu aMenu = MenuTestFixture.aMenuJustPrice("레허반반순살", null);
            UUID id = menu.getId();

            //when && then
            assertThrows(IllegalArgumentException.class,
                    () -> menuService.changePrice(id, aMenu));
        }

        @Test
        @DisplayName("수정 대상 상품이 없을 경우 NoSuchElementException가 발생한다.")
        void case_3() {
            //given
            Menu aMenu = MenuTestFixture.aMenuJustPrice("레허반반순살", 25000L);
            UUID id = menu.getId();

            //when
            when(menuRepository.findById(id)).thenReturn(Optional.empty());

            //then
            assertThrows(NoSuchElementException.class,
                    () -> menuService.changePrice(id, aMenu));
        }

        @Test
        @DisplayName("메뉴상품의 구성 가격보다 비쌀 경우 IllegalArgumentException이 발생한다.")
        void case_4() {
            //given
            Menu aMenu = MenuTestFixture.aMenuJustPrice("레허반반순살", 25000L);
            UUID id = menu.getId();

            //when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));

            //then
            assertThrows(IllegalArgumentException.class,
                    () -> menuService.changePrice(id, aMenu));
        }
    }

    @Nested
    @DisplayName("메뉴를 공개 할 수 있다.")
    class display {
        @Test
        @DisplayName("메뉴를 공개 할 수 있다.")
        void case_1() {
            // given
            UUID id = menu.getId();

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
            Menu displayed = menuService.display(id);

            // then
            assertEquals(displayed.getId(), menu.getId());
            assertEquals(displayed.getName(), menu.getName());
            assertEquals(displayed.getPrice(), menu.getPrice());
            assertEquals(displayed.getMenuGroup(), menu.getMenuGroup());
            assertEquals(displayed.getMenuProducts(), menu.getMenuProducts());
            assertTrue(displayed.isDisplayed());
        }

        @Test
        @DisplayName("동일한 상태로 변경할 경우 문제없이 변결될 수 있다")
        void case_2() {
            // given
            UUID id = menu.getId();
            ReflectionTestUtils.setField(menu, "displayed", true);

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
            Menu displayed = menuService.display(id);

            // then
            assertEquals(displayed.getId(), menu.getId());
            assertEquals(displayed.getName(), menu.getName());
            assertEquals(displayed.getPrice(), menu.getPrice());
            assertEquals(displayed.getMenuGroup(), menu.getMenuGroup());
            assertEquals(displayed.getMenuProducts(), menu.getMenuProducts());
            assertTrue(displayed.isDisplayed());
        }

        @Test
        @DisplayName("메뉴가 존재하지 않을 경우 NoSuchElementException이 발생한다.")
        void case_3() {
            // given
            UUID id = menu.getId();

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.empty());

            // then
            assertThrows(NoSuchElementException.class,
                    () -> menuService.display(id));
        }

        @Test
        @DisplayName("메뉴상품의 총합 가격이 메뉴의 가격보다 작을 경우 IllegalStateException이 발생한다.")
        void case_4() {
            // given
            UUID id = menu.getId();
            ReflectionTestUtils.setField(menu, "price", BigDecimal.valueOf(25000L));

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));

            // then
            assertThrows(IllegalStateException.class,
                    () -> menuService.display(id));
        }
    }

    @Nested
    @DisplayName("메뉴를 비공개 할 수 있다.")
    class hide {

        @Test
        @DisplayName("메뉴를 비공개 할 수 있다.")
        void case_1() {
            // given
            UUID id = menu.getId();

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
            Menu hidden = menuService.hide(id);

            // then
            assertEquals(hidden.getId(), menu.getId());
            assertEquals(hidden.getName(), menu.getName());
            assertEquals(hidden.getPrice(), menu.getPrice());
            assertEquals(hidden.getMenuGroup(), menu.getMenuGroup());
            assertEquals(hidden.getMenuProducts(), menu.getMenuProducts());
            assertFalse(hidden.isDisplayed());
        }

        @Test
        @DisplayName("동일한 상태로 변경할 경우 문제없이 변결될 수 있다")
        void case_2() {
            // given
            UUID id = menu.getId();
            ReflectionTestUtils.setField(menu, "displayed", false);

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.of(menu));
            Menu hidden = menuService.hide(id);

            // then
            assertEquals(hidden.getId(), menu.getId());
            assertEquals(hidden.getName(), menu.getName());
            assertEquals(hidden.getPrice(), menu.getPrice());
            assertEquals(hidden.getMenuGroup(), menu.getMenuGroup());
            assertEquals(hidden.getMenuProducts(), menu.getMenuProducts());
            assertFalse(hidden.isDisplayed());
        }

        @Test
        @DisplayName("메뉴가 존재하지 않을 경우 NoSuchElementException이 발생한다.")
        void case_3() {
            // given
            UUID id = menu.getId();

            // when
            when(menuRepository.findById(id)).thenReturn(Optional.empty());

            // then
            assertThrows(NoSuchElementException.class,
                    () -> menuService.hide(id));
        }
    }
}
