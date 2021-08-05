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

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 가격은 `null` 일 수 없다 )")
    @Test
    public void create_with_null_price() {
        //given
        final Menu request = new Menu();
        final UUID menuGroupId = UUID.randomUUID();
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(List.of(new MenuProduct()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 가격은 `0`보다 작을 수 없다 )")
    @Test
    public void create_with_minus_price() {
        //given
        final BigDecimal minusPrice = BigDecimal.valueOf(-1L);
        final Menu request = new Menu();
        final UUID menuGroupId = UUID.randomUUID();
        request.setPrice(minusPrice);
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(List.of(new MenuProduct()));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴 그룹`이 영속화 되어 있어야 한다 )")
    @Test
    public void create_with_not_persisted_menu_group() {
        //given
        final Menu request = new Menu();
        final UUID menuGroupId = UUID.randomUUID();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(List.of(new MenuProduct()));

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`이 `null` 일 수 없다. )")
    @TestAndRollback
    public void create_with_null_menu_product() {
        //given
        final MenuGroup menuGroup = new MenuGroup();
        final UUID menuGroupId = UUID.randomUUID();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`이 `empty` 일 수 없다. )")
    @TestAndRollback
    public void create_with_empty_menu_product() {
        //given
        final List<MenuProduct> emptyProducts = Collections.emptyList();
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(emptyProducts);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`은 영속화 된 제품 이어야 한다. )")
    @TestAndRollback
    public void create_with_not_persisted_product() {
        //given
        final MenuGroup menuGroup = new MenuGroup();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 치킨");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        request.setMenuProducts(List.of(menuProduct));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`의 제품과 실제 영속화 된 제품의 숫자가 같아야 한다. )")
    @TestAndRollback
    public void create_with_not_equal_persisted_product_count() {
        //given
        final MenuGroup menuGroup = new MenuGroup();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        MenuProduct menuProduct_2 = new MenuProduct();
        menuProduct_2.setProductId(UUID.randomUUID());

        request.setMenuProducts(List.of(menuProduct_1, menuProduct_2));

        final Product product_1 = new Product();
        product_1.setId(productId);
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
        final long minusQuantity = -1L;
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(minusQuantity);

        request.setMenuProducts(List.of(menuProduct_1));

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(10000L));
        productRepository.save(product_1);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴를 생성한다 ( 메뉴 생성시 요청된 `메뉴에 속한 제품`의 가격의 합이 영속화 된 제품의 가격의 합보다 클 수 없다. )")
    @TestAndRollback
    public void create_with_bigger_than_persisted_product_price_sum() {
        //give
        int biggerPrice = 40000;
        int lowerPrice = 15000;

        final MenuGroup menuGroup = new MenuGroup();
        final UUID menuGroupId = UUID.randomUUID();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(biggerPrice));
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);

        final UUID productId = UUID.randomUUID();
        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(2L);

        request.setMenuProducts(List.of(menuProduct_1));

        final Product product_1 = new Product();
        product_1.setId(productId);
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
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(20000));
        request.setDisplayed(true);
        request.setMenuGroupId(menuGroupId);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(2L);

        request.setMenuProducts(List.of(menuProduct_1));

        final Product product_1 = new Product();
        product_1.setId(productId);
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
        final String profanityName = "Bitch";
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(20000));
        request.setDisplayed(true);
        request.setName(profanityName);
        request.setMenuGroupId(menuGroupId);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(2L);

        request.setMenuProducts(List.of(menuProduct_1));

        final Product product_1 = new Product();
        product_1.setId(productId);
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
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Menu request = new Menu();
        request.setPrice(menuPrice);
        request.setDisplayed(true);
        request.setName(menuName);
        request.setMenuGroupId(menuGroupId);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        final MenuProduct menuProduct_2 = new MenuProduct();
        menuProduct_2.setProductId(colaUuid);
        menuProduct_2.setQuantity(1L);

        request.setMenuProducts(List.of(menuProduct_1, menuProduct_2));

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));

        final Product product_2 = new Product();
        product_2.setId(colaUuid);
        product_2.setName("콜라");
        product_2.setPrice(BigDecimal.valueOf(5000L));

        productRepository.saveAll(List.of(product_1, product_2));

        //when
        menuService.create(request);

        //then
        final List<Menu> menus = menuRepository.findAll();
        final Menu findMenu = menus.get(0);
        final List<MenuProduct> menuProducts = findMenu.getMenuProducts();
        final MenuProduct findMenuProduct_1 = menuProducts.get(0);
        final MenuProduct findMenuProduct_2 = menuProducts.get(1);

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

    @DisplayName("메뉴의 가격을 변경한다. ( 변경 가격은 `null` 일 수 없다. )")
    @TestAndRollback
    public void change_price_with_null_price() {
        //given
        final Menu request = new Menu();
        request.setName("후라이드 한마리");
        final UUID menuId = UUID.randomUUID();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, request));
    }

    @DisplayName("메뉴의 가격을 변경한다. ( 변경 가격은 `0` 보다 작을 수 없다. )")
    @TestAndRollback
    public void change_price_with_minus_price() {
        //given
        final BigDecimal minusPrice = BigDecimal.valueOf(-1);
        final UUID menuId = UUID.randomUUID();

        final Menu request = new Menu();
        request.setName("후라이드 한마리");
        request.setPrice(minusPrice);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, request));
    }

    @DisplayName("메뉴의 가격을 변경한다. ( 변경 요청된 메뉴는 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void change_price_with_not_persisted_menu() {
        //given
        final Menu request = new Menu();
        final UUID menuId = UUID.randomUUID();

        request.setName("후라이드 한마리");
        request.setPrice(BigDecimal.valueOf(10000L));


        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, request));
    }

    @DisplayName("메뉴의 가격을 변경한다. ( 변경 요청된 가격은 메뉴에 구성된 제품의 합보다 클 수 없다. )")
    @TestAndRollback
    public void change_price_with_bigger_than_products_price_sum() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu request = new Menu();
        request.setId(menuId);
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setPrice(BigDecimal.valueOf(20000L));
        request.setMenuProducts(List.of(menuProduct_1));
        request.setMenuGroup(menuGroup);

        menuRepository.save(request);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, request));
    }

    @DisplayName("메뉴의 가격을 변경한다.")
    @TestAndRollback
    public void change_price() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final BigDecimal changePrice = BigDecimal.valueOf(10000);

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu request = new Menu();
        request.setId(menuId);
        request.setName("후라이드 한마리");
        request.setDisplayed(true);
        request.setPrice(changePrice);
        request.setMenuProducts(List.of(menuProduct_1));
        request.setMenuGroup(menuGroup);

        menuRepository.save(request);

        //when
        menuService.changePrice(menuId, request);

        //then
        final Menu findMenu = menuRepository.findById(menuId).get();
        assertThat(findMenu.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("해당 메뉴를 노출 시킨다. ( 요청 메뉴 ID는 영속화 된 메뉴여야 한다. )")
    @TestAndRollback
    public void display_with_not_persisted() {
        //given
        final UUID menuId = UUID.randomUUID();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(menuId));
    }

    @DisplayName("해당 메뉴를 노출 시킨다. ( 요청된 메뉴 ID의 메뉴의 가격은 제품가격의 합보다 작아야 한다. )")
    @TestAndRollback
    public void display_with_bigger_than_products_price_sum() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final BigDecimal biggerPrice = BigDecimal.valueOf(20000);

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(false);
        menu.setPrice(biggerPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> menuService.display(menuId));
    }

    @DisplayName("해당 메뉴를 노출 시킨다. ( 요청된 메뉴 ID의 메뉴의 가격은 제품가격의 합보다 작아야 한다. )")
    @TestAndRollback
    public void display_with_minus_price() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final BigDecimal minusPrice = BigDecimal.valueOf(-1);

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000L));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(false);
        menu.setPrice(minusPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> menuService.display(menuId));
    }

    @DisplayName("해당 메뉴를 노출 시킨다.")
    @TestAndRollback
    public void display() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        //when
        menuService.display(menuId);

        //then
        final Menu findMenu = menuRepository.findById(menuId).get();
        assertThat(findMenu.isDisplayed()).isEqualTo(true);
    }

    @DisplayName("해당 메뉴를 감춘다. ( 요청 메뉴 ID는 영속화 된 메뉴여야 한다. )")
    @TestAndRollback
    public void hide_with_not_persisted() {
        //given
        final UUID menuId = UUID.randomUUID();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(menuId));
    }

    @DisplayName("해당 메뉴를 감춘다.")
    @TestAndRollback
    public void hide() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        //when
        menuService.hide(menuId);

        //then
        final Menu findMenu = menuRepository.findById(menuId).get();
        assertThat(findMenu.isDisplayed()).isEqualTo(false);
    }

    @DisplayName("모든 메뉴를 조회한다.")
    @TestAndRollback
    public void findAll() {
        //given
        final UUID menuId = UUID.randomUUID();
        final UUID menuGroupId = UUID.randomUUID();
        final UUID productId = UUID.randomUUID();

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final Product product_2 = new Product();
        final UUID colaUuid = UUID.randomUUID();
        product_2.setId(colaUuid);
        product_2.setName("콜라");
        product_2.setPrice(BigDecimal.valueOf(5000));

        productRepository.save(product_2);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final MenuProduct menuProduct_2 = new MenuProduct();
        menuProduct_2.setProductId(colaUuid);
        menuProduct_2.setQuantity(1L);
        menuProduct_2.setProduct(product_2);

        final Menu menu_1 = new Menu();
        menu_1.setId(menuId);
        menu_1.setName("후라이드 한마리");
        menu_1.setDisplayed(true);
        menu_1.setPrice(BigDecimal.valueOf(15000));
        menu_1.setMenuProducts(List.of(menuProduct_1));
        menu_1.setMenuGroup(menuGroup);

        final Menu menu_2 = new Menu();
        menu_2.setId(UUID.randomUUID());
        menu_2.setName("후라이드 한마리 셋트");
        menu_2.setDisplayed(true);
        menu_2.setPrice(BigDecimal.valueOf(20000));
        menu_2.setMenuProducts(List.of(menuProduct_1, menuProduct_2));
        menu_2.setMenuGroup(menuGroup);

        menuRepository.saveAll(List.of(menu_1, menu_2));

        //when
        final List<Menu> menus = menuService.findAll();

        //then
        assertThat(menus.size()).isEqualTo(2);
        assertThat(menus.get(0)).isEqualTo(menu_1);
        assertThat(menus.get(1)).isEqualTo(menu_2);
    }



}
