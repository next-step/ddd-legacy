package kitchenpos.fake;

import kitchenpos.TestFixture;
import kitchenpos.bo.MenuBo;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeMenuBoTest {
    public static final long LONG_ONE = 1L;
    public static final long LONG_TWO = 2L;

    public static final int INT_ZERO = 0;
    public static final int INT_TWO = 2;

    private MenuBo menuBo;

    private MenuProductDao menuProductDao = new FakeMenuProductDao();

    private MenuDao menuDao = new FakeMenuDao();

    private MenuGroupDao menuGroupDao = new FakeMenuGroupDao();

    private ProductDao productDao = new FakeProductDao();

    @BeforeEach
    void setUp() {
        this.menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
    }

    @Test
    @DisplayName("메뉴 리스트 조회")
    void list() {
        MenuProduct menuProduct1 = TestFixture.generateMenuProductOne();
        MenuProduct menuProduct2 = TestFixture.generateMenuProductTwo();

        Menu menu1 = TestFixture.generateMenuOne();
        Menu menu2 = TestFixture.generateMenuTwo();

        menuDao.save(menu1);
        menuDao.save(menu2);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

        List<Menu> menus = menuBo.list();

        assertAll(
                () -> assertThat(menus.get(INT_ZERO).getId()).isEqualTo(menu1.getId()),
                () -> assertThat(menus.size()).isEqualTo(INT_TWO),
                () -> assertThat(menus).contains(menu1),
                () -> assertThat(menus).contains(menu2)
        );
    }

    @Test
    @DisplayName("메뉴 생성 정상 동작")
    void create() {
        Menu menu = TestFixture.generateMenuHasTwoProduct();

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(LONG_ONE);
        menuGroup.setName("간장 메뉴");

        Product product1 = createProduct(LONG_ONE, BigDecimal.TEN);
        Product product2 = createProduct(LONG_TWO, BigDecimal.ONE);

        menuDao.save(menu);
        menuGroupDao.save(menuGroup);
        productDao.save(product1);
        productDao.save(product2);

        Menu savedMenu = menuBo.create(menu);

        assertAll(
                () -> assertThat(savedMenu.getId()).isEqualTo(menu.getId()),
                () -> assertThat(savedMenu.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(savedMenu.getMenuProducts().size()).isEqualTo(menu.getMenuProducts().size()),
                () -> assertThat(savedMenu.getMenuProducts()).containsAll(menu.getMenuProducts())
        );
    }

    @DisplayName("메뉴 가격이 음수 이거나 null일 경우 생성 실패")
    @ParameterizedTest
    @ValueSource(strings = "-10")
    @NullSource
    void createFailByNegativePrice(BigDecimal price) {
        MenuProduct menuProduct1 = TestFixture.generateMenuProductOne();
        MenuProduct menuProduct2 = TestFixture.generateMenuProductTwo();

        Menu menu = TestFixture.generateMenuHasTwoProduct();
        menu.setPrice(price);

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    @DisplayName("메뉴 그룹 정보가 없을때 메뉴 생성 실패")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 9999)
    void createFailByNotExsistMenugroup(Long menugroupId) {
        MenuProduct menuProduct1 = TestFixture.generateMenuProductOne();
        MenuProduct menuProduct2 = TestFixture.generateMenuProductTwo();

        Menu menu = TestFixture.generateMenuHasTwoProduct();
        menu.setMenuGroupId(menugroupId);

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);
        menuProductDao.save(menuProduct2);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @Test
    @DisplayName("등록되지 않은 상품을 포함 시 생성 실패")
    void createFailByNotExistProduct() {
        Menu menu = TestFixture.generateMenuOne();

        menuDao.save(menu);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("메뉴 가격이 상품의 가격 합보다 클때 생성 실패")
    @ParameterizedTest
    @ValueSource(strings = "10000")
    void createFailByPriceGreaterThanProductPrice(BigDecimal price) {
        MenuProduct menuProduct1 = TestFixture.generateMenuProductOne();

        Menu menu = TestFixture.generateMenuOne();
        menu.setPrice(price);

        menuDao.save(menu);
        menuProductDao.save(menuProduct1);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    private Product createProduct(Long id, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(price);
        return product;
    }
}
