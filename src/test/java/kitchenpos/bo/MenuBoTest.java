package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuBoTest {

    private MenuDao menuDao = new InMemoryMenuDao();
    private MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    private MenuProductDao menuProductDao = new InMemoryMenuProductDao();
    private ProductDao productDao = new InMemoryProductDao();

    private MenuBo menuBo;

    @BeforeEach
    void setUp() {
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
        menuGroupDao.save(MenuGroupTest.ofSet());

        productDao.save(ProductTest.ofHalfFried());
        productDao.save(ProductTest.ofHalfChilly());

        menuProductDao.save(MenuProductTest.ofHalfFriedProduct());
        menuProductDao.save(MenuProductTest.ofHalfChillyProduct());
    }

    @Test
    @DisplayName("메뉴는 추가될 수 있다.")
    void createTest() {
        Menu menu = MenuTest.ofHalfAndHalf();
        assertThat(menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴의 가격은 0원 이상이다")
    void createWithPriceExceptionTest() {
        Menu menu = MenuTest.ofHalfAndHalf();
        menu.setPrice(BigDecimal.valueOf(-1000));
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴는 0개 이상의 항목를 포함한다.")
    void createWithEmptyProductExceptionTest() {
        Menu menu = MenuTest.ofHalfAndHalf();
        menu.setMenuProducts(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴는 한 가지 메뉴그룹에 속해 있어야 한다.")
    void createWithoutMenuGroupExceptionTest() {
        Menu menu = MenuTest.ofHalfAndHalf();
        menu.setMenuGroupId(null);
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴의 가격은 메뉴에 들어가는 요리의 가격의 합보다 클 수 없다.")
    void createWithOverSumOfProductsPriceExceptionTest() {
        Menu menu = MenuTest.ofHalfAndHalf();
        menu.setPrice(BigDecimal.valueOf(20000L));
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("모든 메뉴 리스트를 조회할 수 있다.")
    void readAllMenuListTest() {
        Menu menu = MenuTest.ofHalfAndHalf();
        menuBo.create(menu);
        assertThat(menuBo.list()).contains(menu);
    }
}
