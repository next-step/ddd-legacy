package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
@ExtendWith(MockitoExtension.class)
class MenuBoTest extends Fixtures {

    @InjectMocks
    private MenuBo menuBo;

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Test
    @DisplayName("메뉴를 등록 할 수 있다.")
    void create() {
        menu.setMenuProducts(menuProducts);

        given(menuDao.save(menu)).willReturn(menu);
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(menuProducts.get(0).getProductId())).willReturn(Optional.of(products.get(0)));
        given(productDao.findById(menuProducts.get(1).getProductId())).willReturn(Optional.of(products.get(1)));
        given(menuProductDao.save(menuProducts.get(0))).willReturn(menuProducts.get(0));
        given(menuProductDao.save(menuProducts.get(1))).willReturn(menuProducts.get(1));

        final Menu savedMenu = menuBo.create(menu);

        assertThat(savedMenu).isNotNull();
        assertThat(savedMenu.getId()).isEqualTo(menu.getId());
        assertThat(savedMenu.getName()).isEqualTo(menu.getName());
        assertThat(savedMenu.getPrice()).isEqualTo(menu.getPrice());
        assertThat(savedMenu.getMenuGroupId()).isEqualTo(menu.getMenuGroupId());
        assertThat(savedMenu.getMenuProducts().size()).isEqualTo(menu.getMenuProducts().size());
        assertThat(savedMenu.getMenuProducts().get(0).getProductId())
                .isEqualTo(menu.getMenuProducts().get(0).getProductId());
        assertThat(savedMenu.getMenuProducts().get(0).getQuantity())
                .isEqualTo(menu.getMenuProducts().get(0).getQuantity());
        assertThat(savedMenu.getMenuProducts().get(0).getMenuId())
                .isEqualTo(menu.getMenuProducts().get(0).getMenuId());
        assertThat(savedMenu.getMenuProducts().get(0).getSeq())
                .isEqualTo(menu.getMenuProducts().get(0).getSeq());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-16000"})
    @DisplayName("메뉴의 가격이 바르지 않으면 등록할 수 없다.")
    void create_price_validation(final BigDecimal price) {
        final Menu resolvedMenu = menu;
        resolvedMenu.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(resolvedMenu));
    }

    @Test
    @DisplayName("메뉴 그룹이 존재 하지 않으면 등록할 수 없다.")
    void create_not_exist_groupId() {
        final Menu resolvedMenu = menu;
        resolvedMenu.setMenuGroupId(null);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴의 가격이, 메뉴에 속한 상품 금액의 합 보다 크면 안된다.")
    void create_price_isEqualTo_product_sum() {
        final Menu resolvedMenu = menu;
        resolvedMenu.setPrice(BigDecimal.valueOf(15_000));

        menu.setMenuProducts(menuProducts);

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(menuProducts.get(0).getProductId()))
                .willReturn(Optional.of(products.get(0)));
        given(productDao.findById(menuProducts.get(1).getProductId()))
                .willReturn(Optional.of(products.get(1)));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuBo.create(menu));
    }

    @Test
    @DisplayName("메뉴를 조회할 수 있다.")
    void list() {

        final List<Menu> menus = Arrays.asList(menu);
        given(menuDao.findAll()).willReturn(menus);
        given(menuProductDao.findAllByMenuId(menu.getId())).willReturn(menuProducts);

        final List<Menu> list = menuBo.list();

        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(menus.size());
        assertThat(list.get(0).getMenuProducts().get(0).getSeq()).isEqualTo(menuProducts.get(0).getSeq());
        assertThat(list.get(0).getMenuProducts().get(0).getMenuId()).isEqualTo(menuProducts.get(0).getMenuId());
        assertThat(list.get(0).getMenuProducts().get(0).getQuantity()).isEqualTo(menuProducts.get(0).getQuantity());
        assertThat(list.get(0).getMenuProducts().get(0).getProductId()).isEqualTo(menuProducts.get(0).getProductId());
    }
}
