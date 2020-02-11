package kitchenpos.bo;

import kitchenpos.dao.MenuProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuBoTest {

    @Autowired
    MenuBo menuBo;

    @Autowired
    MenuProductDao menuProductDao;

    @Test
    @DisplayName("메뉴 리스트 조회")
    void list() {
        List<Menu> list = menuBo.list();
        assertThat(list.size()).isEqualTo(6);
    }

    @Test
    @DisplayName("메뉴 생성 정상 동작")
    void create() {
        String menuName = "교촌 오리지날";
        BigDecimal price = new BigDecimal(16000);
        long menuGroupId = 1L;

        Menu menu = new Menu();
        MenuProduct menuProduct = menuProductDao.findById(1L).orElseThrow(RuntimeException::new);

        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        Menu savedMenu = menuBo.create(menu);

        assertThat(savedMenu.getName()).isEqualTo(menuName);
        assertThat(savedMenu.getMenuGroupId()).isEqualTo(menuGroupId);
    }

    @Test
    @DisplayName("메뉴 그룹 정보가 없을때 메뉴 생성 실패")
    void createFailByNotExsistMenugroup() {
        String menuName = "교촌 오리지날";
        BigDecimal price = new BigDecimal(16000);

        Menu menu = new Menu();
        MenuProduct menuProduct = menuProductDao.findById(1L).orElseThrow(RuntimeException::new);

        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴 가격이 음수 일때 생성 실패")
    void createFailByNegativePrice() {
        String menuName = "교촌 오리지날";
        BigDecimal price = new BigDecimal(-1000);
        long menuGroupId = 1L;

        Menu menu = new Menu();
        MenuProduct menuProduct = menuProductDao.findById(1L).orElseThrow(RuntimeException::new);

        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    @DisplayName("메뉴 가격이 Null 일때 생성 실패")
    @ParameterizedTest
    @NullSource
    void createFailByNull(BigDecimal price) {
        String menuName = "교촌 오리지날";
        long menuGroupId = 1L;

        Menu menu = new Menu();
        MenuProduct menuProduct = menuProductDao.findById(1L).orElseThrow(RuntimeException::new);

        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("등록되지 않은 상품을 포함 시 생성 실패")
    void createFailByNotExistProduct() {
        String menuName = "교촌 오리지날";
        BigDecimal price = new BigDecimal(16000);
        long menuGroupId = 1L;

        Menu menu = new Menu();
        MenuProduct menuProduct = new MenuProduct();

        menuProduct.setMenuId(1L);
        menuProduct.setProductId(100L);
        menuProduct.setQuantity(3);

        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Arrays.asList(menuProduct));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴 가격이 상품의 가격 합보다 클때 생성 실패")
    void createFailByPriceGreaterThanProductPrice() {
        String menuName = "교촌 오리지날";
        BigDecimal price = new BigDecimal(40000);
        long menuGroupId = 1L;

        Menu menu = new Menu();
        MenuProduct menuProduct1 = menuProductDao.findById(1L).orElseThrow(RuntimeException::new);
        MenuProduct menuProduct2 = menuProductDao.findById(2L).orElseThrow(RuntimeException::new);

        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(Arrays.asList(menuProduct1, menuProduct2));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }
}
