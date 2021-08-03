package kitchenpos.integration;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.integration.annotation.TestAndRollback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MenuTest extends IntegrationTestRunner {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private static final UUID MENU_GROUP_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final UUID MENU_ID = UUID.randomUUID();

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 가격은 `null` 일 수 없다 )")
    @Test
    public void create_with_null_price() {
        //given
        Menu request = new Menu();
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);
        request.setMenuProducts(List.of(new MenuProduct()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 가격은 `0`보다 작을 수 없다 )")
    @Test
    public void create_with_minus_price() {
        //given
        BigDecimal minusPrice = BigDecimal.valueOf(-1L);

        Menu request = new Menu();
        request.setPrice(minusPrice);
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);
        request.setMenuProducts(List.of(new MenuProduct()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴 그룹`이 영속화 되어 있어야 한다 )")
    @Test
    public void create_with_not_persisted_menu_group() {
        //given
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);
        request.setMenuProducts(List.of(new MenuProduct()));

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`이 `null` 일 수 없다. )")
    @TestAndRollback
    public void create_with_null_menu_product() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`이 `empty` 일 수 없다. )")
    @TestAndRollback
    public void create_with_empty_menu_product() {
        //given
        List<MenuProduct> emptyProducts = Collections.emptyList();

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);
        request.setMenuProducts(emptyProducts);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`은 영속화 된 제품 이어야 한다. )")
    @TestAndRollback
    public void create_with_not_persisted_product() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(PRODUCT_ID);
        request.setMenuProducts(List.of(menuProduct));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`의 제품과 실제 영속화 된 제품의 숫자가 같아야 한다. )")
    @TestAndRollback
    public void create_with_not_equal_persisted_product_count() {
        //given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(PRODUCT_ID);
        MenuProduct menuProduct_2 = new MenuProduct();
        menuProduct_2.setProductId(UUID.randomUUID());

        request.setMenuProducts(List.of(menuProduct_1, menuProduct_2));

        Product product_1 = new Product();
        product_1.setId(PRODUCT_ID);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(10000L));
        productRepository.save(product_1);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`의 제품의 수량은 `0`보다 작을 수 없다.)")
    @TestAndRollback
    public void create_with_minus_product_quantity() {
        //give
        long minusQuantity = -1L;

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(PRODUCT_ID);
        menuProduct_1.setQuantity(minusQuantity);

        request.setMenuProducts(List.of(menuProduct_1));

        Product product_1 = new Product();
        product_1.setId(PRODUCT_ID);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(10000L));
        productRepository.save(product_1);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`의 가격의 합이 영속화 된 제품의 가격의 합보다 클 수 없다. )")
    @TestAndRollback
    public void create_with_not_equal_persisted_product_price_sum() {
        //give
        int biggerPrice = 40000;
        int lowerPrice = 15000;

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(biggerPrice));
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(PRODUCT_ID);
        menuProduct_1.setQuantity(2L);

        request.setMenuProducts(List.of(menuProduct_1));

        Product product_1 = new Product();
        product_1.setId(PRODUCT_ID);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(lowerPrice));
        productRepository.save(product_1);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 메뉴 이름은 `null` 일 수 없다 )")
    @TestAndRollback
    public void create_with_null_name() {
        //give
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(20000));
        request.setDisplayed(true);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(PRODUCT_ID);
        menuProduct_1.setQuantity(2L);

        request.setMenuProducts(List.of(menuProduct_1));

        Product product_1 = new Product();
        product_1.setId(PRODUCT_ID);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));
        productRepository.save(product_1);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 메뉴 이름에 비속어가 들어 갈 수 없다. )")
    @TestAndRollback
    public void create_with_profanity_name() {
        //give
        String profanityName = "Bitch";

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(20000));
        request.setDisplayed(true);
        request.setName(profanityName);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(PRODUCT_ID);
        menuProduct_1.setQuantity(2L);

        request.setMenuProducts(List.of(menuProduct_1));

        Product product_1 = new Product();
        product_1.setId(PRODUCT_ID);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));
        productRepository.save(product_1);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다. ")
    @TestAndRollback
    public void create() {
        //give
        final UUID colaUuid = UUID.randomUUID();
        final String menuName = "후라이드 한마리";
        final BigDecimal menuPrice = BigDecimal.valueOf(19000);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(MENU_GROUP_ID);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        Menu request = new Menu();
        request.setPrice(menuPrice);
        request.setDisplayed(true);
        request.setName(menuName);
        request.setMenuGroupId(MENU_GROUP_ID);

        MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(PRODUCT_ID);
        menuProduct_1.setQuantity(1L);
        MenuProduct menuProduct_2 = new MenuProduct();
        menuProduct_2.setProductId(colaUuid);
        menuProduct_2.setQuantity(1L);

        request.setMenuProducts(List.of(menuProduct_1, menuProduct_2));

        Product product_1 = new Product();
        product_1.setId(PRODUCT_ID);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));

        Product product_2 = new Product();
        product_2.setId(colaUuid);
        product_2.setName("콜라");
        product_2.setPrice(BigDecimal.valueOf(5000L));

        productRepository.saveAll(List.of(product_1, product_2));

        //when
        menuService.create(request);

        //then
        final List<Menu> menus = menuRepository.findAll();
        Menu findMenu = menus.get(0);
        final List<MenuProduct> menuProducts = findMenu.getMenuProducts();
        MenuProduct findMenuProduct_1 = menuProducts.get(0);
        MenuProduct findMenuProduct_2 = menuProducts.get(1);

        assertThat(findMenu.getName()).isEqualTo(menuName);
        assertThat(findMenu.getPrice()).isEqualTo(menuPrice);
        assertThat(findMenu.getMenuGroup()).isEqualTo(menuGroup);
        assertThat(findMenuProduct_1.getQuantity()).isEqualTo(menuProduct_1.getQuantity());
        assertThat(findMenuProduct_2.getQuantity()).isEqualTo(menuProduct_2.getQuantity());
        assertThat(findMenuProduct_1.getProduct().getName()).isEqualTo(product_1.getName());
        assertThat(findMenuProduct_1.getProduct().getPrice()).isEqualTo(product_1.getPrice());
        assertThat(findMenuProduct_2.getProduct().getName()).isEqualTo(product_2.getName());
        assertThat(findMenuProduct_2.getProduct().getPrice()).isEqualTo(product_2.getPrice());

    }
}
