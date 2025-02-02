package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Transactional
class MenuServiceTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurgomalumClient purgomalumClient;

    private MenuGroup menuGroup;
    private Product product;

    @BeforeEach
    void setUp() {
        menuRepository.deleteAll();
        menuGroupRepository.deleteAll();
        productRepository.deleteAll();

        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        menuGroupRepository.save(menuGroup);

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16000));
        productRepository.save(product);

    }
    @DisplayName("메뉴를 생성 할 수 있다.")
    @Test
    void create() {
        // given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setDisplayed(true);
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(16000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(menuProducts);

        //when
        Menu result = menuService.create(request);

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("후라이드");
        Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(16000));
        Assertions.assertThat(result.getMenuProducts()).hasSize(1);

    }

    @DisplayName("메뉴 생성 시 메뉴 가격이 null 이면 IllegalArgumentException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfMenuPriceIsNull() {
        //given
        Menu request = new Menu();
        request.setPrice(null);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴 생성 시 메뉴 가격이 0원 미만이면 IllegalArgumentException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfMenuPriceIsUnderZero() {
        //given
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1));

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴 생성 시 메뉴 그룹에 존재하지 않는다면, NoSuchElementException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfMenuIsNotBelongToMenuGroup() {
        //given
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(10000));
        request.setMenuGroupId(UUID.randomUUID());

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴 생성 시 메뉴 상품이 존재하지 않으면, IllegalArgumentException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfMenuProductIsNull() {
        //given
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setName("세마리세트");
        request.setPrice(BigDecimal.valueOf(25000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(null);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }


    @DisplayName("메뉴 생성 시 메뉴 상품이 상품 내 존재하지 않는다면, IllegalArgumentException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfProductIsMissing() {

        // given
        Product nonExistProduct = new Product();
        nonExistProduct.setId(UUID.randomUUID());
        nonExistProduct.setName("통구이");
        nonExistProduct.setPrice(BigDecimal.valueOf(16000));

        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProduct(nonExistProduct);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(16000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(menuProducts);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴 생성 시 메뉴 상품의 수량이 0보다 작으면, IllegalArgumentException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfMenuProductQuantityIsUnderZero() {

        // given
        MenuProduct quantityIsUnderZeroMenuProduct = new MenuProduct();
        quantityIsUnderZeroMenuProduct.setProductId(product.getId());
        quantityIsUnderZeroMenuProduct.setQuantity(-1);

        List<MenuProduct> menuProducts = List.of(quantityIsUnderZeroMenuProduct);

        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setDisplayed(true);
        request.setName("반반치킨");
        request.setPrice(BigDecimal.valueOf(16000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(menuProducts);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));

    }

    @DisplayName("메뉴 생성 시 메뉴의 가격이 전체 메뉴 상품의 가격보다 크면, IllegalArgumentException 예외 처리를 한다.")
    @Test
    void canNotCreateMenuIfMenuProductIsNotBelongToProducts() {
        // given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setDisplayed(true);
        request.setName("후라이드");
        request.setPrice(BigDecimal.valueOf(17000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(menuProducts);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴 가격 변경을 할 수 있다.")
    @Test
    void changePrice() {
        //given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menuRepository.save(menu);

        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(15000));

        //when
        Menu result = menuService.changePrice(menu.getId(),request);


        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(15000));
    }

    @DisplayName("메뉴 가격 변경시 가격이 존재하지 않으면 IllegalArgumentException 예외를 처리한다.")
    @Test
    void canNotChangePriceWithoutPrice() {
        //given
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setDisplayed(true);
        request.setName("후라이드치킨");
        request.setPrice(null);
        request.setMenuGroupId(menuGroup.getId());

        //when


        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }


    @DisplayName("메뉴 가격 변경시 가격이 0원보다 작다면 IllegalArgumentException 예외를 처리한다.")
    @Test
    void canNotChangePriceWithUnderZero() {
        //given
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setDisplayed(true);
        request.setName("후라이드치킨");
        request.setPrice(BigDecimal.valueOf(-2000));
        request.setMenuGroupId(menuGroup.getId());

        //when


        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴 가격 변경시, 변경할 메뉴의 가격이 전체 메뉴의 상품 가격보다 크면 IllegalArgumentException 예외를 처리한다.")
    @Test
    void canNotChangePriceWithExpensivePriceThanOriginPrice() {
        //given
        Menu request = new Menu();
        request.setId(UUID.randomUUID());
        request.setDisplayed(true);
        request.setName("후라이드치킨");
        request.setPrice(BigDecimal.valueOf(50000));
        request.setMenuGroupId(menuGroup.getId());

        //when


        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->menuService.create(request));
    }

    @DisplayName("메뉴를 판매 가능 상태로 변경 할 수 있다.")
    @Test
    void display() {
        //given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menuRepository.save(menu);

        //when
        Menu result = menuService.display(menu.getId());

        //then
        Assertions.assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 판매 가능 상태로 변경시, 존재하지 않은 메뉴를 선택하면 NoSuchElementException 예외 처리를 한다.")
    @Test
    void canNotDisplayWithDoseNotExistMenu() {
        //given
        UUID nonExistMenuId = UUID.randomUUID();

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->menuService.display(nonExistMenuId));
    }

    @DisplayName("메뉴를 판매 가능 상태로 변경시, 변경할 가격이 전체 메뉴 상품의 가격보다 크면 IllegalStateException 예외 처리를 한다.")
    @Test
    void canNotDisplayWithExpensivePrice() {
        //given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(17000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menuRepository.save(menu);

        //when

        //then
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(()->menuService.display(menu.getId()));
    }

    @DisplayName("메뉴를 판매 불가능 상태로 변경 할 수 있다.")
    @Test
    void hide() {
        //given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menuRepository.save(menu);

        //when
        Menu result = menuService.hide(menu.getId());

        //then
        Assertions.assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 판매 불가능 상태로 변경시, 존재하지 않은 메뉴를 선택하면 NoSuchElementException 예외 처리를 한다.")
    @Test
    void canNotHideWithDoseNotExistMenu() {
        //given
        UUID nonExistMenuId = UUID.randomUUID();

        //when

        //then
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->menuService.hide(nonExistMenuId));
    }

    @DisplayName("모든 메뉴를 조회 할 수 있다.")
    @Test
    void findAll() {
        //given
        MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProductId(product.getId());
        menuProduct1.setProduct(product);
        menuProduct1.setQuantity(1);

        List<MenuProduct> menuProducts = List.of(menuProduct1);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menuRepository.save(menu);
        //when
        List<Menu> result = menuService.findAll();

        //then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result).extracting(Menu::getId).containsExactly(menu.getId());

    }
}