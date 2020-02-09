package kitchenpos.bo;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @InjectMocks private MenuBo menuBo;
    @Mock private MenuDao menuDao;
    @Mock private MenuProductDao menuProductDao;
    @Mock private MenuGroupDao menuGroupDao;
    @Mock private ProductDao productDao;

    @DisplayName("메뉴그룹을 설정할 수 있다.")
    @Test
    public void setMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("두마리메뉴");

        List<MenuGroup> menuGroupList = new ArrayList<>();
        menuGroupList.add(menuGroup);

        when(menuGroupDao.findAll()).thenReturn(menuGroupList);

        List<MenuGroup> result = menuGroupDao.findAll();

        assertThat(result.get(0).getName()).isEqualTo("두마리메뉴");
    }

    @DisplayName("메뉴그룹 목록을 볼 수 있다.")
    @Test
    public void listMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("두마리메뉴");

        when(menuGroupDao.save(menuGroup)).thenReturn(menuGroup);

        MenuGroup result = menuGroupDao.save(menuGroup);

        assertThat(result.getName()).isEqualTo("두마리메뉴");
    }

    @DisplayName("각 메뉴는 반드시 하나 이상의 메뉴그룹에 속한다.")
    @Test
    public void menuGroupIdTest() {
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(16000));

        Throwable thrown = catchThrowable(() ->{
            menuBo.create(menu);
        });

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴를 만들 수 있는 상품의 재고를 볼 수 있다.")
    @Test
    public void menuProductQuantity() {
        MenuProduct menuProduct = new MenuProduct();
        List<MenuProduct> menuProductList = new ArrayList<>();

        menuProduct.setMenuId(1L);
        menuProduct.setQuantity(1L);
        menuProductList.add(menuProduct);

        when(menuProductDao.findAllByMenuId(1L)).thenReturn(menuProductList);

        List<MenuProduct> result = menuProductDao.findAllByMenuId(1L);

        assertThat(result.get(0).getQuantity()).isEqualTo(1L);
    }

    @DisplayName("각 메뉴에 이름,가격을 설정할 수 있다.")
    @Test
    public void create() {
        MenuProduct menuProduct = new MenuProduct();
        List<MenuProduct> menuProductList = new ArrayList<>();
        Menu menu = new Menu();
        Product product = new Product();
        Optional<Product> optionalProduct = Optional.of(product);

        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);
        menuProduct.setSeq(1L);
        menuProductList.add(menuProduct);

        menu.setName("후라이드치킨");
        menu.setPrice(new BigDecimal(16000));
        menu.setMenuProducts(menuProductList);
        menu.setMenuGroupId(2L);
        menu.setId(1L);

        product.setId(1L);
        product.setName("후라이드");
        product.setPrice(new BigDecimal(16000));

        when(menuGroupDao.existsById(menu.getMenuGroupId())).thenReturn(true);
        when(productDao.findById(menuProduct.getProductId())).thenReturn(optionalProduct.of(product));
        when(menuDao.save(menu)).thenReturn(menu);
        when(menuProductDao.save(menuProduct)).thenReturn(menuProduct);

        Menu result = menuBo.create(menu);
        assertThat(result.getName()).isEqualTo("후라이드치킨");
    }

    @DisplayName("메뉴 목록을 볼 수 있다.")
    @Test
    public void list() {
        MenuProduct menuProduct = new MenuProduct();
        List<MenuProduct> menuProductList = new ArrayList<>();
        Menu menu = new Menu();
        List<Menu> menuList = new ArrayList<>();

        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);
        menuProduct.setSeq(1L);
        menuProductList.add(menuProduct);

        menu.setName("후라이드치킨");
        menu.setPrice(new BigDecimal(16000));
        menu.setMenuProducts(menuProductList);
        menu.setMenuGroupId(2L);
        menu.setId(1L);

        menuList.add(menu);

        when(menuDao.findAll()).thenReturn(menuList);
        when(menuProductDao.findAllByMenuId(menu.getId())).thenReturn(menuProductList);

        List<Menu> result = menuBo.list();
        assertThat(result.get(0).getName()).isEqualTo("후라이드치킨");
    }
}