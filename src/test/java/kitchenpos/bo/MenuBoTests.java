package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTests {

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

    private static final Menu mockDefaultMenu = new Menu();
    private static final List<Menu> mockDefaultMenus = new ArrayList<>();

    private static final Product mockDefaultProduct = new Product();

    private static final MenuProduct mockDefaultMenuProduct = new MenuProduct();
    private static final List<MenuProduct> mockDefaultMenuProducts = new ArrayList<>();

    @BeforeAll
    public static void setupAll() {
        setupDefaultMenu();
        setupDefaultMenus();
        setupDefaultProduct();
        setupDefaultMenuProduct();
        setupDefaultMenuProducts();
    }

    @DisplayName("메뉴 전체 조회 시 메뉴에 맞는 메뉴 상품들을 가져오는지 확인")
    @Test
    public void findAllTest() {
        given(menuDao.findAll()).willReturn(mockDefaultMenus);
        given(menuProductDao.findAllByMenuId(1L)).willReturn(mockDefaultMenuProducts);

        List<Menu> menuList = menuBo.list();

        assertThat(menuList.get(0).getId()).isEqualTo(1L);
        assertThat(menuList.get(0).getMenuProducts().get(0).getMenuId()).isEqualTo(1L);
    }

    @DisplayName("정상적인 값들로 메뉴 생성 시도 성공")
    @ParameterizedTest
    @MethodSource("validCreateRequest")
    public void createMenuSuccess(Menu validMenu, MenuProduct validMenuProduct) {
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(2L)).willReturn(Optional.of(mockDefaultProduct));
        given(menuDao.save(validMenu)).willReturn(mockDefaultMenu);
        given(menuProductDao.save(validMenuProduct)).willReturn(mockDefaultMenuProduct);

        Menu saved = menuBo.create(validMenu);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getMenuProducts()).contains(mockDefaultMenuProduct);
    }

    private static Object[] validCreateRequest() {
        Menu validMenu = new Menu();
        MenuProduct validMenuProduct = new MenuProduct();
        List<MenuProduct> validMenuProducts = new ArrayList<>();
        validMenuProducts.add(validMenuProduct);

        validMenuProduct.setSeq(1L);
        validMenuProduct.setMenuId(1L);
        validMenuProduct.setQuantity(10);
        validMenuProduct.setProductId(2L);

        validMenu.setMenuGroupId(1L);
        validMenu.setPrice(BigDecimal.valueOf(16000));
        validMenu.setName("createRequestMenu");
        validMenu.setMenuProducts(validMenuProducts);

        return new Object[]{Arguments.of(validMenu, validMenuProduct)};
    }

    @DisplayName("0보다 작은 가격으로 메뉴 생성 시도 시 실패")
    @ParameterizedTest
    @MethodSource("invalidPriceMenus")
    public void createMenuFailWithInvalidPrice(Menu invalidPriceMenu) {
        assertThatThrownBy(() -> menuBo.create(invalidPriceMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Menu> invalidPriceMenus() {
        Menu negativeNumberMenu = new Menu();
        negativeNumberMenu.setPrice(BigDecimal.valueOf(-1000));

        Menu noPriceMenu = new Menu();

        return Stream.of(negativeNumberMenu, noPriceMenu);
    }

    @DisplayName("메뉴에 속한 메뉴 상품 가격의 총합보다 높은 가격으로 메뉴 생성 시도 시 실패")
    @Test
    public void createMenuFailWhenMenuPriceIsBiggerThanPriceSum() {
        Menu tooBigPriceMenu = new Menu();
        tooBigPriceMenu.setMenuGroupId(1L);
        tooBigPriceMenu.setPrice(BigDecimal.valueOf(999999999));
        tooBigPriceMenu.setMenuProducts(mockDefaultMenuProducts);

        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(2L)).willReturn(Optional.of(mockDefaultProduct));

        assertThatThrownBy(() -> menuBo.create(tooBigPriceMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹에 속하지 않은 메뉴로 생성 시도 시 실패")
    @Test
    public void createMenuFailWithNotInMenuGroup() {
        Menu notInMenuGroup = new Menu();
        notInMenuGroup.setPrice(BigDecimal.valueOf(1));
        notInMenuGroup.setMenuGroupId(1L);

        given(menuGroupDao.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> menuBo.create(notInMenuGroup)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("없는 상품으로 메뉴 생성 시도 시 실패")
    @Test
    public void createMenuFailWithNonExistProduct() {
        Menu notExistProductMenu = new Menu();
        notExistProductMenu.setPrice(BigDecimal.valueOf(1));
        notExistProductMenu.setMenuGroupId(1L);
        notExistProductMenu.setMenuProducts(new ArrayList<>());

        given(menuGroupDao.existsById(1L)).willReturn(true);

        assertThatThrownBy(() -> menuBo.create(notExistProductMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    private static void setupDefaultMenu() {
        mockDefaultMenu.setId(1L);
        mockDefaultMenu.setMenuGroupId(1L);
        mockDefaultMenu.setPrice(BigDecimal.valueOf(16000));
        mockDefaultMenu.setName("createRequestMenu");
        mockDefaultMenu.setMenuProducts(mockDefaultMenuProducts);
    }

    private static void setupDefaultMenus() {
        mockDefaultMenus.add(mockDefaultMenu);
    }

    private static void setupDefaultProduct() {
        mockDefaultProduct.setId(2L);
        mockDefaultProduct.setName("testProduct");
        mockDefaultProduct.setPrice(BigDecimal.valueOf(16000));
    }

    private static void setupDefaultMenuProduct() {
        mockDefaultMenuProduct.setSeq(1L);
        mockDefaultMenuProduct.setMenuId(1L);
        mockDefaultMenuProduct.setQuantity(10);
        mockDefaultMenuProduct.setProductId(2L);
    }

    private static void setupDefaultMenuProducts() {
        mockDefaultMenuProducts.add(mockDefaultMenuProduct);
    }
}
