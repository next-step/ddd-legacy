package kitchenpos.bo;

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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private Menu input;
    private Menu saved;
    private Product product;
    private MenuProduct menuProduct;
    private MenuProduct badMenuProduct;

    @BeforeEach
    void setUp() {
        menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(2L);

        badMenuProduct = new MenuProduct();
        badMenuProduct.setProductId(2L);

        input = new Menu();
        input.setPrice(BigDecimal.valueOf(5000));
        input.setMenuGroupId(1L);
        input.setMenuProducts(Collections.singletonList(menuProduct));

        saved = new Menu();
        saved.setId(1L);
        saved.setPrice(BigDecimal.valueOf(5000));
        saved.setMenuGroupId(1L);
        input.setMenuProducts(Collections.singletonList(menuProduct));

        product = new Product();
        product.setPrice(BigDecimal.valueOf(5000));
    }

    @DisplayName("메뉴 생성 시 가격이 없거나, 0보다 작으면 IllegalArgumentException 발생")
    @ParameterizedTest
    @MethodSource("provideNegativePrice")
    void createLessThanZero(Menu parameter) {
        assertThrows(IllegalArgumentException.class, () -> menuBo.create(parameter));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹아이디가 없으면 IllegalArgumentException 발생")
    @Test
    void createLessThanZero() {
        given(menuGroupDao.existsById(anyLong()))
                .willReturn(false);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(input));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹에 상품이 없으면 IllegalArgumentException 발생")
    @Test
    void createNull() {
        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);

        given(productDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(input));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹에 속한 상품 금액의 합이 메뉴 가격보다 작으면 IllegalArgumentException 발생")
    @Test
    void createLessThanSum() {
        input.setMenuProducts(Collections.singletonList(badMenuProduct));
        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);

        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(input));
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        given(menuGroupDao.existsById(anyLong()))
                .willReturn(true);

        given(productDao.findById(anyLong()))
                .willReturn(Optional.of(product));

        given(menuDao.save(input))
                .willReturn(saved);

        given(menuProductDao.save(menuProduct))
                .willReturn(menuProduct);

        Menu result = menuBo.create(input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMenuProducts().size()).isEqualTo(1);
        assertThat(result.getMenuProducts().get(0).getMenuId()).isEqualTo(1L);
        assertThat(result.getMenuProducts().get(0).getQuantity()).isEqualTo(2L);
    }

    @DisplayName("메뉴 목록 조회")
    @Test
    void list() {
        given(menuDao.findAll())
                .willReturn(Collections.singletonList(saved));

        given(menuProductDao.findAllByMenuId(anyLong()))
                .willReturn(Collections.singletonList(new MenuProduct()));

        List<Menu> result = menuBo.list();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getMenuGroupId()).isEqualTo(1L);
        assertThat(result.get(0).getMenuProducts().size()).isEqualTo(1);
    }

    private static Stream<Arguments> provideNegativePrice() {
        Menu sample1 = new Menu();
        Menu sample2 = new Menu();
        sample2.setPrice(BigDecimal.valueOf(-2000));

        return Stream.of(
                Arguments.of(sample1),
                Arguments.of(sample2)
        );
    }
}
