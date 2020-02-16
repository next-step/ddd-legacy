package kitchenpos.bo;

import kitchenpos.builder.MenuBuilder;
import kitchenpos.builder.MenuProductBuilder;
import kitchenpos.builder.ProductBuilder;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

public class MenuBoTestWithBuilder extends MockTest {
    @InjectMocks private MenuBo menuBo;
    @Mock private MenuDao menuDao;
    @Mock private MenuProductDao menuProductDao;
    @Mock private MenuGroupDao menuGroupDao;
    @Mock private ProductDao productDao;

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void create() {
        //given
        final Product givenProduct = ProductBuilder
                .product()
                .withId(1L)
                .withName("후라이드")
                .withPrice(new BigDecimal(16000))
                .build();
        final MenuProduct givenMenuProduct = MenuProductBuilder
                .menuProduct()
                .withMenuId(1L)
                .withProductId(1L)
                .withSeq(1L)
                .withQuantity(2L)
                .build();
        final Menu givenMenu = MenuBuilder
                .menu()
                .withId(1L)
                .withName("후라이드치킨")
                .withPrice(new BigDecimal(16000))
                .withMenuGroupId(1L)
                .withMenuProducts(Arrays.asList(givenMenuProduct))
                .build();

        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);
        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(givenProduct));
        given(menuDao.save(givenMenu))
                .willReturn(givenMenu);
        given(menuProductDao.save(any(MenuProduct.class)))
                .willReturn(givenMenuProduct);

        //when
        final Menu actualMenu = menuBo.create(givenMenu);

        //then
        assertThat(actualMenu.getName()).isEqualTo(givenMenu.getName());
    }

    @DisplayName("메뉴를 생성할 때 메뉴의 가격을 반드시 입력해야 한다.")
    @ParameterizedTest
    @NullSource
    void createMenuWithoutPriceTest(BigDecimal price) {
        //given
        Menu givenMenu = MenuBuilder.menu()
                .withPrice(price)
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 생성할 때 메뉴의 가격은 반드시 양수를 입력해야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {0,-1,-2})
    public void createMenuWithNegativePriceTest(int price) {
        //given
        Menu givenMenu = MenuBuilder.menu()
                .withPrice(BigDecimal.valueOf(price))
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("각 메뉴는 반드시 하나 이상의 메뉴그룹에 속한다.")
    @ParameterizedTest
    @NullSource
    public void menuGroupIdTest(Long menuGroupId) {
        //given
        Menu givenMenu = MenuBuilder.menu()
                .withMenuGroupId(null)
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장에서 판매하지 않는 상품을 입력할 수 없다.")
    @ParameterizedTest
    @NullSource
    public void menuPriceTest(List<MenuProduct> menuProducts) {
        //given
        Menu givenMenu = MenuBuilder.menu()
                .withMenuProducts(menuProducts)
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("산정된 메뉴의 가격은 매장에서 판매하는 상품의 가격과 수량을 곱한 금액보다 크거나 같아야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1L})
    public void menuPriceVersusMenuProductPriceTest(long quantity) {
        //given
        MenuProduct givenMenuProduct = MenuProductBuilder.menuProduct()
                .withQuantity(quantity)
                .build();
        Menu givenMenu = MenuBuilder.menu()
                .withMenuProducts(Arrays.asList(givenMenuProduct))
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 목록을 볼 수 있다.")
    @Test
    public void list() {
        //given
        MenuProduct givenMenuProduct = MenuProductBuilder.menuProduct()
                .build();
        Menu givenMenu = MenuBuilder.menu()
                .withMenuProducts(Arrays.asList(givenMenuProduct))
                .build();
        List<Menu> givenMenuList = Arrays.asList(givenMenu);
        List<MenuProduct> givenMenuProductList = givenMenu.getMenuProducts();

        given(menuDao.findAll())
                .willReturn(givenMenuList);
        given(menuProductDao.findAllByMenuId(anyLong()))
                .willReturn(givenMenuProductList);

        //when
        List<Menu> actualMenuList = menuBo.list();

        //then
        assertThat(actualMenuList.size())
                .isEqualTo(givenMenuList.size());
    }
}
