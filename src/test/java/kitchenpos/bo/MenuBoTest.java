package kitchenpos.bo;

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
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @Mock
    private MenuGroupDao menuGroupDao;
    @Mock
    private ProductDao productDao;
    @Mock
    private MenuDao menuDao;
    @Mock
    private MenuProductDao menuProductDao;

    @InjectMocks
    private MenuBo menuBo;

    @DisplayName("메뉴를 등록할 수 있다")
    @Test
    void createMenu() {
        Menu expected = new Menu();
        expected.setId(1L);
        expected.setName("엄청이득메뉴");
        expected.setPrice(new BigDecimal(10000));
        expected.setMenuGroupId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("엄청비싼음식");
        product.setPrice(new BigDecimal(1000000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);
        expected.setMenuProducts(Arrays.asList(menuProduct));

        //given
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(1L)).willReturn(Optional.of(product));
        given(menuDao.save(expected)).willReturn(expected);

        //when
        Menu result = menuBo.create(expected);

        //then
        assertAll(
                () -> assertThat(result.getPrice()).isEqualTo(expected.getPrice()),
                () -> assertThat(result.getMenuGroupId()).isEqualTo(expected.getMenuGroupId()),
                () -> assertThat(result.getMenuProducts()).isEqualTo(expected.getMenuProducts())
        );
    }

    @DisplayName("메뉴를 조회할 수 있다")
    @Test
    void listMenu() {
        Menu expected = new Menu();
        expected.setId(1L);
        expected.setName("엄청이득메뉴");
        expected.setPrice(new BigDecimal(10000));
        expected.setMenuGroupId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setName("엄청비싼음식");
        product.setPrice(new BigDecimal(10000000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(2L);
        expected.setMenuProducts(Arrays.asList(menuProduct));

        //given
        given(menuDao.findAll()).willReturn(Arrays.asList(expected));
        given(menuProductDao.findAllByMenuId(1L)).willReturn(Arrays.asList(menuProduct));

        //when
        List<Menu> result = menuBo.list();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("가격이 올바르지 않은 메뉴의 등록")
    @ParameterizedTest
    @MethodSource("createInvalidPriceMenu")
    void invalidPriceMenu(BigDecimal price) {
        Menu expected = new Menu();
        expected.setId(1L);
        expected.setName("환상의메뉴");
        expected.setPrice(price);
        expected.setMenuGroupId(1L);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    static Stream<BigDecimal> createInvalidPriceMenu() {
        return Stream.of(null, new BigDecimal(-10000));
    }

    @DisplayName("존재하지않는 메뉴그룹을 포함한 메뉴의 등록")
    @Test
    void invalidMenuGroupMenu() {
        Menu expected = new Menu();
        expected.setId(1L);
        expected.setName("엄청이득메뉴");
        expected.setPrice(new BigDecimal(10000));
        expected.setMenuGroupId(1L);

        //given
        given(menuGroupDao.existsById(1L)).willReturn(false);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    @DisplayName("포함된 상품의 가격 합보다 비싼 메뉴의 등록")
    @Test
    void expensiveMenu() {
        Menu expected = new Menu();
        expected.setId(1L);
        expected.setName("호갱메뉴");
        expected.setPrice(new BigDecimal(100000));
        expected.setMenuGroupId(1L);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("엄청싼음식");
        product1.setPrice(new BigDecimal(1000));
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("엄청싼음식");
        product2.setPrice(new BigDecimal(5500));

        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product1.getId());
        menuProduct1.setQuantity(2L);
        MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setProductId(product2.getId());
        menuProduct2.setQuantity(1L);
        expected.setMenuProducts(Arrays.asList(menuProduct1, menuProduct2));

        //given
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(product1.getId())).willReturn(Optional.of(product1));
        given(productDao.findById(product2.getId())).willReturn(Optional.of(product2));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }
}
