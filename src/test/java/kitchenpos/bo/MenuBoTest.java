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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


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

    private Menu expectedMenu = null;
    private Product expectedProduct = null;
    private List<MenuProduct> expectedMenuProducts = new ArrayList<>();
    private MenuProduct expectedMenuProduct = null;

    @BeforeEach
    void setUp() {
        expectedMenu = new Menu();

        expectedProduct = new Product();
        expectedProduct.setId(1L);
        expectedProduct.setName("coke");
        expectedProduct.setPrice(BigDecimal.valueOf(1000L));

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("name");

        expectedMenuProduct = new MenuProduct();
        expectedMenuProduct.setProductId(expectedProduct.getId());
        expectedMenuProduct.setQuantity(3);
        expectedMenuProducts.add(expectedMenuProduct);

        expectedMenu.setMenuGroupId(menuGroup.getId());
        expectedMenu.setName("drinks");
        expectedMenu.setPrice(BigDecimal.valueOf(3000L));
        expectedMenu.setMenuProducts(expectedMenuProducts);

    }

    @DisplayName("메뉴를 등록하였다.")
    @Test
    void createMenu() {
        //given
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.of(expectedProduct));
        given(menuProductDao.save(any(MenuProduct.class))).willReturn(expectedMenuProduct);
        given(menuDao.save(any(Menu.class)))
                .willReturn(expectedMenu);
        //when
        final Menu actual = menuBo.create(expectedMenu);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expectedMenu.getName());
        assertThat(actual.getPrice()).isEqualTo(expectedMenu.getPrice());
        assertThat(actual.getMenuProducts()).isEqualTo(expectedMenuProducts);
    }

    @DisplayName("메뉴 금액은 0원 이상이다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-10000"})
    void shouldThrowIllegalArgumentExceptionWhenPriceLessThan0(final BigDecimal price) {
        //given
        expectedMenu.setPrice(price);
        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuBo.create(expectedMenu));
    }

    @DisplayName("존재하지 않는 메뉴 그룹을 메뉴에 추가할수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForNotExistMenuGroup() {
        //given
        given(menuGroupDao.existsById(anyLong())).willReturn(false);
        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuBo.create(expectedMenu));
    }

    @DisplayName("존재하지 않는 상품을 메뉴에 추가할수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForNotExistProduct() {
        //given
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.empty());
        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuBo.create(expectedMenu));
    }

    @DisplayName("메뉴 금액이 메뉴상품의 금액*수량 보다 작아야한다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForDifferentPrice() {
        //given
        given(menuGroupDao.existsById(anyLong())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.of(expectedProduct));
        expectedMenu.setPrice(BigDecimal.valueOf(4000L));

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuBo.create(expectedMenu));
    }

    @DisplayName("상품 목록을 확인할 수 있다.")
    @Test
    void getMenus() {
        //given
        expectedMenu.setId(1L);
        given(menuDao.findAll()).willReturn(Arrays.asList(expectedMenu));
        given(menuProductDao.findAllByMenuId(anyLong())).willReturn(expectedMenuProducts);
        //when
        List<Menu> actual = menuBo.list();

        //then
        assertThat(actual).isEqualTo(menuDao.findAll());
    }
}
