package kitchenpos.bo;

import kitchenpos.Fixtures;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private Menu defaultMenu;
    private List<MenuProduct> defaultMenuProducts = new ArrayList<>();

    @BeforeEach
    private void setup() {
        defaultMenuGroup = Fixtures.getMenuGroup(1L, "테스트 메뉴 그룹");
        defaultProduct = Fixtures.getProduct(1L, "테스트 제품", BigDecimal.valueOf(1000L));
        defaultMenuProducts = Collections.singletonList(Fixtures.getMenuProduct(1L, defaultProduct.getId(), 1L, 1L));
        defaultMenu = Fixtures.getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), defaultMenuProducts);
    }

    @DisplayName("메뉴 가격이 0 이상일 때만 생성이 된다.")
    @Test
    public void createMenuWithNegativePrice() {
        Menu negativePriceMenu = Fixtures.getMenu(1L, BigDecimal.valueOf(-1000L), defaultMenuGroup.getId(), defaultMenuProducts);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(negativePriceMenu));
    }

    @DisplayName("메뉴는 메뉴 그룹에 포함되어 있어야 한다.")
    @Test
    public void createMenuInMenuGroup() {
        given(menuGroupDao.existsById(defaultMenu.getMenuGroupId())).willReturn(false);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(defaultMenu));
    }

    @DisplayName("메뉴는 제품을 반드시 포함해야한다.")
    @Test
    public void createMenuNoEmptyProducts() {
        Menu menu = Fixtures.getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>());

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴안의 제품은 반드시 존재하는 제품이어야 한다.")
    @Test
    public void createMenuWithProducts() {
        given(menuGroupDao.existsById(defaultMenu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(defaultMenu));
    }

    @DisplayName("메뉴의 가격은 제품 가격의 합 이하이다.")
    @Test
    public void creteMenuCompareMenuPriceToProductsSum() {
        given(productDao.findById(anyLong())).willReturn(java.util.Optional.ofNullable(defaultProduct));
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        defaultMenu.setPrice(defaultProduct.getPrice().add(BigDecimal.valueOf(100L)));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(defaultMenu));
    }

    @DisplayName("메뉴의 목록을 볼 수 있다.")
    @Test
    public void list() {
        List<Menu> menus = new ArrayList<>();
        menus.add(Fixtures.getMenu(1L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>()));
        menus.add(Fixtures.getMenu(2L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>()));
        menus.add(Fixtures.getMenu(3L, BigDecimal.valueOf(1000L), defaultMenuGroup.getId(), new ArrayList<>()));

        given(menuProductDao.findAllByMenuId(anyLong())).willReturn(defaultMenuProducts);
        given(menuDao.findAll()).willReturn(menus);

        assertThat(menuBo.list()).contains(menus.toArray(new Menu[0]));
    }
}