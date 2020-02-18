package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuBoTest {

    DefaultMenuDao menuDao = new InMemoryMenuDao();
    DefaultMenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    DefaultMenuProductDao menuProductDao = new InMemoryMenuProductDao();
    DefaultProductDao productDao = new InMemoryProductDao();

    MenuBo menuBo;
    Product halfFried;
    Product halfChilly;
    MenuProduct halfFriedProduct;
    MenuProduct halfChillyProduct;

    @BeforeEach
    void setUp() {
        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("세트메뉴");
        menuGroupDao.save(menuGroup);

        halfFried = new Product();
        halfFried.setId(1L);
        halfFried.setName("후라이드 반마리");
        halfFried.setPrice(BigDecimal.valueOf(7000L));
        productDao.save(halfFried);

        halfChilly = new Product();
        halfChilly.setId(2L);
        halfChilly.setName("양념 반마리");
        halfChilly.setPrice(BigDecimal.valueOf(8000L));
        productDao.save(halfChilly);

        halfFriedProduct = new MenuProduct();
        halfFriedProduct.setMenuId(1L);
        halfFriedProduct.setProductId(halfFried.getId());
        halfFriedProduct.setQuantity(1);
        halfFriedProduct.setSeq(1L);
        menuProductDao.save(halfFriedProduct);

        halfChillyProduct = new MenuProduct();
        halfChillyProduct.setMenuId(2L);
        halfChillyProduct.setProductId(halfChilly.getId());
        halfChillyProduct.setQuantity(1);
        halfChillyProduct.setSeq(2L);
        menuProductDao.save(halfChillyProduct);
    }

    @Test
    @DisplayName("메뉴는 추가될 수 있다.")
    void createTest() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(Arrays.asList(halfFriedProduct, halfChillyProduct));
        menu.setPrice(BigDecimal.valueOf(14000L));
        assertThat(menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴의 가격은 0원 이상이다")
    void createWithPriceExceptionTest() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuGroupId(-1L);
        menu.setMenuProducts(Arrays.asList(halfFriedProduct, halfChillyProduct));
        menu.setPrice(BigDecimal.valueOf(14000L));
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴는 0개 이상의 항목를 포함한다.")
    void createWithEmptyProductExceptionTest() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuGroupId(-1L);
        menu.setMenuProducts(null);
        menu.setPrice(BigDecimal.valueOf(14000L));
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴는 한 가지 메뉴그룹에 속해 있어야 한다.")
    void createWithoutMenuGroupExceptionTest() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuProducts(null);
        menu.setPrice(BigDecimal.valueOf(14000L));
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴의 가격은 메뉴에 들어가는 요리의 가격의 합보다 클 수 없다.")
    void createOverSumOfProductsPriceExceptionTest() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuGroupId(-1L);
        menu.setMenuProducts(null);
        menu.setPrice(BigDecimal.valueOf(20000L));
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("모든 메뉴 리스트를 조회할 수 있다.")
    void readAllMenuListTest() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(Arrays.asList(halfFriedProduct, halfChillyProduct));
        menu.setPrice(BigDecimal.valueOf(14000L));
        menuBo.create(menu);
        assertThat(menuBo.list()).contains(menu);
    }
}