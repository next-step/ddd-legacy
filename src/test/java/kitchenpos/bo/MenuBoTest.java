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
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    private MenuProduct mockMenuProduct1;
    private MenuProduct mockMenuProduct2;
    private Product mockProduct1;
    private Product mockProduct2;
    private Menu newMenu;
    private List<Menu> mockMenus;

    @BeforeEach
    void beforeEach() {
        mockMenuProduct1 = new MenuProduct();
        mockMenuProduct1.setSeq(1L);
        mockMenuProduct1.setMenuId(1L);
        mockMenuProduct1.setProductId(1L);
        mockMenuProduct1.setQuantity(1);

        mockMenuProduct2 = new MenuProduct();
        mockMenuProduct2.setSeq(2L);
        mockMenuProduct2.setMenuId(1L);
        mockMenuProduct2.setProductId(2L);
        mockMenuProduct2.setQuantity(2);

        mockProduct1 = new Product();
        mockProduct1.setId(1L);
        mockProduct1.setName("저세상치킨");
        mockProduct1.setPrice(BigDecimal.valueOf(1000));

        mockProduct2 = new Product();
        mockProduct2.setId(2L);
        mockProduct2.setName("저세상감튀");
        mockProduct2.setPrice(BigDecimal.valueOf(500));


        /**
         * 새로운 메뉴
         */
        newMenu = new Menu();
        newMenu.setName("저세상세트");
        newMenu.setPrice(BigDecimal.valueOf(1000)); // 2000원 초과 불가
        newMenu.setMenuGroupId(1L);
        newMenu.setMenuProducts(new ArrayList(Arrays.asList(mockMenuProduct1, mockMenuProduct2)));

        /**
         * 메뉴 리스트
         */
        mockMenus = new ArrayList<>();

        LongStream.range(1, 100).forEach(i -> {

            Menu tempMenu = new Menu();
            tempMenu.setId(i);
            tempMenu.setName("메뉴" + i);
            tempMenu.setPrice(BigDecimal.valueOf(1000)); // 2000원 초과 불가
            tempMenu.setMenuGroupId(1L);
            tempMenu.setMenuProducts(new ArrayList(Arrays.asList(mockMenuProduct1, mockMenuProduct2)));

            mockMenus.add(tempMenu);
        });
    }

    @DisplayName("새로운 메뉴를 생성할 수 있다.")
    @Test
    void create() {

        given(menuGroupDao.existsById(any())).willReturn(true);
        given(productDao.findById(mockProduct1.getId())).willReturn(Optional.of(mockProduct1));
        given(productDao.findById(mockProduct2.getId())).willReturn(Optional.of(mockProduct2));
        given(menuDao.save(newMenu)).willAnswer((invocation) -> {
            newMenu.setId(1L);
            return newMenu;
        });
        given(menuProductDao.save(mockMenuProduct1)).willReturn(mockMenuProduct1);
        given(menuProductDao.save(mockMenuProduct2)).willReturn(mockMenuProduct2);

        // when
        Menu result = menuBo.create(newMenu);

        // then
        assertThat(result.getId()).isEqualTo(newMenu.getId());
        assertThat(result.getName()).isEqualTo(newMenu.getName());
        assertThat(result.getPrice()).isEqualTo(newMenu.getPrice());
        assertThat(result.getMenuGroupId()).isEqualTo(newMenu.getMenuGroupId());
        assertThat(result.getMenuProducts().size()).isEqualTo(2);
        assertThat(result.getMenuProducts().size()).isEqualTo(newMenu.getMenuProducts().size());
        assertThat(result.getMenuProducts().get(0).getProductId()).isEqualTo(newMenu.getMenuProducts().get(0).getProductId());
    }

    // TODO mocking check
    @DisplayName("메뉴는 1개의 메뉴그룹에 속한다.")
    @ParameterizedTest
    @MethodSource(value = "provideInvalidMenuGroupIds")
    void shouldBeInMenuGroup(Long menuGroupId) {
        // given
        newMenu.setMenuGroupId(menuGroupId);

        given(menuGroupDao.existsById(any())).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Long> provideInvalidMenuGroupIds() {
        return Stream.of(null, 100L);
    }

    @DisplayName("메뉴는 1개 이상의 제품으로 구성된다.")
    @Test
    void shouldIncludeAtLeastOneProduct() {
        // given
        newMenu.setMenuProducts(new ArrayList<>());
        given(menuGroupDao.existsById(any())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격은 0원 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -10, -1})
    void priceShouldBeOver0(int price) {
        // given
        newMenu.setPrice(BigDecimal.valueOf(price));

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 가격은 구성된 메뉴제품들의 가격 총합을 초과할 수 없다.")
    @ParameterizedTest
    @ValueSource(longs = {2001, 10000})
    void priceShouldNotOverSumOfProductPrices(Long price) {
        // given
        newMenu.setPrice(BigDecimal.valueOf(price));

        given(menuGroupDao.existsById(any())).willReturn(true);
        given(productDao.findById(mockProduct1.getId())).willReturn(Optional.of(mockProduct1));
        given(productDao.findById(mockProduct2.getId())).willReturn(Optional.of(mockProduct2));

        // when
        // then
        assertThatThrownBy(() -> {
            menuBo.create(newMenu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("전체 메뉴 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        given(menuDao.findAll()).willReturn(mockMenus);
        given(menuProductDao.findAllByMenuId(any(Long.class))).willReturn(new ArrayList<>(Arrays.asList(mockMenuProduct1, mockMenuProduct2)));

        // when
        final List<Menu> result = menuBo.list();

        // then
        assertThat(result.size()).isEqualTo(mockMenus.size());
        assertThat(result.get(0).getId()).isEqualTo(mockMenus.get(0).getId());
        assertThat(result.get(0).getName()).isEqualTo(mockMenus.get(0).getName());
        assertThat(result.get(0).getMenuProducts()).isNotNull();
    }
}
