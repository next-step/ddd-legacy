package kitchenpos.application;

import fixtures.MenuBuilder;
import fixtures.MenuGroupBuilder;
import fixtures.MenuProductBuilder;
import fixtures.ProductBuilder;
import kitchenpos.domain.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;

    private Product product;
    private MenuProduct menuProduct;
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        product = new ProductBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .build();
        product = productRepository.save(product);

        menuProduct = new MenuProductBuilder()
                .withProduct(product)
                .withQuantity(1)
                .build();

        menuGroup = menuGroupRepository.save(new MenuGroupBuilder().withName("한 마리 메뉴").build());
    }

    @DisplayName("메뉴를 등록할 때")
    @Nested
    class MenuCreateTest {
        @DisplayName("이름, 가격, 표시 여부를 갖는다")
        @Test
        void createMenuTest() {

            Menu menu = menuService.create(new MenuBuilder()
                    .with("치킨", BigDecimal.valueOf(10_000))
                    .withMenuGroup(menuGroup)
                    .withMenuProducts(List.of(menuProduct))
                    .build());

            assertNotNull(menu.getName());
            assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(10_000));
            assertTrue(menu.isDisplayed());
        }


        @DisplayName("메뉴의 이름이 없으면 등록이 불가능 하다")
        @Test
        void menuNameWithBlankTest() {

            assertThrows(IllegalArgumentException.class, () -> menuService.create(new MenuBuilder()
                    .with(null, BigDecimal.valueOf(10_000))
                    .withMenuGroup(menuGroup)
                    .withMenuProducts(List.of(menuProduct))
                    .build()));
        }

        @DisplayName("메뉴의 가격이 0보다 작으면 등록이 불가능하다")
        @Test
        void menuPriceWithZeroTest() {

            assertThrows(IllegalArgumentException.class, () -> menuService.create(new MenuBuilder()
                    .with("치킨", BigDecimal.valueOf(-1))
                    .withMenuGroup(menuGroup)
                    .withMenuProducts(List.of(menuProduct))
                    .build()));
        }

        @DisplayName("메뉴그룹에 소속되지 않으면 등록이 불가능하다")
        @Test
        void menuGroupNullTest() {

            assertThrows(NoSuchElementException.class, () -> menuService.create(new MenuBuilder()
                    .with("치킨", BigDecimal.valueOf(10_000))
                    .withMenuProducts(List.of(menuProduct))
                    .withMenuGroup(new MenuGroupBuilder().withName("메뉴그룹 없음").build())
                    .build()));
        }
    }

    @DisplayName("메뉴는 숨김 처리가 가능하다")
    @Test
    void menuHideTest() {

        // given
        Menu menu = menuService.create(new MenuBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .withMenuGroup(menuGroup)
                .withMenuProducts(List.of(menuProduct))
                .build());

        // when
        Menu hiddenMenu = menuService.hide(menu.getId());

        // then
        assertFalse(hiddenMenu.isDisplayed());
    }

    @DisplayName("메뉴는 보이기 처리가 가능하다")
    @Test
    void menuDisplayTest() {

        // given
        Menu menu = menuService.create(new MenuBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .withMenuGroup(menuGroup)
                .withMenuProducts(List.of(menuProduct))
                .build());
        menuService.hide(menu.getId());

        // when
        Menu displayed = menuService.display(menu.getId());

        // then
        assertTrue(displayed.isDisplayed());
    }

    @DisplayName("메뉴의 가격을 변경할 때 메뉴상품의 가격보다 크면 변경이 불가능하다")
    @Test
    void changePriceTest() {

        // given
        Menu menu = menuService.create(new MenuBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .withMenuGroup(menuGroup)
                .withMenuProducts(List.of(menuProduct))
                .build());

        // when
        menu.setPrice(BigDecimal.valueOf(20_000));

        // then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
