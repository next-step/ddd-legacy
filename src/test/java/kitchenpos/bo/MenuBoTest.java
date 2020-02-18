package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;

import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @Mock
    MenuDao menuDao;
    @Mock
    MenuGroupDao menuGroupDao;
    @Mock
    MenuProductDao menuProductDao;
    @Mock
    ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    @BeforeEach
    private void setup() {

    }

    @DisplayName("메뉴 가격이 0 이상일 때만 생성이 된다.")
    @ParameterizedTest
    @MethodSource("getPriceTestMenus")
    public void createMenuWithNegativePrice(Menu positivePriceMenu, Menu negativePriceMenu) {
        List<MenuProduct> mockMenuProducts = new ArrayList<>();
        given(menuProductDao.findAllByMenuId(anyLong())).willReturn(mockMenuProducts);
        given(menuGroupDao.existsById(1L)).willReturn(true);

        positivePriceMenu.setMenuProducts(mockMenuProducts);
        negativePriceMenu.setMenuProducts(mockMenuProducts);

        assertThat(menuBo.create(positivePriceMenu)).isEqualTo(positivePriceMenu);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(negativePriceMenu));

    }

    public static Object[] getPriceTestMenus() {
        Menu negativePriceMenu = new Menu();
        Menu positivePriceMenu = new Menu();

        negativePriceMenu.setId(1L);
        negativePriceMenu.setName("negativePriceMenu");
        negativePriceMenu.setPrice(BigDecimal.valueOf(-1000));
        negativePriceMenu.setMenuGroupId(1L);


        positivePriceMenu.setId(2L);
        positivePriceMenu.setName("positivePriceMenu");
        positivePriceMenu.setPrice(BigDecimal.valueOf(1000));
        positivePriceMenu.setMenuGroupId(1L);
        return new Object[] {Arguments.of(positivePriceMenu, negativePriceMenu)};
    }

    public Product getDefaultProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("후라이드 치킨");
        product.setPrice(BigDecimal.valueOf(1000L));
        return product;
    }
}