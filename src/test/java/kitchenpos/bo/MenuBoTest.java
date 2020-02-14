package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.DefaultProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @InjectMocks private MenuBo menuBo;
    @Mock private MenuDao menuDao;
    @Mock private MenuProductDao menuProductDao;
    @Mock private MenuGroupDao menuGroupDao;
    @Mock private DefaultProductDao productDao;

    private MenuProduct menuProduct;
    private Menu menu;
    private Product product;
    private List<MenuProduct> menuProductList;
    private List<Menu> menuList;
    private Optional<Product> optionalProduct;

    @BeforeEach
    public void setup() {
        this.menuProduct = new MenuProduct();
        this.menu = new Menu();
        this.product = new Product();
        this.menuList = new ArrayList<>();
        this.menuProductList = new ArrayList<>();

        this.menuProduct.setMenuId(1L);
        this.menuProduct.setProductId(1L);
        this.menuProduct.setQuantity(1L);
        this.menuProduct.setSeq(1L);

        this.menuProductList.add(this.menuProduct);

        this.menu.setName("후라이드치킨");
        this.menu.setPrice(new BigDecimal(16000));
        this.menu.setMenuProducts(this.menuProductList);
        this.menu.setMenuGroupId(2L);
        this.menu.setId(1L);

        this.menuList.add(menu);

        this.product.setId(1L);
        this.product.setName("후라이드");
        this.product.setPrice(new BigDecimal(16000));

        this.optionalProduct = Optional.of(this.product);
    }

    @DisplayName("메뉴를 생성할 때 메뉴의 가격을 반드시 입력해야 한다.")
    @Test
    public void createMenuWithoutPriceTest() {
        //given
        Menu givenMenu = menu;
        givenMenu.setPrice(null);

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 생성할 때 메뉴의 가격은 반드시 양수를 입력해야 한다.")
    @Test
    public void createMenuWithNegativePriceTest() {
        //given
        Menu givenMenu = menu;
        givenMenu.setPrice(BigDecimal.valueOf(-1));

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("각 메뉴는 반드시 하나 이상의 메뉴그룹에 속한다.")
    @Test
    public void menuGroupIdTest() {
        //given
        Menu givenMenu = menu;
        givenMenu.setMenuGroupId(null);

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장에서 판매하지 않는 상품을 입력할 수 없다.")
    @Test
    public void menuPriceTest() {
        //given
        Menu givenMenu = menu;
        givenMenu.setMenuProducts(null);

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("산정된 메뉴의 가격은 매장에서 판매하는 상품의 가격과 수량을 곱한 금액보다 크거나 같아야 한다.")
    @Test
    public void menuPriceVersusMenuProductPriceTest() {
        //given
        this.menuProduct.setQuantity(-1L);
        Menu givenMenu = menu;

        //when
        //then
        assertThatThrownBy(() ->{ menuBo.create(givenMenu); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 목록을 볼 수 있다.")
    @Test
    public void list() {
        //given
        List<Menu> givenMenuList = menuList;
        List<MenuProduct> givenMenuProductList = menuProductList;
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