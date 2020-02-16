package kitchenpos.bo;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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

    private Menu menu;
    private MenuProduct menuProduct;
    private Product product;
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        final List<MenuProduct> menuProductList = new ArrayList<>();

        menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1);
        menuProductList.add(menuProduct);

        product = new Product();
        product.setId(1L);
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16000));

        menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("두마리메뉴");

        menu = new Menu();
        menu.setId(1L);
        menu.setMenuGroupId(1L);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(menuProductList); ;

        menuBo = new MenuBo(menuDao, menuGroupDao, menuProductDao, productDao);
    }

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.of(product));
        given(menuProductDao.save(any(MenuProduct.class)))
                .willReturn(menuProduct);
        given(menuDao.save(any(Menu.class))).willReturn(menu);

        // when
        final Menu actual = menuBo.create(menu);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(menu.getName());
        assertThat(actual.getPrice()).isEqualTo(menu.getPrice());
        assertThat(actual.getMenuGroupId()).isEqualTo(menuGroup.getId());
    }

    @DisplayName("메뉴의 가격은 0보다 커야한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-100000"})
    void priceNull(final BigDecimal price) {
        // given
        menu.setPrice(price);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴는 메뉴 그룹에 존재 해야한다.")
    @Test
    void menuGroupException() {
        // when
        when(menuGroupDao.existsById(anyLong()))
                .thenReturn(false);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴에 담기는 상품이 존재해야한다.")
    @Test
    void productExist() {
        menu.setMenuProducts(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("상품의 총액 = 상품의 가격 * 상품의 수량")
    @Test
    void productSum() {
        assertThat(menu.getPrice()).isEqualTo(getProductSum());
    }

    private BigDecimal getProductSum() {
        return product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity()));
    }

    @DisplayName("메뉴 가격이 상품의 총액을 넘을 수 없다.")
    @Test
    void menuPriceException() {
        // when
        menu.setPrice(BigDecimal.valueOf(17000));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴리스트를 검색할 수 있다.")
    @Test
    void list() {
        // given
        given(menuDao.findAll())
                .willReturn(Collections.singletonList(menu));

        // when
        List<Menu> menus = menuBo.list();
        Menu actual = menus.get(0);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(menu.getName());
    }
}
