package kitchenpos.application;

import fixtures.MenuBuilder;
import fixtures.MenuGroupBuilder;
import fixtures.MenuProductSteps;
import fixtures.ProductBuilder;
import fixtures.TestContainers;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private MenuService menuService;
    @Mock
    private PurgomalumClient purgomalumClient;
    private MenuProductSteps menuProductSteps;

    @BeforeEach
    void setUp() {
        TestContainers testContainers = new TestContainers();
        menuProductSteps = new MenuProductSteps(testContainers.menuGroupRepository, testContainers.menuRepository, testContainers.productRepository);
        menuService = new MenuService(testContainers.menuRepository, testContainers.menuGroupRepository, testContainers.productRepository, purgomalumClient);
    }

    @DisplayName("메뉴를 등록할 때")
    @Nested
    class MenuCreateTest {
        @DisplayName("이름, 가격, 표시 여부를 갖는다")
        @Test
        void createMenuTest() {

            // given
            MenuGroup menuGroup = menuProductSteps.메뉴그룹_생성한다();
            MenuProduct menuProduct = menuProductSteps.메뉴상품을_생성한다();

            Menu menu = menuService.create(new MenuBuilder().with("치킨", BigDecimal.valueOf(10_000)).withMenuGroup(menuGroup).withMenuProducts(List.of(menuProduct)).build());

            assertNotNull(menu.getName());
            assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(10_000));
            assertTrue(menu.isDisplayed());
        }


        @DisplayName("메뉴의 이름이 없으면 등록이 불가능 하다")
        @Test
        void menuNameWithBlankTest() {

            MenuGroup menuGroup = menuProductSteps.메뉴그룹_생성한다();
            MenuProduct menuProduct = menuProductSteps.메뉴상품을_생성한다();

            assertThrows(IllegalArgumentException.class, () -> menuService.create(new MenuBuilder().with(null, BigDecimal.valueOf(10_000)).withMenuGroup(menuGroup).withMenuProducts(List.of(menuProduct)).build()));
        }

        @DisplayName("메뉴의 가격이 0보다 작으면 등록이 불가능하다")
        @Test
        void menuPriceWithZeroTest() {

            MenuGroup menuGroup = menuProductSteps.메뉴그룹_생성한다();
            MenuProduct menuProduct = menuProductSteps.메뉴상품을_생성한다();

            assertThrows(IllegalArgumentException.class, () -> menuService.create(new MenuBuilder().with("치킨", BigDecimal.valueOf(-1)).withMenuGroup(menuGroup).withMenuProducts(List.of(menuProduct)).build()));
        }

        @DisplayName("메뉴그룹에 소속되지 않으면 등록이 불가능하다")
        @Test
        void menuGroupNullTest() {

            MenuProduct menuProduct = menuProductSteps.메뉴상품을_생성한다();

            assertThrows(NoSuchElementException.class, () -> menuService.create(new MenuBuilder().with("치킨", BigDecimal.valueOf(10_000)).withMenuProducts(List.of(menuProduct)).withMenuGroup(new MenuGroupBuilder().withName("메뉴그룹 없음").build()).build()));
        }
    }

    @DisplayName("메뉴는 숨김 처리가 가능하다")
    @Test
    void menuHideTest() {

        // given
        Menu menu = menuProductSteps.노출된_메뉴를_생성한다();

        // when
        Menu hiddenMenu = menuService.hide(menu.getId());

        // then
        assertFalse(hiddenMenu.isDisplayed());
    }

    @DisplayName("메뉴는 보이기 처리가 가능하다")
    @Test
    void menuDisplayTest() {

        // given
        Menu menu = menuProductSteps.숨겨진_메뉴를_생성한다();

        // when
        Menu displayed = menuService.display(menu.getId());

        // then
        assertTrue(displayed.isDisplayed());
    }


    @DisplayName("메뉴의 가격이 메뉴상품의 가격보다 높으면 보이기 처리가 불가능하다")
    @Test
    void menuDisplayFailTest() {

        MenuGroup menuGroup = menuProductSteps.메뉴그룹_생성한다();

        // given
        Product product = menuProductSteps.상품을_생성한다(new ProductBuilder().with("치킨", BigDecimal.valueOf(100)).build());
        MenuProduct menuProduct1 = menuProductSteps.메뉴상품을_생성한다(product);
        Menu menu = menuProductSteps.메뉴를_생성한다("치킨", 10_000, menuGroup, true, List.of(menuProduct1));

        // when
        // then
        assertThatThrownBy(() -> menuService.display(menu.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴의 가격을 변경할 때 메뉴상품의 가격보다 크면 변경이 불가능하다")
    @Test
    void changePriceTest() {


        // given
        MenuGroup menuGroup = menuProductSteps.메뉴그룹_생성한다();
        Product product = menuProductSteps.상품을_생성한다(new ProductBuilder().with("치킨", BigDecimal.valueOf(100)).build());
        MenuProduct menuProduct1 = menuProductSteps.메뉴상품을_생성한다(product);

        Menu menu = menuProductSteps.메뉴를_생성한다("치킨", 10_000, menuGroup, true, List.of(menuProduct1));

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu)).isInstanceOf(IllegalArgumentException.class);
    }

}
