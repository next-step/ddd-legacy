package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import kitchenpos.model.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

class MenuBoTest extends MockTest {
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
        Product product = TestFixtures.veryExpensiveProduct();
        MenuProduct menuProduct = TestFixtures.menuProduct(product.getId(), 1L);
        Menu expected = TestFixtures.veryCheapMenu(Arrays.asList(menuProduct));

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
        MenuProduct menuProduct = TestFixtures.menuProduct(1L, 1L);
        Menu expected = TestFixtures.veryCheapMenu(Arrays.asList(menuProduct));

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
        Menu expected = TestFixtures.customPriceMenu(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    static Stream<BigDecimal> createInvalidPriceMenu() {
        return Stream.of(null, new BigDecimal(-10000));
    }

    @DisplayName("존재하지않는 메뉴그룹을 포함한 메뉴의 등록")
    @Test
    void invalidMenuGroupMenu() {
        Menu expected = TestFixtures.veryCheapMenu(Collections.emptyList());

        //given
        given(menuGroupDao.existsById(1L)).willReturn(false);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }

    @DisplayName("포함된 상품의 가격 합보다 비싼 메뉴의 등록")
    @Test
    void expensiveMenu() {

        Product product1 = TestFixtures.customPriceProduct(new BigDecimal(1000));
        Product product2 = TestFixtures.customPriceProduct(new BigDecimal(5500));
        MenuProduct menuProduct1 = TestFixtures.menuProduct(product1.getId(), 2L);
        MenuProduct menuProduct2 = TestFixtures.menuProduct(product2.getId(), 1L);
        Menu expected = TestFixtures.veryExpensiveMenu(Arrays.asList(menuProduct1, menuProduct2));

        //given
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(product1.getId())).willReturn(Optional.of(product1));
        given(productDao.findById(product2.getId())).willReturn(Optional.of(product2));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(expected));
    }
}
