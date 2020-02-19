package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @Mock
    MenuDao menuDao;
    @Mock
    MenuGroupDao menuGroupDao;
    @Mock
    MenuProductDao menuProductDao;
    @Mock
    ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    private MenuGroup defaultMenuGroup;
    private Product defaultProduct;

    @BeforeEach
    private void setup() {
        defaultMenuGroup = getMenuGroup(1L, "테스트 메뉴 그룹");
        defaultProduct = getProduct(1L, "테스트 제품", BigDecimal.valueOf(1000L));
    }

    @DisplayName("메뉴 가격이 0 이상일 때만 생성이 된다.")
    @Test
    public void createMenuWithNegativePrice() {
        MenuProduct menuProduct =  getMenuProduct(1L, defaultProduct.getId(), 1L, 1L);
        Menu negativePriceMenu = getMenu(1L, BigDecimal.valueOf(-1000L), defaultMenuGroup.getId(), Collections.singletonList(menuProduct));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(negativePriceMenu));
    }

    @DisplayName("메뉴는 메뉴 그룹에 포함되어 있어야 한다.")
    @Test
    public void createMenuInMenuGroup() {
        Menu menu = getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>());
        given(menuGroupDao.existsById(anyLong())).willReturn(false);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴는 제품을 반드시 포함해야한다.")
    @Test
    public void createMenuWithProducts() {
        Menu menu = getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>());
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }


    @DisplayName("메뉴의 가격은 제품 가격의 합 이하이다.")
    @Test
    public void creteMenuCompareMenuPriceToProductsSum() {

        MenuProduct menuProduct =  getMenuProduct(1L, defaultProduct.getId(), 1L, 1L);
        Menu menu = getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), Collections.singletonList(menuProduct));
        given(productDao.findById(anyLong())).willReturn(java.util.Optional.ofNullable(defaultProduct));
        given(menuGroupDao.existsById(anyLong())).willReturn(true);

        menu.setPrice(defaultProduct.getPrice().add(BigDecimal.valueOf(100L)));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }


    @DisplayName("메뉴의 목록을 볼 수 있다.")
    @Test
    public void list() {
        MenuProduct menuProduct = getMenuProduct(1L, defaultProduct.getId(), 1L, 1L);

        List<Menu> menus = new ArrayList<>();
        menus.add(getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>()));
        menus.add(getMenu(2L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>()));
        menus.add(getMenu(3L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>()));

        given(menuProductDao.findAllByMenuId(anyLong())).willReturn(Collections.singletonList(menuProduct));
        given(menuDao.findAll()).willReturn(menus);

        Menu[] menusArrays = new Menu[menus.size()];
        menus.toArray(menusArrays);

        assertThat(menuBo.list()).contains(menusArrays);
    }

    public Menu getMenu(Long id, BigDecimal price, Long menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public Product getProduct(Long id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public MenuGroup getMenuGroup(Long id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public MenuProduct getMenuProduct(Long seq, Long productId, Long quantity, Long menuId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setMenuId(productId);
        menuProduct.setProductId(menuId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public MenuProduct getMenuProduct(Long seq, Product product, Long quantity, Menu menu) {
        return getMenuProduct(seq, product.getId(), quantity, menu.getId());
    }


}