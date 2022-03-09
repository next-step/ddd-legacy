package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @InjectMocks
    MenuService menuService;
    @Mock
    MenuRepository menuRepository;
    @Mock
    MenuGroupRepository menuGroupRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    PurgomalumClient 비속어_판별기;

    @DisplayName(value = "메뉴를 등록 할 수 있다")
    @Test
    void create_success() {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        String 메뉴명 = "후라이드치킨";
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17000L);
        given(등록할_메뉴.getPrice()).willReturn(메뉴가격);
        given(등록할_메뉴.getName()).willReturn(메뉴명);
        given(비속어_판별기.containsProfanity(메뉴명)).willReturn(false);

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        MenuProduct 메뉴구성상품 = mock(MenuProduct.class);
        List<MenuProduct> 메뉴구성상품_리스트 = new ArrayList<>(Arrays.asList(메뉴구성상품));
        given(등록할_메뉴.getMenuProducts()).willReturn(메뉴구성상품_리스트);

        Product 상품 = mock(Product.class);
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
        given(상품.getPrice()).willReturn(BigDecimal.valueOf(17500L));

        given(메뉴구성상품.getQuantity()).willReturn(1L);
        given(productRepository.findById(any())).willReturn(Optional.of(상품));

        //when
        menuService.create(등록할_메뉴);

        //then
        verify(menuRepository,times(1)).save(any(Menu.class));
    }

    @DisplayName(value = "반드시 0원 이상의 메뉴가격을 가져야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴가격")
    void create_fail_invalid_price(final BigDecimal 메뉴가격) {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(메뉴가격);

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴는 반드시 하나의 메뉴그룹을 지정하여 등록해야 한다")
    @Test
    void create_fail_menu_should_belongs_to_menu_group() {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));
        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "메뉴는 반드시 하나 이상의 메뉴구성상품(menuProduct)을 포함하고 있어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴구성상품_리스트")
    void create_fail_menu_should_have_gt_1_menu_product(final List<MenuProduct> 메뉴구성상품_리스트) {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        given(등록할_메뉴.getMenuProducts()).willReturn(메뉴구성상품_리스트);

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "상품(product)의 개수와 메뉴상품구성(menuProduct)의 개수는 일치해야 한다")
    @Test
    void create_fail_menu_product_size_should_eq_product_size() {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        MenuProduct 메뉴상품구성 = mock(MenuProduct.class);
        given(등록할_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(메뉴상품구성)));

        Product 상품1 = mock(Product.class);
        Product 상품2 = mock(Product.class);
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품1, 상품2));

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "각 메뉴구성상품(menuProduct)의 양(quantity)은 0이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴구성상품_개수")
    void create_fail_menu_product_quantity_should_gt_0(final long 메뉴구성상품_개수) {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        MenuProduct 메뉴상품구성 = mock(MenuProduct.class);
        given(등록할_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(메뉴상품구성)));

        Product 상품 = mock(Product.class);
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));

        given(메뉴상품구성.getQuantity()).willReturn(메뉴구성상품_개수);

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴는 존재하는 상품만 포함할 수 있다")
    @Test
    void create_fail_product_no_exist() {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        MenuProduct 메뉴상품구성 = mock(MenuProduct.class);
        given(등록할_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(메뉴상품구성)));

        Product 상품 = mock(Product.class);
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));

        given(메뉴상품구성.getQuantity()).willReturn(1L);
        given(productRepository.findById(any())).willReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "메뉴의 가격이 각 메뉴구성상품의 합보다 클 수 없다")
    @Test
    void create_fail_menu_price_shoud_lt_sum_of_menu_product() {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        MenuProduct 메뉴상품구성 = mock(MenuProduct.class);
        given(등록할_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(메뉴상품구성)));

        Product 상품 = mock(Product.class);
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
        given(상품.getPrice()).willReturn(BigDecimal.valueOf(17500L));

        given(메뉴상품구성.getQuantity()).willReturn(1L);
        given(productRepository.findById(any())).willReturn(Optional.of(상품));
        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴는 반드시 메뉴명을 가지고 있어야 하며, 비속어를 포함할 수 없다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴명")
    void create_fail_invalid_name(final String 메뉴명) {
        //given
        Menu 등록할_메뉴 = mock(Menu.class);
        given(등록할_메뉴.getPrice()).willReturn(BigDecimal.valueOf(17000L));
        given(등록할_메뉴.getName()).willReturn(메뉴명);
        lenient().when(비속어_판별기.containsProfanity(메뉴명)).thenReturn(true);

        MenuGroup 메뉴그룹 = mock(MenuGroup.class);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        MenuProduct 메뉴상품구성 = mock(MenuProduct.class);
        given(등록할_메뉴.getMenuProducts()).willReturn(new ArrayList<>(Arrays.asList(메뉴상품구성)));

        Product 상품 = mock(Product.class);
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
        given(상품.getPrice()).willReturn(BigDecimal.valueOf(17500L));

        given(메뉴상품구성.getQuantity()).willReturn(1L);
        given(productRepository.findById(any())).willReturn(Optional.of(상품));

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "변경하려는 메뉴의 가격은 존재해야하며, 0원 이상이어야 한다")
    @Test
    void changePrice_fail_invalid_menu_price() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴 가격을 각 메뉴구성상품 가격의 합보다 크게 변경할 수 없다")
    @Test
    void changePrice_fail_menu_price_gt_sum_of_menu_product_price() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴의 판매상태를 판매중으로 변경할 수 있다")
    @Test
    void display_success() {
        //given

        //when

        //then
    }

    @DisplayName(value = "존재하는 메뉴만 판매상태를 판매중으로 변경할 수 있다")
    @Test
    void display_fail_menu_not_exist() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴 가격이 각 메뉴구성상품 가격의 합보다 큰경우 판매중으로 변경할 수 없다")
    @Test
    void display_fail_menu_price_gt_sum_of_menu_product() {
        //given

        //when

        //then
    }

    @DisplayName(value = "메뉴의 판매상태를 판매중단으로 변경할 수 있다")
    @Test
    void hide_success() {
        //given

        //when

        //then
    }

    @DisplayName(value = "존재하는 메뉴만 판매상태를 판매중단으로 변경할 수 있다")
    @Test
    void hide_fail_menu_not_exist() {
        //given

        //when

        //then
    }

    @DisplayName(value = "전체 메뉴를 조회할 수 있다")
    @Test
    void findAll_success() {
        //given

        //when

        //then
    }

    private static Stream<BigDecimal> 잘못된_메뉴가격() {
        return Stream.of(
                null,
                BigDecimal.valueOf(-1)
        );
    }

    private static Stream<List<MenuProduct>> 잘못된_메뉴구성상품_리스트() {
        return Stream.of(
                null,
                new ArrayList<>()
        );
    }

    private static Stream<Long> 잘못된_메뉴구성상품_개수() {
        return Stream.of(
                -1L
        );
    }

    private static Stream<String> 잘못된_메뉴명() {
        return Stream.of(
                null,
                "xx같은후라이드치킨메뉴"
        );
    }
}