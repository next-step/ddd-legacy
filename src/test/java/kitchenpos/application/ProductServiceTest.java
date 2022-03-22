package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
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
class ProductServiceTest extends DefaultIntegrationTestConfig {

    private static final String NAME1 = "testProductName1";
    private static final String NAME2 = "testProductName2";
    private static final String MENU_NAME = "testMenuName";
    private static final String MENU_GROUP_NAME = "testMenuGroupName";

    private static final BigDecimal PRICE1 = BigDecimal.valueOf(13L);
    private static final BigDecimal PRICE2 = BigDecimal.valueOf(50L);
    private static final BigDecimal PRICE_ZERO = BigDecimal.ZERO;

    @MockBean
    private PurgomalumClient mockPurgomalumClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductService service;

    private Product product1;

    @BeforeEach
    void setUp() {
        product1 = create(NAME1, PRICE1);
    }

    private BigDecimal multiply(final BigDecimal price, int multiply) {
        return price.multiply(BigDecimal.valueOf(multiply));
    }

    private Product create(String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    private Menu create(final BigDecimal menuPrice, final Product product,
        final int productQuantity, final boolean displayed) {

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(MENU_GROUP_NAME);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(productQuantity);

        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(displayed);
        menu.setPrice(menuPrice);
        menu.setName(MENU_NAME);
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menu.setMenuGroup(menuGroup);

        return menu;
    }

    private UUID readyMenu(final Menu menu) {
        final MenuGroup menuGroup = menuGroupRepository.save(menu.getMenuGroup());
        menu.setMenuGroup(menuGroup);

        return menuRepository.save(menu).getId();
    }

    private UUID readySavedProduct(final Product product) {
        return productRepository.save(product).getId();
    }

    private void configContainsProfanityNameScenario(final String name,
        final boolean profanityName) {

        doReturn(profanityName).when(mockPurgomalumClient).containsProfanity(name);
    }

    private void assertReturnProduct(final Product result, final Product request) {
        assertThat(result).usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isNotEqualTo(request.getId());
    }

    private void assertSavedProductPrice(final Product savedProduct, final BigDecimal price) {
        assertThat(savedProduct.getPrice()).isEqualTo(price);
    }

    /*
    강의 진행되어 도메인에 equals()를 정의하면 이 테스트 코드도 수정한다.
     */
    private void assertContainsByField(final List<Product> results,
        final Product product1, final Product product2) {

        assertThat(results).hasSize(2);

        final Product result1;
        final Product result2;
        if (results.get(0).getId().equals(product1.getId())) {
            result1 = results.get(0);
            result2 = results.get(1);
        } else {
            result1 = results.get(1);
            result2 = results.get(0);
        }

        assertThat(result1).usingRecursiveComparison().isEqualTo(product1);
        assertThat(result2).usingRecursiveComparison().isEqualTo(product2);
    }

    @DisplayName("Product의 price가 null이면 예외를 발생시킨다")
    @Test
    void create_when_null_price() {
        // given

        // when & then
        assertThatThrownBy(() -> service.create(create(NAME1, null)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Product의 price가 음수이면 예외를 발생시킨다")
    @Test
    void create_when_negative_price() {
        // given

        // when & then
        assertThatThrownBy(() -> service.create(create(NAME1, BigDecimal.valueOf(-1L))))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Product의 name이 null이면 예외를 발생시킨다")
    @Test
    void create_when_null() {
        // given

        // when & then
        assertThatThrownBy(() -> service.create(create(null, PRICE1)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Product의 name에 비속어가 있다면 예외를 발생시킨다")
    @Test
    void create_when_profanity_name() {
        // given
        configContainsProfanityNameScenario(product1.getName(), true);

        // when & then
        assertThatThrownBy(() -> service.create(product1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Product를 추가할 수 있다")
    @Test
    void create() {
        // given
        configContainsProfanityNameScenario(product1.getName(), false);

        // when
        final Product result = service.create(product1);

        // then
        final Product product = productRepository.findById(result.getId())
            .orElseThrow(NoSuchElementException::new);

        assertThat(result).usingRecursiveComparison().isEqualTo(product);
    }

    @DisplayName("가격이 0원인 Product를 추가할 수 있다")
    @Test
    void create_when_zero_price() {
        // given
        final Product request = create(NAME1, PRICE_ZERO);

        configContainsProfanityNameScenario(request.getName(), false);

        // when
        final Product result = service.create(request);

        // then
        final Product product = productRepository.findById(result.getId())
            .orElseThrow(NoSuchElementException::new);

        assertThat(result).usingRecursiveComparison().isEqualTo(product);
    }

    @DisplayName("Product를 추가하면 식별자가 부여된 결과를 반환한다")
    @Test
    void create_return() {
        // given
        configContainsProfanityNameScenario(product1.getName(), false);

        // when
        final Product result = service.create(product1);

        // then
        assertReturnProduct(result, product1);
    }

    @DisplayName("이름이 같은 Product를 추가할 수 있다")
    @Test
    void create_equal_name() {
        // given
        final Product alreadyExistEqualNameProduct = productRepository.save(create(NAME1, PRICE2));
        final Product request = product1;

        configContainsProfanityNameScenario(request.getName(), false);

        // when
        final Product result = service.create(request);

        // then
        final List<Product> products = productRepository.findAll();
        assertContainsByField(products, result, alreadyExistEqualNameProduct);
    }

    @DisplayName("변경 요청 가격이 null이면 예외를 발생시킨다")
    @Test
    void changePrice_when_null_price() {
        // given
        final Product product = create(NAME1, null);

        // when & then
        assertThatThrownBy(() -> service.changePrice(product.getId(), product))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경 요청 가격이 음수이면 예외를 발생시킨다")
    @Test
    void changePrice_when_negative_price() {
        // given
        final Product product = create(NAME1, BigDecimal.valueOf(-1L));

        // when & then
        assertThatThrownBy(() -> service.changePrice(product.getId(), product))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경 요청 제품이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void changePrice_when_not_exist_product() {
        // given

        // when & then
        assertThatThrownBy(() -> service.changePrice(product1.getId(), product1))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("제품을 사용하는 메뉴가 없다면 제품 가격만 변경하고 종료한다")
    @Test
    void changePrice_when_not_used_product() {
        // given
        final UUID productId = readySavedProduct(create(NAME1, PRICE1));
        final Product request = create(NAME1, PRICE2);

        // when
        final Product result = service.changePrice(productId, request);

        final Product savedProduct = productRepository.getById(productId);

        // then
        assertReturnProduct(result, request);
        assertSavedProductPrice(savedProduct, request.getPrice());
    }

    @DisplayName("제품을 사용하는 메뉴가 존재하고 제품 가격 변동으로 인해 메뉴 구성 제품 가격의 총합이 메뉴의 가격보다 비싸다면 메뉴를 미노출 처리힌다")
    @Test
    void changePrice_when_higher_price_than_menus() {
        // given
        final Product product = create(NAME1, PRICE1);
        final UUID productId = readySavedProduct(product);

        final BigDecimal menuPrice = multiply(product.getPrice(), 3);
        final Menu menu = create(menuPrice, product, 2, true);
        final UUID menuId = readyMenu(menu);

        final Product request = create(NAME1, menuPrice);

        // when
        service.changePrice(productId, request);

        final Menu savedMenu = menuRepository.getById(menuId);

        // then
        assertThat(savedMenu.isDisplayed()).isFalse();
    }

    @DisplayName("제품을 사용하는 메뉴가 존재하고 제품 가격 변동으로 인해 메뉴 구성 제품 가격의 총합이 메뉴의 가격과 같다면 메뉴 노출 여부를 변경하지 않고 종료한다")
    @Test
    void changePrice_when_equals_price() {
        // given
        final Product product = create(NAME1, PRICE1);
        final UUID productId = readySavedProduct(product);

        final BigDecimal menuPrice = multiply(product.getPrice(), 4);
        final Menu menu = create(menuPrice, product, 2, true);
        final UUID menuId = readyMenu(menu);

        final BigDecimal price = menuPrice.divide(BigDecimal.valueOf(2L));
        final Product request = create(NAME1, price);

        // when
        service.changePrice(productId, request);

        final Menu savedMenu = menuRepository.getById(menuId);

        // then
        assertThat(savedMenu.isDisplayed()).isTrue();
    }

    @DisplayName("제품을 사용하는 메뉴가 존재하고 제품 가격 변동으로 인해 메뉴 구성 제품 가격의 총합이 메뉴의 가격보다 싸다면 메뉴 노출 여부를 변경하지 않고 종료한다")
    @Test
    void changePrice_lower_price_than_menus() {
        // given
        final Product product = create(NAME1, PRICE2);
        final UUID productId = readySavedProduct(product);

        final BigDecimal menuPrice = multiply(product.getPrice(), 4);
        final Menu menu = create(menuPrice, product, 2, true);
        final UUID menuId = readyMenu(menu);

        final Product request = create(NAME1, PRICE1);

        // when
        service.changePrice(productId, request);

        final Menu savedMenu = menuRepository.getById(menuId);

        // then
        assertThat(savedMenu.isDisplayed()).isTrue();
    }

    @DisplayName("Product가 아무것도 없다면 빈 리스트를 반환한다")
    @Test
    void findAll_when_empty() {
        // given

        // when
        final List<Product> results = service.findAll();

        // then
        assertThat(results).isEmpty();
    }

    @DisplayName("Product가 있다면 그것이 담긴 리스트를 반환한다")
    @Test
    void findAll() {
        // given
        final Product product1 = productRepository.save(create(NAME1, PRICE1));
        final Product product2 = productRepository.save(create(NAME2, PRICE2));

        // when
        final List<Product> results = service.findAll();

        // then
        assertContainsByField(results, product1, product2);
    }
}
