package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @DisplayName("새로운 상품을 추가할 수 있다.")
    @Test
    void create() {
        // given
        final String expectedName = "상품 이름";
        final BigDecimal expectedPrice = BigDecimal.valueOf(1);
        final Product product = createProduct(expectedName, expectedPrice);
        given(productRepository.save(any(Product.class)))
                .willReturn(product);

        // when
        final Product actual = productService.create(product);

        // then
        assertAll(
                () -> assertThat(actual.getName())
                        .isEqualTo(expectedName),
                () -> assertThat(actual.getPrice())
                        .isEqualTo(expectedPrice)
        );
    }

    @DisplayName("상품의 가격은 비어있을 수 없다.")
    @Test
    void createEmptyPrice() {
        // given
        final Product product = createProduct("상품 이름", null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 가격은 음수일 수 없다.")
    @Test
    void createNegativePrice() {
        // given
        final Product product = createProduct("상품 이름", BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 이름은 비어있을 수 없다.")
    @Test
    void createEmptyName() {
        // given
        final Product product = createProduct(null, BigDecimal.valueOf(1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 이름은 저속해서는 안된다.")
    @Test
    void createProfanityName() {
        // given
        final Product product = createProduct("상품 이름", BigDecimal.valueOf(1));
        given(purgomalumClient.containsProfanity(any(String.class)))
                .willReturn(true);


        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 가격을 수정할 수 있다.")
    @Test
    void changePrice() {
        // given
        final Product product = createProduct("상품 이름", BigDecimal.valueOf(1));
        final ProductRepository productRepository = new FakeProductRepository();
        productRepository.save(product);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);

        // when
        final BigDecimal changedPrice = BigDecimal.valueOf(20_000);
        final Product changedProduct = createProduct(product.getName(), changedPrice);
        final Product actual = productService.changePrice(product.getId(), changedProduct);

        // then
        assertThat(actual.getPrice())
                .isEqualTo(changedPrice);
    }

    @DisplayName("상품의 가격을 빈값으로 수정할 수 없다.")
    @Test
    void changeEmptyPrice() {
        // given
        final Product product = createProduct("상품 이름", null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @DisplayName("상품의 가격을 음수로 수정할 수 없다.")
    @Test
    void changeNegativePrice() {
        // given
        final Product product = createProduct("상품 이름", BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @DisplayName("존재하지 않는 상품은 수정할 수 없다.")
    @Test
    void changeNonExistProduct() {
        // given
        final Product product = createProduct("상품 이름", BigDecimal.valueOf(1));
        given(productRepository.findById(any(UUID.class)))
                .willReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @DisplayName("상품 가격 수정으로, 메뉴가 비싸진다면 메뉴를 숨겨야한다.")
    @Test
    void changePriceExpensiveMenu() {
        // given
        final Product product1 = createProduct("상품1", BigDecimal.valueOf(1_100));
        final Product product2 = createProduct("상품2", BigDecimal.valueOf(2_200));
        final Menu menu1 = createMenu("메뉴1", BigDecimal.valueOf(1_000), Arrays.asList(product1));
        final Menu menu2 = createMenu("메뉴2", BigDecimal.valueOf(2_000), Arrays.asList(product2));
        final Menu menu3 = createMenu("메뉴3", BigDecimal.valueOf(3_000), Arrays.asList(product1, product2));

        final ProductRepository productRepository = new FakeProductRepository();
        final MenuRepository menuRepository = new FakeMenuRepository();

        productRepository.save(product1);
        productRepository.save(product2);
        menuRepository.save(menu1);
        menuRepository.save(menu2);
        menuRepository.save(menu3);

        productService = new ProductService(productRepository, menuRepository, purgomalumClient);

        // when
        final Product changedProduct1 = createProduct(product1.getName(), BigDecimal.valueOf(100));
        final Product actual = productService.changePrice(product1.getId(), changedProduct1);
        final List<Menu> displayedMenus = menuRepository.findAll().stream()
                .filter(Menu::isDisplayed)
                .collect(Collectors.toList());

        // then
        assertThat(displayedMenus)
                .isEqualTo(Arrays.asList(menu2));
    }

    @DisplayName("상품의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final List<Product> expectedProducts = Arrays.asList(
                createProduct("상품1", BigDecimal.valueOf(1)),
                createProduct("상품2", BigDecimal.valueOf(2)));
        given(productRepository.findAll())
                .willReturn(expectedProducts);

        // when
        final List<Product> actual = productService.findAll();

        assertThat(actual)
                .isEqualTo(expectedProducts);
    }

    private Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private Menu createMenu(final String name, final BigDecimal price, final List<Product> products) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(createMenuProducts(products));
        return menu;
    }

    private List<MenuProduct> createMenuProducts(final List<Product> products) {
        return products.stream()
                .map(this::createMenuProduct)
                .collect(Collectors.toList());
    }

    private MenuProduct createMenuProduct(final Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        return menuProduct;
    }
}
