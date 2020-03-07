package kitchenpos.bo;

import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.MenuGroupBuilder;
import kitchenpos.builder.MenuProductBuilder;
import kitchenpos.builder.ProductBuilder;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;

import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    private MenuGroupBuilder menuGroupBuilder = new MenuGroupBuilder();
    private ProductBuilder productBuilder = new ProductBuilder();
    private MenuBuilder menuBuilder = new MenuBuilder();
    private MenuProductBuilder menuProductBuilder = new MenuProductBuilder();

    @Test
    @DisplayName("메뉴를 새로 생성활 수 있다")
    void createMenu() {
        Product product = productBuilder
                .id(1L)
                .name("후라이드")
                .price(BigDecimal.valueOf(16000))
                .build();

        MenuGroup menuGroup = menuGroupBuilder
                .id(1L)
                .name("")
                .build();

        Menu newMenu = menuBuilder
                .id(1L)
                .name("후라이드치킨")
                .menuGroupId(menuGroup.getId())
                .price(BigDecimal.valueOf(16000))
                .menuProducts(asList(menuProductBuilder
                        .seq(1L)
                        .menuId(1L)
                        .productId(product.getId())
                        .quantity(1)
                        .build()
                ))
                .build();

        given(menuGroupDao.existsById(menuGroup.getId()))
                .willReturn(Boolean.TRUE);

        given(productDao.findById(product.getId()))
                .willReturn(Optional.of(product));

        given(menuDao.save(any(Menu.class)))
                .willReturn(newMenu);

        given(menuProductDao.save(any(MenuProduct.class)))
                .willReturn(newMenu.getMenuProducts().get(0));

        Menu savedMenu = menuBo.create(newMenu);

        assertThat(savedMenu).isEqualTo(newMenu);
        assertThat(savedMenu.getMenuProducts()).isEqualTo(newMenu.getMenuProducts());
    }

    @Test
    @DisplayName("메뉴의 가격이 존재해야 한다")
    void menuPriceIsNotNull() {

        Menu newMenu = menuBuilder
                .id(1L)
                .name("후라이드치킨")
                .price(null)
                .build();

        assertThatThrownBy(() -> menuBo.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @ParameterizedTest
    @DisplayName("메뉴의 가격은 0 보다 커야 한다")
    @NullSource
    @ValueSource(strings = {"-1", "-0.1"})
    void menuPriceIsBiggerThanZero(final BigDecimal price) {
        Menu newMenu = menuBuilder
                .id(1L)
                .name("후라이드치킨")
                .price(price)
                .build();

        assertThatThrownBy(() -> menuBo.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("사전에 등록되어 있는 메뉴그룹에 반드시 지정되어야 한다")
    void menuMustHasMenuGroup() {
        MenuGroup menuGroup = menuGroupBuilder
                .id(1L)
                .name("")
                .build();

        Menu newMenu = menuBuilder
                .id(1L)
                .name("후라이드치킨")
                .menuGroupId(menuGroup.getId())
                .price(BigDecimal.valueOf(16000))
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(Boolean.FALSE);

        assertThatThrownBy(() -> menuBo.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("메뉴가 포함하는 제품은 모두 사전에 존재하는 것이야 한다")
    void menuMustHasProduct() {
        Product product = productBuilder
                .id(1L)
                .name("후라이드")
                .price(BigDecimal.valueOf(16000))
                .build();

        MenuGroup menuGroup = menuGroupBuilder
                .id(1L)
                .name("")
                .build();

        Menu newMenu = menuBuilder
                .id(1L)
                .name("후라이드치킨")
                .menuGroupId(menuGroup.getId())
                .price(BigDecimal.valueOf(16000))
                .menuProducts(asList(menuProductBuilder
                        .seq(1L)
                        .menuId(1L)
                        .productId(product.getId())
                        .quantity(1)
                        .build()
                ))
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(Boolean.TRUE);

        given(productDao.findById(anyLong()))
                .willReturn(Optional.empty());


        assertThatThrownBy(() -> menuBo.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("메뉴의 가격이 속한 모든 제품의 (수량 * 단가)의 합계는 보다 작야야 한다.")
    void menuPriceIsSmallerThanProductTotalPrice() {
        Product product = productBuilder
                .id(1L)
                .name("후라이드")
                .price(BigDecimal.valueOf(17000))
                .build();

        MenuGroup menuGroup = menuGroupBuilder
                .id(1L)
                .name("")
                .build();

        Menu newMenu = menuBuilder
                .id(1L)
                .name("후라이드치킨")
                .menuGroupId(menuGroup.getId())
                .price(BigDecimal.valueOf(18000))
                .menuProducts(asList(menuProductBuilder
                        .seq(1L)
                        .menuId(1L)
                        .productId(product.getId())
                        .quantity(1)
                        .build()
                ))
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(Boolean.TRUE);

        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(product));

        assertThatThrownBy(() -> menuBo.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("메뉴의 목록을 조회할 수 있다")
    void listMenu() {
        Product product = productBuilder
                .id(1L)
                .name("후라이드")
                .price(BigDecimal.valueOf(16000))
                .build();

        MenuGroup menuGroup = menuGroupBuilder
                .id(1L)
                .name("")
                .build();


        List<Menu> menus = asList(
                menuBuilder
                        .id(1l)
                        .name("후라이드치킨")
                        .menuGroupId(menuGroup.getId())
                        .price(BigDecimal.valueOf(16000))
                        .menuProducts(asList(menuProductBuilder
                                .seq(1L)
                                .menuId(1L)
                                .productId(product.getId())
                                .quantity(1)
                                .build()
                        ))
                        .build()
        );

        given(menuDao.findAll())
                .willReturn(menus);

        given(menuProductDao.findAllByMenuId(anyLong()))
                .willReturn(null);

        List<Menu> exceptedMenus = menuBo.list();

        assertThat(exceptedMenus)
                .hasSameSizeAs(menus)
                .isEqualTo(menus);

    }

}