package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.DefaultIntegrationTestConfig;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class MenuServiceTest extends DefaultIntegrationTestConfig {

    private static final UUID NOT_EXIST_ID = UUID.randomUUID();

    private static final String NAME = "menuName";

    private static final BigDecimal PRICE1 = BigDecimal.valueOf(100L);

    private static final boolean DISPLAYED = true;

    private static final boolean PROFANITY_NAME = true;
    private static final boolean NOT_PROFANITY_NAME = false;

    @MockBean
    private PurgomalumClient mockPurgomalumClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuService service;

    private Product product1;
    private Product product2;
    private Product productZeroPrice;

    @BeforeEach
    void setUp() {
        product1 = createProduct("testProductName1", BigDecimal.valueOf(10L));
        product2 = createProduct("testProductName2", BigDecimal.valueOf(20L));
        productZeroPrice = createProduct("productZeroPrice", BigDecimal.ZERO);
    }

    private void configContainsProfanityNameScenario(final boolean isProfanityName) {
        doReturn(isProfanityName).when(mockPurgomalumClient).containsProfanity(anyString());
    }

    private MenuProduct createRequest(final UUID productId, final int quantity) {
        final MenuProduct request = new MenuProduct();
        request.setProductId(productId);
        request.setQuantity(quantity);

        return request;
    }

    private Menu createRequest(final String name, final BigDecimal price,
        final boolean displayed, final UUID menuGroupId, final MenuProduct... menuProducts) {

        final Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setDisplayed(displayed);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(Arrays.asList(menuProducts));

        return request;
    }

    private Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);

        return product;
    }

    private MenuGroup createMenuGroup() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("testMenuGroup");

        return menuGroup;
    }

    private MenuProduct createMenuProduct(final Product product, int quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }

    private Menu create(final boolean displayed, final BigDecimal price, final String name,
        final MenuProduct... menuProducts) {

        final MenuGroup menuGroup = createMenuGroup();

        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setName(name);
        menu.setMenuProducts(Collections.unmodifiableList(Arrays.asList(menuProducts)));
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);

        return menu;
    }

    private void save(final Menu menu) {
        menuGroupRepository.save(menu.getMenuGroup());

        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            productRepository.save(menuProduct.getProduct());
        }

        menuRepository.save(menu);
    }

    private void assertMenuForDisplay(final Menu result, final Menu expResult) {
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("displayed", "menuGroupId", "menuProducts")
            .isEqualTo(expResult);

        assertThat(result.isDisplayed()).isTrue();
    }

    private void assertMenusForFindAll(final List<Menu> results,
        final Menu expResult1, final Menu expResult2) {

        assertThat(results).hasSize(2);

        final Menu result1;
        final Menu result2;
        if (results.get(0).getId().equals(expResult1.getId())) {
            result1 = expResult1;
            result2 = expResult2;
        } else {
            result1 = expResult2;
            result2 = expResult1;
        }

        assertThat(result1).usingRecursiveComparison().isEqualTo(expResult1);
        assertThat(result2).usingRecursiveComparison().isEqualTo(expResult2);
    }

    private void assertMenuForHide(final Menu result, final Menu expResult) {
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("displayed", "menuGroupId", "menuProducts")
            .isEqualTo(expResult);

        assertThat(result.isDisplayed()).isFalse();
    }

    private void assertMenuForCreate(final Menu result, final Menu expResult,
        final MenuGroup expMenuGroup, final Product expProduct1,
        final int expProduct1Quantity, final Product expProduct2, final int expProduct2Quantity) {

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(expResult.getName());
        assertThat(result.getPrice()).isEqualTo(expResult.getPrice());
        assertThat(result.isDisplayed()).isEqualTo(expResult.isDisplayed());
        assertThat(result.getMenuGroup()).usingRecursiveComparison().isEqualTo(expMenuGroup);

        final List<MenuProduct> actMenuProducts = result.getMenuProducts();
        assertThat(actMenuProducts).hasSize(2);

        final MenuProduct actMenuProduct1;
        final MenuProduct actMenuProduct2;
        final int expQuantity1;
        final int expQuantity2;

        if (actMenuProducts.get(0).getProduct().getId().equals(expProduct1.getId())) {
            actMenuProduct1 = actMenuProducts.get(0);
            actMenuProduct2 = actMenuProducts.get(1);
            expQuantity1 = expProduct1Quantity;
            expQuantity2 = expProduct2Quantity;
        } else {
            actMenuProduct1 = actMenuProducts.get(1);
            actMenuProduct2 = actMenuProducts.get(0);
            expQuantity1 = expProduct2Quantity;
            expQuantity2 = expProduct1Quantity;
        }

        assertThat(actMenuProduct1.getProduct()).usingRecursiveComparison().isEqualTo(expProduct1);
        assertThat(actMenuProduct1.getQuantity()).isEqualTo(expQuantity1);
        assertThat(actMenuProduct2.getProduct()).usingRecursiveComparison().isEqualTo(expProduct2);
        assertThat(actMenuProduct2.getQuantity()).isEqualTo(expQuantity2);
    }

    private void assertMenuForChangePrice(final Menu result,
        final Menu expResult, final BigDecimal expPrice) {

        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("menuGroupId", "menuProducts", "price")
            .isEqualTo(expResult);

        assertThat(result.getPrice()).isEqualTo(expPrice);
    }

    @DisplayName("메뉴 생성 요청의 가격이 없으면 예외를 발생시킨다")
    @Test
    void create_when_null_price() {
        // given
        final UUID menuGroupId = menuGroupRepository.save(createMenuGroup()).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, null, DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 요청의 가격이 음수이면 예외를 발생시킨다")
    @Test
    void create_when_null_or_negative_price() {
        // given
        final UUID menuGroupId = menuGroupRepository.save(createMenuGroup()).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, BigDecimal.valueOf(-1L), DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 요청의 메뉴 그룹이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void create_when_not_exist_menuGroup() {
        // given
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, PRICE1, DISPLAYED,
            NOT_EXIST_ID, menuProduct1, menuProduct2);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 생성 요청의 메뉴 제품이 없다면 예외를 발생시킨다")
    @Test
    void create_when_null_or_empty_menuProducts() {
        // given
        final UUID menuGroupId = menuGroupRepository.save(createMenuGroup()).getId();

        final MenuProduct notExistMenuProduct = createRequest(NOT_EXIST_ID, 1);

        final Menu request = createRequest(NAME, PRICE1, DISPLAYED,
            menuGroupId, notExistMenuProduct);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 요청의 메뉴 제품의 수량이 음수라면 예외를 발생시킨다")
    @Test
    void create_when_negative_menu_product_quantity() {
        // given
        final UUID menuGroupId = menuGroupRepository.save(createMenuGroup()).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, -1);
        final MenuProduct menuProduct2 = createRequest(product2Id, -2);

        final Menu request = createRequest(NAME, BigDecimal.ZERO, DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 요청의 가격이 제품가격들의 총합보다 작다면 예외를 발생시킨다")
    @Test
    void create_when_cheaper_than_products_total_price() {
        // given
        final UUID menuGroupId = menuGroupRepository.save(createMenuGroup()).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, BigDecimal.ZERO, DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 요청의 메뉴이름에 비속어가 있다면 예외를 발생시킨다")
    @Test
    void create_when_profanity_name() {
        // given
        configContainsProfanityNameScenario(PROFANITY_NAME);

        final UUID menuGroupId = menuGroupRepository.save(createMenuGroup()).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, PRICE1, DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성 요청대로 메뉴를 생성한다")
    @Test
    void create() {
        // given
        configContainsProfanityNameScenario(NOT_PROFANITY_NAME);

        final MenuGroup menuGroup = createMenuGroup();
        final UUID menuGroupId = menuGroupRepository.save(menuGroup).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, PRICE1, DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when
        final Menu result = service.create(request);

        final Menu savedResult = menuRepository.getById(result.getId());

        // then
        assertMenuForCreate(result, request, menuGroup, product1, 1, product2, 2);
        assertMenuForCreate(savedResult, request, menuGroup, product1, 1, product2, 2);
    }


    @DisplayName("0원짜리 메뉴를 생성할 수 있다")
    @Test
    void create_when_zero_price() {
        // given
        configContainsProfanityNameScenario(NOT_PROFANITY_NAME);

        final MenuGroup menuGroup = createMenuGroup();
        final UUID menuGroupId = menuGroupRepository.save(menuGroup).getId();
        final UUID productId = productRepository.save(productZeroPrice).getId();

        final MenuProduct menuProduct = createRequest(productId, 1);

        final Menu request = createRequest(NAME, BigDecimal.ZERO, DISPLAYED,
            menuGroupId, menuProduct);

        // when
        final Menu result = service.create(request);

        final Menu savedResult = menuRepository.getById(result.getId());

        // then
        assertThat(result.getPrice()).isZero();
        assertThat(savedResult.getPrice()).isZero();
    }

    @DisplayName("메뉴 생성 요청은 동일한 이름으로도 가능하다")
    @Test
    void create_when_exist_name() {
        // given
        save(create(true, BigDecimal.valueOf(300L), NAME, createMenuProduct(product1, 1)));

        configContainsProfanityNameScenario(NOT_PROFANITY_NAME);

        final MenuGroup menuGroup = createMenuGroup();
        final UUID menuGroupId = menuGroupRepository.save(menuGroup).getId();
        final UUID product1Id = productRepository.save(product1).getId();
        final UUID product2Id = productRepository.save(product2).getId();

        final MenuProduct menuProduct1 = createRequest(product1Id, 1);
        final MenuProduct menuProduct2 = createRequest(product2Id, 2);

        final Menu request = createRequest(NAME, PRICE1, DISPLAYED,
            menuGroupId, menuProduct1, menuProduct2);

        // when
        final Menu result = service.create(request);

        final Menu savedResult = menuRepository.getById(result.getId());

        // then
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(savedResult.getName()).isEqualTo(NAME);
    }

    @DisplayName("가격이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void changePrice_when_null_price() {
        // given
        final Menu menu = create(false, BigDecimal.valueOf(1000L),
            NAME, createMenuProduct(product1, 1));
        save(menu);

        final Menu nullPriceRequest = createRequest(NAME, null, true, null);

        // when & then
        assertThatThrownBy(() -> service.changePrice(menu.getId(), nullPriceRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격이 음수면 예외를 발생시킨다")
    @Test
    void changePrice_when_negative_price() {
        // given
        final Menu menu = create(false, BigDecimal.valueOf(1000L),
            NAME, createMenuProduct(product1, 1));
        save(menu);

        final Menu negativePriceRequest = createRequest(NAME, BigDecimal.valueOf(-1L), true, null);

        // when & then
        assertThatThrownBy(() -> service.changePrice(menu.getId(), negativePriceRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 존재하지 않으면 예외를 발생시킨다")
    @Test
    void changePrice_when_not_exist_menu() {
        // given

        final Menu negativePriceRequest = createRequest(NAME, PRICE1, true, null);

        // when & then
        assertThatThrownBy(() -> service.changePrice(NOT_EXIST_ID, negativePriceRequest))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가격이 메뉴를 구성하는 제품들의 총합보다 작다면 예외를 발생시킨다")
    @Test
    void changePrice_when_cheaper_than_products_total_price() {
        // given
        final Menu menu = create(false, BigDecimal.valueOf(1000L),
            NAME, createMenuProduct(product1, 1));
        save(menu);

        final Menu cheaperThanProductsTotalPriceRequest = createRequest(NAME, BigDecimal.ZERO, true,
            null);

        // when & then
        assertThatThrownBy(
            () -> service.changePrice(menu.getId(), cheaperThanProductsTotalPriceRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격을 변경한다")
    @Test
    void changePrice() {
        // given
        final Menu menu = create(false, BigDecimal.valueOf(1000L),
            NAME, createMenuProduct(product1, 1));
        save(menu);

        final Menu request = createRequest(NAME, PRICE1, true, null);

        // when
        final Menu result = service.changePrice(menu.getId(), request);

        final Menu savedMenu = menuRepository.getById(result.getId());

        // then
        assertMenuForChangePrice(result, menu, PRICE1);
        assertMenuForChangePrice(savedMenu, menu, PRICE1);
    }

    @DisplayName("가격은 0원으로 변경 가능하다")
    @Test
    void changePrice_when_zero_price() {
        // given
        final Menu menu = create(false, BigDecimal.valueOf(1000L),
            NAME, createMenuProduct(productZeroPrice, 1));
        save(menu);

        final Menu request = createRequest(NAME, BigDecimal.ZERO, true, null);

        // when
        final Menu result = service.changePrice(menu.getId(), request);

        final Menu savedMenu = menuRepository.getById(result.getId());

        // then
        assertMenuForChangePrice(result, menu, BigDecimal.ZERO);
        assertMenuForChangePrice(savedMenu, menu, BigDecimal.ZERO);
    }

    @DisplayName("메뉴가 존재하지 않으면 예외를 발생시킨다")
    @Test
    void display_when_not_exist_menu() {
        // given

        // when & then
        assertThatThrownBy(() -> service.display(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴의 가격이 구성 제품 가격들의 총합보다 작다면 예외를 발생시킨다")
    @Test
    void display_when_cheaper_than_products_total_price() {
        // given
        final Menu cheaperThanProductsTotalPriceMenu = create(false, BigDecimal.valueOf(1L), NAME,
            createMenuProduct(product1, 1));
        save(cheaperThanProductsTotalPriceMenu);

        // when & then
        assertThatThrownBy(() -> service.display(cheaperThanProductsTotalPriceMenu.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴를 노출시킨다")
    @Test
    void display() {
        // given
        final Menu menu = create(false, BigDecimal.valueOf(1000L),
            NAME, createMenuProduct(product1, 1));
        save(menu);

        // when
        final Menu result = service.display(menu.getId());

        final Menu savedMenu = menuRepository.getById(result.getId());

        // then
        assertMenuForDisplay(result, menu);
        assertMenuForDisplay(savedMenu, menu);
    }


    @DisplayName("메뉴가 존재하지 않으면 예외를 발생시킨다")
    @Test
    void hide_when_not_exist() {
        // given

        // when & then
        assertThatThrownBy(() -> service.hide(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴를 비노출로 만든다")
    @Test
    void hide() {
        // given
        final Menu menu = create(true, BigDecimal.valueOf(100L), "menu1",
            createMenuProduct(product1, 1));
        save(menu);

        // when
        final Menu result = service.hide(menu.getId());

        final Menu savedMenu = menuRepository.getById(result.getId());

        // then
        assertMenuForHide(result, menu);
        assertMenuForHide(savedMenu, menu);
    }

    @DisplayName("메뉴가 존재하지 않으면 빈 리스트를 반환한다")
    @Test
    void findAll_when_empty() {
        // given

        // when
        final List<Menu> result = service.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("메뉴가 존재하면 그것을 담은 리스트를 반환한다")
    @Test
    void findAll() {
        // given
        final Menu menu1 = create(true, BigDecimal.valueOf(100L), "menu1",
            createMenuProduct(product1, 1));
        save(menu1);

        final Menu menu2 = create(true, BigDecimal.valueOf(200L), "menu2",
            createMenuProduct(product1, 1));
        save(menu2);

        // when
        final List<Menu> result = service.findAll();

        // then
        assertMenusForFindAll(result, menu1, menu2);
    }
}
