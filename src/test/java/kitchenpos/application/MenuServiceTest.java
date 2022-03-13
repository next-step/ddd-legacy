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

import static kitchenpos.fixture.MenuFixture.MenuBuilder;
import static kitchenpos.fixture.MenuGroupFixture.MenuGroupBuilder;
import static kitchenpos.fixture.MenuProductFixture.MenuProductBuilder;
import static kitchenpos.fixture.ProductFixture.ProductBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

    @DisplayName(value = "메뉴를 등록 할 수 있다")
    @Test
    void create_success() {
        //given
        MenuProduct 메뉴구성상품 = new MenuProductBuilder().quantity(1L).build();
        Menu 등록할메뉴 = new MenuBuilder().name("후라이드치킨").price(BigDecimal.valueOf(17000L)).menuProducts(new ArrayList<>(Arrays.asList(메뉴구성상품))).build();
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Product 상품 = new ProductBuilder().price(BigDecimal.valueOf(17500L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
        given(productRepository.findById(any())).willReturn(Optional.of(상품));
        given(비속어_판별기.containsProfanity("후라이드치킨")).willReturn(false);
        given(menuRepository.save(any(Menu.class))).willReturn(등록할메뉴);

        //when
        Menu 등록된메뉴 = menuService.create(등록할메뉴);

        //then
        verify(menuRepository, times(1)).save(any(Menu.class));
        assertThat(등록된메뉴.getPrice()).isEqualTo(BigDecimal.valueOf(17000L));
        assertThat(등록된메뉴.getName()).isEqualTo("후라이드치킨");
        assertThat(등록된메뉴.getMenuProducts()).containsExactly(메뉴구성상품);
    }

    @DisplayName(value = "반드시 0원 이상의 메뉴가격을 가져야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴가격")
    void create_fail_invalid_price(final BigDecimal 메뉴가격) {
        //given
        Menu 등록할_메뉴 = new MenuBuilder().price(메뉴가격).build();

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴는 반드시 하나의 메뉴그룹을 지정하여 등록해야 한다")
    @Test
    void create_fail_menu_should_belongs_to_menu_group() {
        //given
        Menu 등록할_메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "메뉴는 반드시 하나 이상의 메뉴구성상품(menuProduct)을 포함하고 있어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴구성상품_리스트")
    void create_fail_menu_should_have_gt_1_menu_product(final List<MenuProduct> 메뉴구성상품리스트) {
        //given
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Menu 등록할메뉴 = new MenuBuilder().menuGroup(메뉴그룹).menuProducts(메뉴구성상품리스트).price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "상품(product)의 개수와 메뉴상품구성(menuProduct)의 개수는 일치해야 한다")
    @Test
    void create_fail_menu_product_size_should_eq_product_size() {
        //given
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Product 상품 = new ProductBuilder().build();
        MenuProduct 메뉴상품구성 = new MenuProductBuilder().product(상품).productId(상품.getId()).build();
        Menu 등록할_메뉴 = new MenuBuilder().menuGroup(메뉴그룹).menuProducts(new ArrayList<>(Arrays.asList(메뉴상품구성))).price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(new ProductBuilder().build(), new ProductBuilder().build()));

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "각 메뉴구성상품(menuProduct)의 양(quantity)은 0이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴구성상품_개수")
    void create_fail_menu_product_quantity_should_gt_0(final long 메뉴구성상품개수) {
        //given
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Product 상품 = new ProductBuilder().build();
        MenuProduct 메뉴상품구성 = new MenuProductBuilder().quantity(메뉴구성상품개수).product(상품).productId(상품.getId()).build();
        Menu 등록할_메뉴 = new MenuBuilder().menuGroup(메뉴그룹).menuProducts(new ArrayList<>(Arrays.asList(메뉴상품구성))).price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(new ProductBuilder().build()));

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴는 존재하는 상품만 포함할 수 있다")
    @Test
    void create_fail_product_no_exist() {
        //given
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Product 상품 = new ProductBuilder().build();
        MenuProduct 메뉴상품구성 = new MenuProductBuilder().quantity(1L).product(상품).productId(상품.getId()).build();
        Menu 등록할_메뉴 = new MenuBuilder().menuGroup(메뉴그룹).menuProducts(new ArrayList<>(Arrays.asList(메뉴상품구성))).price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
        given(productRepository.findById(any())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "메뉴의 가격이 각 메뉴구성상품의 합보다 클 수 없다")
    @Test
    void create_fail_menu_price_shoud_lt_sum_of_menu_product() {
        //given
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Product 상품 = new ProductBuilder().price(BigDecimal.valueOf(17500)).build();
        MenuProduct 메뉴상품구성 = new MenuProductBuilder().quantity(1L).product(상품).productId(상품.getId()).build();
        Menu 등록할_메뉴 = new MenuBuilder().menuGroup(메뉴그룹).menuProducts(new ArrayList<>(Arrays.asList(메뉴상품구성))).price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
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
        MenuGroup 메뉴그룹 = new MenuGroupBuilder().build();
        Product 상품 = new ProductBuilder().price(BigDecimal.valueOf(17500)).build();
        MenuProduct 메뉴상품구성 = new MenuProductBuilder().quantity(1L).product(상품).productId(상품.getId()).build();
        Menu 등록할_메뉴 = new MenuBuilder().name(메뉴명).menuGroup(메뉴그룹).menuProducts(new ArrayList<>(Arrays.asList(메뉴상품구성))).price(BigDecimal.valueOf(17000L)).build();

        given(menuGroupRepository.findById(any())).willReturn(Optional.of(메뉴그룹));
        given(productRepository.findAllByIdIn(any(ArrayList.class))).willReturn(Arrays.asList(상품));
        given(productRepository.findById(any())).willReturn(Optional.of(상품));

        //when,then
        lenient().when(비속어_판별기.containsProfanity(메뉴명)).thenReturn(true);
        assertThatThrownBy(() -> menuService.create(등록할_메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴의 가격을 변경할 수 있다")
    @Test
    void changePrice_success() {
        //given
        final BigDecimal 변경가격 = BigDecimal.valueOf(17000);
        final BigDecimal 기존가격 = BigDecimal.valueOf(17000);

        final Menu 가격변경요청 = new MenuBuilder().price(변경가격).build();
        Product 상품 = new ProductBuilder().price(기존가격).build();
        MenuProduct 메뉴구성상품 = new MenuProductBuilder().product(상품).quantity(1L).build();
        final Menu 가격변경될메뉴 = new MenuBuilder().price(기존가격).menuProducts(new ArrayList<>(Arrays.asList(메뉴구성상품))).build();

        given(menuRepository.findById(가격변경될메뉴.getId())).willReturn(Optional.of(가격변경될메뉴));

        //when
        Menu 가격변경된메뉴 = menuService.changePrice(가격변경될메뉴.getId(), 가격변경요청);

        //then
        assertThat(가격변경된메뉴.getPrice()).isEqualTo(변경가격);
    }

    @DisplayName(value = "변경하려는 메뉴의 가격은 존재해야하며, 0원 이상이어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_메뉴가격")
    void changePrice_fail_invalid_menu_price(final BigDecimal 변경할메뉴가격) {
        //given
        Menu 가격변경요청 = new MenuBuilder().price(변경할메뉴가격).build();

        //when, then
        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), 가격변경요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "존재하는 메뉴만 가격을 변경할 수 있다")
    @Test
    void changePrice_fail_menu_not_exist() {
        //given
        final BigDecimal 변경할메뉴가격 = BigDecimal.valueOf(17000);

        final Menu 가격변경요청 = new MenuBuilder().price(변경할메뉴가격).build();

        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), 가격변경요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "메뉴 가격을 각 메뉴구성상품 가격의 합보다 크게 변경할 수 없다")
    @Test
    void changePrice_fail_menu_price_gt_sum_of_menu_product_price() {
        //given
        final BigDecimal 변경할메뉴가격 = BigDecimal.valueOf(17500);
        final BigDecimal 기존상품가격 = BigDecimal.valueOf(17000);

        final Menu 가격변경요청 = new MenuBuilder().price(변경할메뉴가격).build();
        Product 상품 = new ProductBuilder().price(기존상품가격).build();
        MenuProduct 메뉴구성상품 = new MenuProductBuilder().product(상품).quantity(1L).build();
        final Menu 가격변경될메뉴 = new MenuBuilder().menuProducts(new ArrayList<>(Arrays.asList(메뉴구성상품))).build();

        given(menuRepository.findById(가격변경될메뉴.getId())).willReturn(Optional.of(가격변경될메뉴));

        //when, then
        assertThatThrownBy(() -> menuService.changePrice(가격변경될메뉴.getId(), 가격변경요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "메뉴의 판매상태를 판매중으로 변경할 수 있다")
    @Test
    void display_success() {
        //given
        BigDecimal 가격 = BigDecimal.valueOf(17000L);

        Product 상품 = new ProductBuilder().price(가격).build();
        MenuProduct 메뉴구성상품 = new MenuProductBuilder().quantity(1L).product(상품).build();
        Menu 메뉴 = new MenuBuilder().price(가격).menuProducts(new ArrayList<>(Arrays.asList(메뉴구성상품))).build();

        given(menuRepository.findById(메뉴.getId())).willReturn(Optional.of(메뉴));

        //when
        Menu 판매중인메뉴 = menuService.display(메뉴.getId());

        //then
        assertThat(판매중인메뉴.isDisplayed()).isTrue();
    }

    @DisplayName(value = "존재하는 메뉴만 판매상태를 판매중으로 변경할 수 있다")
    @Test
    void hide_fail_menu_not_exist() {
        //given
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "메뉴 가격이 각 메뉴구성상품 가격의 합보다 큰경우 판매중으로 변경할 수 없다")
    @Test
    void display_fail_menu_price_gt_sum_of_menu_product() {
        //given
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17500L);
        BigDecimal 상품가격 = BigDecimal.valueOf(17000L);

        Product 상품 = new ProductBuilder().price(상품가격).build();
        MenuProduct 메뉴구성상품 = new MenuProductBuilder().quantity(1L).product(상품).build();
        Menu 메뉴 = new MenuBuilder().price(메뉴가격).menuProducts(new ArrayList<>(Arrays.asList(메뉴구성상품))).build();

        given(menuRepository.findById(메뉴.getId())).willReturn(Optional.of(메뉴));

        //when, then
        assertThatThrownBy(() -> menuService.display(메뉴.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "메뉴의 판매상태를 판매중단으로 변경할 수 있다")
    @Test
    void hide_success() {
        //given
        Menu 메뉴 = new MenuBuilder().build();

        given(menuRepository.findById(메뉴.getId())).willReturn(Optional.of(메뉴));

        //when
        Menu 판매중단메뉴 = menuService.hide(메뉴.getId());

        //then
        assertThat(판매중단메뉴.isDisplayed()).isFalse();
    }

    @DisplayName(value = "존재하는 메뉴만 판매상태를 판매중단으로 변경할 수 있다")
    @Test
    void hide_fail_no_exist_menu() {
        //given
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> menuService.hide(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "전체 메뉴를 조회할 수 있다")
    @Test
    void findAll_success() {
        //given, when
        menuService.findAll();

        //then
        verify(menuRepository, times(1)).findAll();
    }
}
