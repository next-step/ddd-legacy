package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import kitchenpos.support.MenuBuilder;
import kitchenpos.support.MenuProductBuilder;
import kitchenpos.support.ProductBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class MenuBoTest extends MockTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private MenuProductDao menuProductDao;

    @InjectMocks
    private MenuBo sut;

    @Test
    @DisplayName("메뉴 생성")
    void createMenu() {
        // given
        final long menuId = 10L;
        final MenuProductBuilder menuProductBuilder = MenuProductBuilder.menuProduct()
                .withMenuId(menuId)
                .withQuantity(10);
        final MenuBuilder menuBuilder = MenuBuilder.menu()
                .withMenuGroupId(1)
                .withName("menu group name")
                .withPrice(BigDecimal.ZERO)
                .withMenuProducts(Collections.singletonList(
                        menuProductBuilder.build()
                ));
        final Product product = ProductBuilder.product()
                .withId(2)
                .withPrice(BigDecimal.ONE)
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);
        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(product));
        given(menuDao.save(any(Menu.class)))
                .willReturn(menuBuilder.withId(menuId).build());
        given(menuProductDao.save(any(MenuProduct.class)))
                .willReturn(menuProductBuilder.withSeq(4).build());

        // when
        final Menu savedMenu = sut.create(menuBuilder.build());

        // then
        assertThat(savedMenu.getMenuProducts().get(0))
                .isEqualToComparingFieldByField(menuProductBuilder.build());

        // and
        verify(menuGroupDao).existsById(anyLong());
        verify(menuDao).save(any(Menu.class));
        verify(productDao).findById(anyLong());
        verify(menuProductDao).save(any(MenuProduct.class));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideIllegalPrice")
    @DisplayName("메뉴의 가격이 없거나 0보다 작으면 예외 던짐")
    void shouldThrowExceptionWithIllegalPrice(BigDecimal price) {
        // given
        final Menu menu = MenuBuilder.menu()
                .withPrice(price)
                .build();

        // when
        assertThatThrownBy(() -> sut.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verifyNoInteractions(menuDao);
        verifyNoInteractions(menuGroupDao);
        verifyNoInteractions(menuProductDao);
        verifyNoInteractions(productDao);
    }

    private static Stream provideIllegalPrice() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-1L)),
                Arguments.of(BigDecimal.valueOf(-12L))
        );
    }

    @Test
    @DisplayName("메뉴 그룹이 존재하지 않으면 예외 던짐")
    void shouldThrowExceptionWhenNotExistMenuGroup() {
        // given
        final MenuProductBuilder menuProductBuilder = MenuProductBuilder.menuProduct()
                .withMenuId(1)
                .withQuantity(10);
        final Menu menu = MenuBuilder.menu()
                .withMenuGroupId(2)
                .withName("menu group name")
                .withPrice(BigDecimal.ZERO)
                .withMenuProducts(Collections.singletonList(
                        menuProductBuilder.build()
                )).build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> sut.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verify(menuGroupDao).existsById(anyLong());
        verifyNoInteractions(productDao);
        verifyNoInteractions(menuDao);
        verifyNoInteractions(menuProductDao);
    }

    @Test
    @DisplayName("상품을 찾지 못하면 예외 던짐")
    void shouldThrowExceptionWhenNotFoundProduct() {
        // given
        final MenuProductBuilder menuProductBuilder = MenuProductBuilder.menuProduct()
                .withMenuId(1)
                .withQuantity(10);
        final Menu menu = MenuBuilder.menu()
                .withMenuGroupId(2)
                .withName("menu group name")
                .withPrice(BigDecimal.ZERO)
                .withMenuProducts(Collections.singletonList(
                        menuProductBuilder.build()
                )).build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);
        given(productDao.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verify(menuGroupDao).existsById(anyLong());
        verify(productDao).findById(anyLong());
        verifyNoInteractions(menuDao);
        verifyNoInteractions(menuProductDao);
    }

    @Test
    @DisplayName("메뉴 가격이 상품 가격의 합보다 크면 예외 던짐")
    void shouldThrowExceptionWhenMenuPriceIsOverSummationOfProductPrice() {
        // given
        final long menuId = 10L;
        final MenuProduct menuProduct = MenuProductBuilder.menuProduct()
                .withMenuId(menuId)
                .withQuantity(1)
                .build();
        final Menu menu = MenuBuilder.menu()
                .withMenuGroupId(1)
                .withName("menu group name")
                .withPrice(BigDecimal.TEN)
                .withMenuProducts(
                        Collections.singletonList(menuProduct)
                ).build();
        final Product product = ProductBuilder.product()
                .withId(2)
                .withPrice(BigDecimal.ONE)
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);
        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> sut.create(menu))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verify(menuGroupDao).existsById(anyLong());
        verify(productDao).findById(anyLong());
        verifyNoInteractions(menuDao);
        verifyNoInteractions(menuProductDao);
    }

    @Test
    @DisplayName("메뉴 목록 조회")
    void getMenus() {
        // given
        final long menuId = 12;
        final MenuProduct menuProduct = MenuProductBuilder.menuProduct()
                .withMenuId(menuId)
                .withQuantity(10)
                .build();
        final Menu menu = MenuBuilder.menu()
                .withId(menuId)
                .withMenuGroupId(1)
                .withName("menu group name")
                .withPrice(BigDecimal.ZERO)
                .build();

        given(menuDao.findAll())
                .willReturn(Collections.singletonList(menu));
        given(menuProductDao.findAllByMenuId(anyLong()))
                .willReturn(Collections.singletonList(menuProduct));

        // when
        List<Menu> menus = sut.list();

        // then
        assertThat(menus.get(0).getMenuProducts().get(0))
                .isEqualToComparingFieldByField(menuProduct);

        // and
        verify(menuDao).findAll();
        verify(menuProductDao).findAllByMenuId(anyLong());
    }
}
