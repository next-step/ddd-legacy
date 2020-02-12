package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuBoTest {

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private MenuProductDao menuProductDao;

    @InjectMocks
    private MenuBo menuBo;

    private Menu menu;

    private static Stream<BigDecimal> prices() {
        return Stream.of(null, BigDecimal.valueOf(-1));
    }

    @BeforeEach
    void setUp() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);

        menu = new Menu();
        menu.setId(1L);
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(menuProducts);
        menu.setName("name");
        menu.setPrice(BigDecimal.ONE);

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(BigDecimal.ONE);

        when(menuGroupDao.existsById(anyLong())).thenReturn(true);
        when(productDao.findById(anyLong())).thenReturn(Optional.of(product));
        when(menuProductDao.save(menuProduct)).thenReturn(menuProduct);
        when(menuDao.save(menu)).thenReturn(menu);
    }

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void create() {
        assertThat(menuBo.create(menu)).isEqualTo(menu);
    }

    @DisplayName("메뉴의 가격이 책정되지 않았거나 `0`보다 작은 값인 경우, 예외를 발생시킨다.")
    @ParameterizedTest
    @MethodSource("prices")
    void exceptionWithPrice(final BigDecimal price) {
        menu.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴가 어떤 메뉴그룹에도 속하지 않은 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithMenuGroup() {
        when(menuGroupDao.existsById(anyLong())).thenReturn(false);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("존재하지 않는 메뉴상품이 포함 된 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithMenuProduct() {
        when(productDao.findById(anyLong())).thenThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 합산금액이 각 메뉴상품의 가격과 개수를 곱한 값들의 합보다 큰 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithPrice() {
        menu.setPrice(BigDecimal.TEN);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴 목록을 조회할 수 있다.")
    @Test
    void list() {
        when(menuDao.findAll()).thenReturn(new ArrayList<>());
        assertThat(menuBo.list()).isEmpty();
    }
}
