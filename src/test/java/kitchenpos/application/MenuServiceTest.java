package kitchenpos.application;

import kitchenpos.commons.MenuGenerator;
import kitchenpos.commons.MenuGroupGenerator;
import kitchenpos.commons.MenuProductGenerator;
import kitchenpos.commons.ProductGenerator;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private MenuGenerator menuGenerator = new MenuGenerator();
    private MenuGroupGenerator menuGroupGenerator = new MenuGroupGenerator();
    private ProductGenerator productGenerator = new ProductGenerator();
    private MenuProductGenerator menuProductGenerator = new MenuProductGenerator();

    private MenuGroup mockMenuGroup;
    private Product mockProduct;
    private MenuProduct mockMenuProduct;
    private List<MenuProduct> mockMenuProducts;
    private Menu mockMenu;

    @Test
    @DisplayName("메뉴 추가 - 성공")
    void addMenu() {
        // given
        generateMenuRequest();

        // mocking
        given(menuRepository.save(any())).willReturn(mockMenu);
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));
        given(productRepository.findById(any())).willReturn(Optional.of(mockProduct));
        given(productRepository.findAllById(any())).willReturn(Collections.singletonList(mockProduct));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);

        // when
        Menu newMenu = menuService.create(mockMenu);

        // then
        assertThat(newMenu.getId()).isNotNull();

        MenuGroup menuGroupOfNewMenu = newMenu.getMenuGroup();
        assertThat(menuGroupOfNewMenu).isNotNull();
        assertThat(menuGroupOfNewMenu.getId()).isEqualTo(mockMenuGroup.getId());

        List<MenuProduct> menuProductsOfNewMenu = newMenu.getMenuProducts();
        List<MenuProduct> menuProductsOfMockMenu = mockMenu.getMenuProducts();
        assertThat(menuProductsOfNewMenu).isNotNull();
        assertThat(menuProductsOfNewMenu.size()).isEqualTo(menuProductsOfMockMenu.size());

        MenuProduct menuProductOfNewMenu = menuProductsOfNewMenu.get(0);
        MenuProduct menuProductOfMockMenu = menuProductsOfNewMenu.get(0);
        assertThat(menuProductOfNewMenu).isNotNull();
        assertThat(menuProductOfNewMenu.getSeq()).isEqualTo(menuProductOfMockMenu.getSeq());

        Product productOfNewMenu = menuProductOfNewMenu.getProduct();
        Product productOfMockMenu = menuProductOfMockMenu.getProduct();
        assertThat(productOfNewMenu).isNotNull();
        assertThat(productOfNewMenu.getId()).isEqualTo(productOfMockMenu.getId());
    }

    private void generateMenuRequest() {
        mockMenuGroup = menuGroupGenerator.generateRequest();

        mockProduct = productGenerator.generateRequest();

        mockMenuProduct = menuProductGenerator.generateRequestByProduct(mockProduct);

        mockMenuProducts = new ArrayList<>();
        mockMenuProducts.add(mockMenuProduct);

        mockMenu = menuGenerator.generateRequestByMenuGroupAndMenuProducts(mockMenuGroup, mockMenuProducts);
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 빈 가격")
    void addMenu_IllegalArgument_Invalid_Price() {
        // given
        generateMenuRequest();
        mockMenu.setPrice(BigDecimal.valueOf(-1));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 유효하지 않은 MenuGroupId")
    void addMenu_NoSuchElementException_Invalid_MenuGroupId() {
        // given
        generateMenuRequest();

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 유효하지 않은 MenuProducts")
    void addMenu_IllegalArgument_Invalid_MenuProducts() {
        // given
        generateMenuRequest();
        mockMenuProducts = null;

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 유효하지 않은 메뉴 제품")
    void addMenu_IllegalArgument_NotEquals_ProductSize() {
        // given
        generateMenuRequest();

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));
        given(productRepository.findAllById(any())).willReturn(Collections.emptyList());

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 메뉴 제품 수량이 0이하")
    void addMenu_IllegalArgument_Invalid_MenuProductQuantity() {
        // given
        generateMenuRequest();
        mockMenuProduct.setQuantity(0);

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));
        given(productRepository.findById(any())).willReturn(Optional.of(mockProduct));
        given(productRepository.findAllById(any())).willReturn(Collections.singletonList(mockProduct));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 유효하지 않은 ProductId")
    void addMenu_NoSuchElementException_Invalid_ProductId() {
        // given
        generateMenuRequest();

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));
        given(productRepository.findAllById(any())).willReturn(Collections.singletonList(mockProduct));
        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 잘못된 가격")
    void addMenu_IllegalArgumentException_Invalid_Price() {
        // given
        generateMenuRequest();
        mockMenu.setPrice(BigDecimal.valueOf(9999));

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));
        given(productRepository.findById(any())).willReturn(Optional.of(mockProduct));
        given(productRepository.findAllById(any())).willReturn(Collections.singletonList(mockProduct));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 추가 - 실패: 잘못된 메뉴 이름")
    void addMenu_IllegalArgumentException_Invalid_MenuName() {
        // given
        generateMenuRequest();

        // mocking
        given(menuGroupRepository.findById(any())).willReturn(Optional.of(mockMenuGroup));
        given(productRepository.findById(any())).willReturn(Optional.of(mockProduct));
        given(productRepository.findAllById(any())).willReturn(Collections.singletonList(mockProduct));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.create(mockMenu));
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 성공")
    void changePrice() {
        // given
        generateMenuRequest();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(1000));

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when
        Menu menu = menuService.changePrice(mockMenu.getId(), request);

        // then
        assertThat(menu.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 실패: 변경할 가격이 비어있거나 0보다 작다")
    void changePrice_IllegalArgumentException_Invalid_price() {
        // given
        generateMenuRequest();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.changePrice(mockMenu.getId(), request));
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 실패: 잘못된 메뉴 id")
    void changePrice_NoSuchElementException_Invalid_MenuId() {
        // given
        generateMenuRequest();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(1000));

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.changePrice(mockMenu.getId(), request));
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 실패: 제품 가격보다 메뉴가격이 비싸다")
    void changePrice_IllegalStateException_Invalid_Price_More_Expensive_Product() {
        // given
        generateMenuRequest();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(3000));

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuService.changePrice(mockMenu.getId(), request));
    }

    @Test
    @DisplayName("메뉴 판매가능 - 성공")
    void displayMenu() {
        // given
        generateMenuRequest();

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when
        Menu menu = menuService.display(mockMenu.getId());

        // then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴 판매가능 - 실패: 잘못된 메뉴 id")
    void displayMenu_NoSuchElementException_Invalid_MenuId() {
        // given
        generateMenuRequest();

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.display(mockMenu.getId()));
    }

    @Test
    @DisplayName("메뉴 판매가능 - 실패: 메뉴 가격이 제품 총 가격보다 비싸다")
    void displayMenu_IllegalStateException_Invalid_Price_More_Expensive_Product() {
        // given
        generateMenuRequest();
        mockProduct.setPrice(BigDecimal.valueOf(100));

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> menuService.display(mockMenu.getId()));
    }

    @Test
    @DisplayName("메뉴 판매불가능 (숨기기) - 성공")
    void hideMenu() {
        // given
        generateMenuRequest();

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.of(mockMenu));

        // when
        Menu menu = menuService.hide(mockMenu.getId());

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("메뉴 판매불가능 (숨기기) - 실패: 잘못된 메뉴 id")
    void hideMenu_NoSuchElementException_Invalid_MenuId() {
        // given
        generateMenuRequest();

        // mocking
        given(menuRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> menuService.hide(mockMenu.getId()));
    }

    @Test
    @DisplayName("모든 메뉴 조회")
    void findAllMenu() {
        // given
        int size = 10;
        List<Menu> mockMenus = generateMenus(size);

        // mocking
        given(menuRepository.findAll()).willReturn(mockMenus);

        // when
        List<Menu> Menus = menuService.findAll();

        // then
        assertThat(Menus.size()).isEqualTo(size);
    }

    private List<Menu> generateMenus(int size) {
        return IntStream.range(0, size).mapToObj(i -> new Menu()).collect(Collectors.toList());
    }
}