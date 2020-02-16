package kitchenpos.bo;

import kitchenpos.bo.mock.TestMenuDao;
import kitchenpos.bo.mock.TestMenuGroupDao;
import kitchenpos.bo.mock.TestMenuProductDao;
import kitchenpos.bo.mock.TestProductDao;
import kitchenpos.dao.Interface.MenuDao;
import kitchenpos.dao.Interface.MenuGroupDao;
import kitchenpos.dao.Interface.MenuProductDao;
import kitchenpos.dao.Interface.ProductDao;
import kitchenpos.model.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuBoTest {

    private MenuDao menuDao = new TestMenuDao();
    private MenuGroupDao menuGroupDao = new TestMenuGroupDao();
    private MenuProductDao menuProductDao = new TestMenuProductDao();
    private ProductDao productDao = new TestProductDao();

    private MenuBo menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);

    private Menu input;

    @BeforeEach
    void setUp() {
        input = new Menu();
        input.setPrice(BigDecimal.valueOf(5000));

        productDao.save(defaultProduct());
        menuGroupDao.save(defaultMenuGroup());
    }


    @DisplayName("메뉴 생성 시 가격이 없거나, 0보다 작으면 IllegalArgumentException 발생")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-3000"})
    void createLessThanZero(BigDecimal parameter) {
        input.setPrice(parameter);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(input));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹아이디가 없으면 IllegalArgumentException 발생")
    @Test
    void createLessThanZero() {
        input.setPrice(BigDecimal.valueOf(5000));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(input));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹에 상품이 없으면 IllegalArgumentException 발생")
    @Test
    void createNull() {
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(emptyMenuGroupMenu()));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹에 속한 상품 금액의 합이 메뉴 가격보다 작으면 IllegalArgumentException 발생")
    @Test
    void createLessThanSum() {
        Menu hoho = defaultMenu();
        hoho.setPrice(BigDecimal.valueOf(8000));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(hoho));
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        Menu result = menuBo.create(defaultMenu());
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMenuProducts().size()).isEqualTo(1);
        assertThat(result.getMenuProducts().get(0).getMenuId()).isEqualTo(1L);
        assertThat(result.getMenuProducts().get(0).getQuantity()).isEqualTo(2L);
    }

    @DisplayName("메뉴 목록 조회")
    @Test
    void list() {
        menuBo.create(defaultMenu());

        List<Menu> result = menuBo.list();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getMenuGroupId()).isEqualTo(1L);
        assertThat(result.get(0).getMenuProducts().size()).isEqualTo(1);
    }
}
