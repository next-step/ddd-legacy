package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MenuServiceTest {

    @Autowired
    MenuService menuService;

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        menuRepository.deleteAll();
    }

    @DisplayName("새로운 메뉴를 추가")
    @Test
    void create_menu() {
        //given
        Menu newMenu = generateNormalMenu();

        //when
        Menu savedMenu = menuService.create(newMenu);

        // then
        final List<Menu> menus = menuRepository.findAll();
        final Menu findMenu = menus.get(0);
        assertThat(savedMenu.getId()).isEqualTo(findMenu.getId());
        assertThat(savedMenu.getName()).isEqualTo(findMenu.getName());
        assertThat(savedMenu.getMenuGroupId()).isEqualTo(findMenu.getMenuGroupId());
        assertThat(savedMenu.isDisplayed()).isEqualTo(findMenu.isDisplayed());
    }

    @DisplayName("메뉴 추가 실패 - 이름이 지정되지 않음")
    @Test
    void create_menu_null_name() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setName(null);

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름");
    }

    @DisplayName("메뉴 추가 실패 - 이름에 비속어포함")
    @Test
    void create_menu_name_contains_profanity() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setName("wop");

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이름");
    }

    @DisplayName("메뉴 추가 실패 - 가격이 지정되지 않음")
    @Test
    void create_menu_null_price() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setPrice(null);

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("메뉴 추가 실패 - 가격이 음수")
    @Test
    void create_menu_negative_price() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setPrice(BigDecimal.valueOf(-10000));

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("메뉴 추가 실패 - 메뉴 그룹 미지정")
    @Test
    void create_menu_null_menugroup() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setMenuGroup(null);

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 그룹");
    }

    @DisplayName("메뉴 추가 실패 - 존재하지 않는 메뉴 그룹")
    @Test
    void create_menu_not_exist_menugroup() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setMenuGroupId(UUID.randomUUID());

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("메뉴 그룹");
    }

    @DisplayName("메뉴 추가 실패 - 포함된 상품이 없다.")
    @Test
    void create_menu_not_exist_menuProduct() {
        //given
        Menu newMenu = generateNormalMenu();
        newMenu.setMenuProducts(new ArrayList<>());

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴에 상품 정보가 없습니다.");
    }

    @DisplayName("메뉴 추가 실패 - 존재하지 않는 상품이 포함")
    @Test
    void create_menu_not_exist_product() {
        //given
        Menu newMenu = generateNormalMenu();
        MenuProduct menuProduct = generateMenuProduct(new Product());
        newMenu.setMenuProducts(Arrays.asList(menuProduct));

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하는 상품이어야 합니다.");
    }

    @DisplayName("메뉴 추가 실패 - 상품 수량은 모두 0 이상이어야 한다.")
    @Test
    void create_menu_menuProduct_quantity() {
        //given
        Menu newMenu = generateNormalMenu();
        List<MenuProduct> menuProducts = newMenu.getMenuProducts();
        menuProducts.get(0).setQuantity(-1);

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수량");
    }

    @DisplayName("메뉴 추가 실패 - 메뉴 가격은 메뉴에 포함된 상품들의 가격과 갯수를 곱한 총액보다 작거나 같아야 한다.")
    @Test
    void create_menu_menuProduct_price() {
        //given
        Menu newMenu = generateNormalMenu();
        BigDecimal totalPrice = newMenu.totalPriceOfProducts();
        System.out.println(totalPrice);
        newMenu.setPrice(totalPrice.add(BigDecimal.valueOf(10000)));

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("메뉴 가격 수정")
    @Test
    void change_price() {
        //given
        Menu menu = generateNormalMenu();
        menu.setId(UUID.randomUUID());
        Menu savedMenu = menuRepository.save(menu);
        savedMenu.setPrice(BigDecimal.valueOf(21000));

        //when
        Menu findMenu = menuService.changePrice(savedMenu.getId(), savedMenu);

        //then
        assertThat(findMenu.getPrice()).isEqualTo(savedMenu.getPrice());
    }

    @DisplayName("메뉴 가격 수정 실패 - 존재하지 않는 메뉴")
    @Test
    void change_price_fail_not_exist_menu() {
        //given
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(10000));

        //when & then
        assertThatThrownBy(() -> menuService.changePrice(UUID.randomUUID(), menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 가격 수정 실패 - 가격이 음수")
    @Test
    void change_price_fail_invalid() {
        //given
        Menu menu = generateNormalMenu();
        menu.setId(UUID.randomUUID());
        Menu savedMenu = menuRepository.save(menu);
        savedMenu.setPrice(BigDecimal.valueOf(-10000));

        //when & Then
        assertThatThrownBy(() -> menuService.changePrice(savedMenu.getId(), savedMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("메뉴 가격 수정 실패 - 메뉴 가격은 메뉴에 포함된 상품들의 가격과 갯수를 곱한 총액보다 작거나 같아야 한다.")
    @Test
    void price_is_over_total_products_price() {
        //given
        Menu menu = generateNormalMenu();
        menu.setId(UUID.randomUUID());
        Menu savedMenu = menuRepository.save(menu);
        savedMenu.setPrice(savedMenu.totalPriceOfProducts().add(BigDecimal.valueOf(10000)));

        //when & Then
        assertThatThrownBy(() -> menuService.changePrice(savedMenu.getId(), savedMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("메뉴 노출 실패 - 존재하지 않는 메뉴")
    @Test
    void menu_display_fail_not_exist_menu() {
        //when & then
        assertThatThrownBy(() -> menuService.display(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 노출 실패 - 메뉴 가격은 메뉴에 포함된 상품들의 가격과 갯수를 곱한 총액보다 작거나 같아야 한다.")
    @Test
    void menu_display_fail_invalid_price() {
        //given
        Menu menu = generateNormalMenu();
        menu.setId(UUID.randomUUID());
        Menu savedMenu = menuRepository.save(menu);
        savedMenu.setPrice(menu.totalPriceOfProducts().add(BigDecimal.valueOf(10000))); //이렇게 저장되는 것도 문제
        Menu findMenu = menuRepository.findById(savedMenu.getId()).get();
        findMenu.setDisplayed(true);
        System.out.println("price ===> " + findMenu.getPrice());

        //when & then
        assertThatThrownBy(() -> menuService.changePrice(findMenu.getId(), findMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격");
    }

    @DisplayName("메뉴 노출")
    @Test
    void menu_display() {
        //given
        Menu menu = generateNormalMenu();
        menu.setId(UUID.randomUUID());
        Menu savedMenu = menuRepository.save(menu);

        //when & then
        menuService.display(savedMenu.getId());
        Menu findMenu = menuRepository.findById(savedMenu.getId()).get();
        assertThat(findMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 숨김")
    @Test
    void menu_hide() {
        //given
        Menu menu = generateNormalMenu();
        menu.setId(UUID.randomUUID());
        Menu savedMenu = menuRepository.save(menu);

        //when & then
        menuService.hide(savedMenu.getId());
        Menu findMenu = menuRepository.findById(savedMenu.getId()).get();
        assertThat(findMenu.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    void findAll() {
        //given
        Menu menu01 = generateNormalMenu();
        menu01.setId(UUID.randomUUID());
        Menu menu02 = generateNormalMenu();
        menu02.setId(UUID.randomUUID());
        menuRepository.save(menu01);
        menuRepository.save(menu02);

        //when
        List<Menu> all = menuService.findAll();

        //then
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getId()).isEqualTo(menu01.getId());
        assertThat(all.get(0).getName()).isEqualTo(menu01.getName());
        assertThat(all.get(1).getId()).isEqualTo(menu02.getId());
        assertThat(all.get(1).getName()).isEqualTo(menu02.getName());
    }

    private Menu generateNormalMenu() {
        Menu menu = new Menu();
        menu.setName("양념 & 후라이드");
        menu.setPrice(BigDecimal.valueOf(20000));
        menu.setDisplayed(true);

        MenuGroup menuGroup = generateMenuGroup();
        menuGroupRepository.save(menuGroup);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());

        List<MenuProduct> menuProducts = generateMenuProducts();
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    private List<MenuProduct> generateMenuProducts() {
        Product product01 = generateProduct("양념 치킨", 11000);
        Product product02 = generateProduct("후라이드 치킨", 11000);
        productRepository.save(product01);
        productRepository.save(product02);

        MenuProduct menuProduct01 = generateMenuProduct(product01);
        MenuProduct menuProduct02 = generateMenuProduct(product02);
        return Arrays.asList(menuProduct01, menuProduct02);
    }

    private MenuProduct generateMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    private Product generateProduct(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        product.setId(UUID.randomUUID());
        return product;
    }

    private MenuGroup generateMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("2가지 세트 메뉴");
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }
}
