package kitchenpos.bo;

import static org.mockito.ArgumentMatchers.refEq;
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
import kitchenpos.model.Menu.MenuBuilder;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.MenuProduct.MenuProductBuilder;
import kitchenpos.model.Product;
import kitchenpos.model.Product.ProductBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @DisplayName("메뉴는 하나씩 등록할수있다. 하나의 메뉴에 여러 상품을 등록할수있다")
    @Test
    void createOneMenu() {
        //given
        final Menu menu = createMenu();
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        menu.getMenuProducts().forEach(
            menuProduct -> {
                Product product = createProduct(menuProduct.getProductId());
                given(productDao.findById(menuProduct.getProductId()))
                    .willReturn(Optional.of(product));
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

    @DisplayName("메뉴의 가격은 0원 이상이 아닐경우 실패한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1000", "-2000"})
    void menuPrice(BigDecimal price) {
        //given
        final Menu menu = createMenu();
        menu.setPrice(price);

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("존재 하지 않는 메뉴 그룹에 메뉴룰 등록할수 없다.")
    @Test
    void inGroup() {
        //given
        final Menu menu = createMenu();
        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(false);

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격은 각 상품의 가격의 합보다 클수없다.")
    @Test
    void notFoundProduct() {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(10000L));

        final Menu menu = createMenu();
        menu.setPrice(BigDecimal.valueOf(Long.MAX_VALUE));

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(anyLong())).willReturn(Optional.ofNullable(product));

        //when then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴의 가격은 각 상품의 가격의 합보다 클수없다.")
    @Test
    void pricelessThanProductSum() {
        //given
        final Menu menu = createMenu();
        BigDecimal sum = BigDecimal.ZERO;
        for (MenuProduct menuProduct : menu.getMenuProducts()) {
            Product product = createProduct(menuProduct.getProductId());
            given(productDao.findById(menuProduct.getProductId())).willReturn(Optional.of(product));
            sum = sum
                .add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }
        menu.setPrice(sum.add(BigDecimal.TEN));

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);

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
        return MenuBuilder.builder()
            .withName("name")
            .withMenuGroupId(1L)
            .withMenuProducts(Arrays.asList(createMenuProduct(1), createMenuProduct(2)))
            .withPrice(BigDecimal.valueOf(1000L))
            .build();
    }

    private MenuProduct createMenuProduct(long productId) {
        return MenuProductBuilder.builder()
            .withProductId(productId)
            .withQuantity(1)
            .build();
    }

    private Product createProduct(Long productId) {
        return ProductBuilder.builder()
            .withId(productId)
            .withPrice(BigDecimal.valueOf(1000L))
            .withName("상품_" + productId)
            .build();
    }
}

