package kitchenpos.bo;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.spy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {

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

    @DisplayName("메뉴는 하나씩 등록할수있다., 하나의 메뉴에 여러 상품을 등록할수있다")
    @Test
    void createOneMenu() {
        //given
        final Menu menu = createMenu(1L);
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        menu.getMenuProducts().forEach(
            menuProduct -> {
                Product product = new Product();
                product.setId(menuProduct.getProductId());
                product.setPrice(BigDecimal.valueOf(1000L));
                product.setName("상품_" + menuProduct.getProductId());

                given(productDao.findById(menuProduct.getProductId()))
                    .willReturn(Optional.of(product));

                menuProduct.setSeq(new Random().nextLong());
                given(menuProductDao.save(menuProduct)).willReturn(menuProduct);
            });
        given(menuDao.save(menu)).willReturn(menu);

        //when
        Menu newMenu = menuBo.create(menu);

        //then
        Assertions.assertThat(newMenu).isEqualTo(menu);
        Assertions.assertThat(newMenu.getMenuProducts().size())
            .isEqualTo(menu.getMenuProducts().size());
    }

    @DisplayName("메뉴 정보는 이름과 가격(필수)이다.")
    @Test
    void menuRequired() {
        //given
        final Menu menu = spy(createMenu());
        given(menu.getPrice()).willReturn(null);

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격은 음수일수 없다.")
    @Test
    void priceNoneNegative() {
        //given
        final Menu menu = spy(createMenu());
        given(menu.getPrice()).willReturn(BigDecimal.valueOf(-1000L));

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴들은 하나의 그룹에 속해야 한다.")
    @Test
    void inGroup() {
        //given
        final Menu menu = spy(createMenu());
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(false);

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격은 각 상품의 가격의 합보다 클수없다.")
    @Test
    void notFoundProduct() {
        //given
        final Menu menu = spy(createMenu());
        given(menu.getPrice()).willReturn(BigDecimal.valueOf(Long.MAX_VALUE));
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.empty());

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격은 각 상품의 가격의 합보다 클수없다.")
    @Test
    void pricelessThanProductSum() {
        //given
        final Menu menu = spy(createMenu());

        BigDecimal sum = BigDecimal.ZERO;
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        for (MenuProduct menuProduct : menu.getMenuProducts()) {
            Product product = new Product();
            product.setName("상품:" + menuProduct.getProductId());
            product.setId(menuProduct.getProductId());
            product.setPrice(BigDecimal.valueOf(1000L));
            given(productDao.findById(menuProduct.getProductId())).willReturn(Optional.of(product));

            sum = sum.add(product.getPrice());
        }

        given(menu.getPrice()).willReturn(sum.add(BigDecimal.ONE));

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("전체 메뉴 목록을 조회 할수있다.")
    @Test
    void allMenu() {
        //given
        Menu menu = createMenu();
        menu.setId(1L);
        List<Menu> menus = Arrays.asList(menu);
        given(menuDao.findAll()).willReturn(menus);
        given(menuProductDao.findAllByMenuId(menu.getId())).willReturn(menu.getMenuProducts());

        //when then
        Assertions.assertThat(menuBo.list()).containsAll(menus);
    }

    private Menu createMenu() {
        Menu menu = new Menu();
        menu.setName("name");
        menu.setPrice(BigDecimal.valueOf(1000L));
        menu.setMenuGroupId(1L);

        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(1L);
        menuProduct1.setQuantity(1);

        MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setProductId(2L);
        menuProduct2.setQuantity(1);

        menu.setMenuProducts(Arrays.asList(menuProduct1, menuProduct2));
        return menu;
    }

    private Menu createMenu(Long id) {
        Menu menu = createMenu();
        menu.setId(id);
        return menu;
    }

}
