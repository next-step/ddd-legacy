package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuBoTest {

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

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setId(1L);
        menu.setName("고기");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroupId(2L);
        menu.setMenuProducts(Collections.emptyList());
    }

    @Test
    @DisplayName("메뉴 가격은 0원 이하일 때")
    void createMenuByValidationPrice() {
        // give
        Menu menuActual = new Menu();
        menuActual.setPrice(BigDecimal.valueOf(0));
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menuActual));
    }

    @Test
    @DisplayName("등록된 메뉴 그룹에만 메뉴를 등록할 수 있다.")
    void createMenuByValidationMenuGroup() {
        // give
        given(menuGroupDao.existsById(2L))
                .willReturn(false);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menu));
    }

    @Test
    @DisplayName("등록된 상품만 선택이 가능하다.")
    void createMenuByValidationProduct() {
        // give
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);

        menu.setMenuProducts(Arrays.asList(menuProduct));
        menu.setPrice(BigDecimal.valueOf(0));

        given(menuGroupDao.existsById(2L))
                .willReturn(true);

        given(productDao.findById(1L))
                .willReturn(null);
        // when then
        assertThatNullPointerException().isThrownBy(() -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴 가격은 등록된 상품들의 가격 합보다 작아야한다.")
    void createMenuByValidationMenuPriceOver() {
        // give
        given(menuGroupDao.existsById(2L))
                .willReturn(true);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴 등록")
    void create() {
        // give
        given(menuGroupDao.existsById(2L))
                .willReturn(true);
        given(menuDao.save(menu))
                .willReturn(menu);
        menu.setPrice(BigDecimal.valueOf(0));
        Menu menuExpected = menu;

        // when
        Menu menuActual = menuBo.create(menu);
        // then
        assertThat(menuActual.getId()).isEqualTo(menuExpected.getId());
        assertThat(menuActual.getName()).isEqualTo(menuExpected.getName());
    }

    @Test
    @DisplayName("등록된 메뉴들을 조회할 수 있다.")
    void getMenus() {
        // give
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);

        given(menuDao.findAll())
                .willReturn(Arrays.asList(menu));

        given(menuProductDao.findAllByMenuId(menu.getId()))
                .willReturn(Arrays.asList(menuProduct));

        List<Menu> menusExpected = Arrays.asList(menu);
        // when
        List<Menu> menusActual = menuBo.list();
        // then
        assertThat(menusActual.get(0).getName())
                .isEqualTo(menusExpected.get(0).getName());
    }
}
