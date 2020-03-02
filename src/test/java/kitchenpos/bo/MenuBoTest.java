package kitchenpos.bo;

import kitchenpos.TestFixture;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    private static final long MENU_ID_ONE = 1L;

    private static final int INT_TWO = 2;
    private static final int INT_ZERO = 0;

    @InjectMocks
    private MenuBo menuBo;

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private ProductDao productDao;

    @Test
    @DisplayName("메뉴 리스트 조회")
    void list() {
        Menu menu1 = TestFixture.generateMenuOne();
        Menu menu2 = TestFixture.generateMenuTwo();

        List<Menu> menufixtures = Arrays.asList(menu1, menu2);

        given(menuDao.findAll())
                .willReturn(menufixtures);

        List<Menu> menus = menuBo.list();

        assertAll(
                () -> assertThat(menus.get(INT_ZERO).getId()).isEqualTo(MENU_ID_ONE),
                () -> assertThat(menus.size()).isEqualTo(INT_TWO),
                () -> assertThat(menus).containsExactlyInAnyOrderElementsOf(menufixtures)
        );
    }

    @Test
    @DisplayName("메뉴 생성 정상 동작")
    void create() {
        Menu menu = TestFixture.generateMenuHasTwoProduct();

        menu.getMenuProducts()
                .forEach(menuProduct -> {
                    Product product = new Product();
                    product.setPrice(BigDecimal.TEN);
                    given(productDao.findById(menuProduct.getProductId())).willReturn(Optional.of(product));
                });

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(menuDao.save(menu)).willReturn(menu);

        Menu savedMenu = menuBo.create(menu);

        assertAll(
                () -> assertThat(savedMenu.getId()).isEqualTo(menu.getId()),
                () -> assertThat(savedMenu.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(savedMenu.getMenuProducts().size()).isEqualTo(menu.getMenuProducts().size()),
                () -> assertThat(savedMenu.getMenuProducts()).containsAll(menu.getMenuProducts())
        );
    }

    @DisplayName("메뉴 가격이 음수이거나 Null 일때 생성 실패")
    @ParameterizedTest
    @ValueSource(strings = "-10")
    @NullSource
    void createFailByNegativePrice(BigDecimal price) {
        Menu menu = TestFixture.generateMenuHasTwoProduct();
        menu.setPrice(price);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    @DisplayName("메뉴 그룹 정보가 없을때 메뉴 생성 실패")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 9999)
    void createFailByNotExsistMenugroup(Long groupId) {
        Menu menu = TestFixture.generateMenuHasTwoProduct();
        menu.setMenuGroupId(groupId);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("등록되지 않은 상품을 포함 시 생성 실패")
    @ParameterizedTest
    @ValueSource(longs = {999, 888})
    void createFailByNotExistProduct(Long productId) {
        MenuProduct menuProduct = TestFixture.generateMenuProductOne();
        menuProduct.setProductId(productId);

        Menu menu = TestFixture.generateMenuOne();
        menu.setMenuProducts(Arrays.asList(menuProduct));

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("메뉴 가격이 상품의 가격 합보다 클때 생성 실패")
    @ParameterizedTest
    @ValueSource(strings = "100")
    void createFailByPriceGreaterThanProductPrice(BigDecimal price) {
        Menu menu = TestFixture.generateMenuOne();
        menu.setPrice(price);

        menu.getMenuProducts()
                .forEach(menuProduct -> {
                    Product product = new Product();
                    product.setPrice(BigDecimal.ONE);
                    given(productDao.findById(menuProduct.getProductId())).willReturn(Optional.of(product));
                });

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }
}
