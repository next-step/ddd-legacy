package kitchenpos.unit.application;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.stub.MenuStubRepository;
import kitchenpos.stub.ProductStubRepository;
import kitchenpos.stub.ProfanityClientStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private ProfanityClient profanityClient;

    @BeforeEach
    void setUp() {
        productRepository = new ProductStubRepository();
        menuRepository = new MenuStubRepository();
        profanityClient = new ProfanityClientStub();
        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }

    @Nested
    @DisplayName("제품 생성 테스트")
    class CreateTest {

        @DisplayName("제품 가격이 없다면 생성할 수 없다.")
        @Test
        void productPriceNull() {
            // Arrange
            Product product = createProduct();

            // Act
            // Assert
            assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("제품 가격이 0보다 작다면 생성할 수 없다.")
        @Test
        void productPriceUnderZero() {
            // Arrange
            Product product = createProduct(BigDecimal.valueOf(-1));

            // Act
            // Assert
            assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("제품 이름이 없다면 생성할 수 없다.")
        @Test
        void productNameIsNull() {
            // Arrange
            Product product = createProduct(BigDecimal.TEN);

            // Act
            // Assert
            assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("제품 이름에 부적절한 단어가 포함되어 있다면 생성할 수 없다.")
        @Test
        void productNameContainProfanity() {
            // Arrange
            Product product = createProduct("xxx", BigDecimal.TEN);

            // Act
            // Assert
            assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("제품 생성 성공.")
        @Test
        void create() {
            // Arrange
            Product product = createProduct("clean name", BigDecimal.TEN);

            // Act
            Product result = productService.create(product);

            // Assert
            assertThat(result.getId()).isNotNull();
        }
    }

    @Nested
    class ChangePriceTest {
        @DisplayName("변경할 금액이 없으면 제품 가격을 변경할 수 없다.")
        @Test
        void changePriceIsNull() {
            // Arrange
            UUID uuid = 제품_생성_요청("name", BigDecimal.ONE).getId();
            Product request = createProduct();

            // Act
            // Assert
            assertThatThrownBy(() -> productService.changePrice(uuid, request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경할 금액이 0 이하라면 제품 가격을 변경할 수 없다.")
        @Test
        void changePriceUnderZero() {
            // Arrange
            UUID uuid = 제품_생성_요청("name", BigDecimal.ONE).getId();
            Product request = createProduct(BigDecimal.valueOf(-1));

            // Act
            // Assert
            assertThatThrownBy(() -> productService.changePrice(uuid, request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경 하려는 제품이 등록되어있지 않다면 가격을 변경할 수 없다.")
        @Test
        void unRegisteredProduct() {
            // Arrange
            Product request = createProduct(BigDecimal.TEN);

            // Act
            // Assert
            assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), request)).isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("제품이 포함된 메뉴의 가격이 메뉴가 가진 제품의 가격의 합보다 높다면 메뉴의 전시를 내린다.")
        @Test
        void menuPriceRaiseThenUnDisplayed() {
            // Arrange
            Product product = 제품_생성_요청("name", BigDecimal.TEN);
            Product request = createProduct(BigDecimal.ONE);
            Menu menu = 메뉴_생성_요청(product);

            // Act
            productService.changePrice(product.getId(), request);

            // Assert
            assertThat(menu.isDisplayed()).isFalse();
        }

        @DisplayName("제품이 포함된 메뉴의 가격이 메뉴가 가진 제품의 가격의 합과 같다면 메뉴의 전시를 유지한다.")
        @Test
        void menuPriceSameThenUnDisplayed() {
            // Arrange
            Product product = 제품_생성_요청("name", BigDecimal.ONE);
            Product request = createProduct(BigDecimal.TEN);
            Menu menu = 메뉴_생성_요청(product);

            // Act
            productService.changePrice(product.getId(), request);

            // Assert
            assertThat(menu.isDisplayed()).isTrue();
        }

        @DisplayName("제품이 포함된 메뉴의 가격이 메뉴가 가진 제품의 가격의 합보다 낮다면 메뉴의 전시를 유지한다.")
        @Test
        void menuPriceUnderThenUnDisplayed() {
            // Arrange
            Product product = 제품_생성_요청("name", BigDecimal.ONE);
            Product request = createProduct(BigDecimal.valueOf(50));
            Menu menu = 메뉴_생성_요청(product);

            // Act
            productService.changePrice(product.getId(), request);

            // Assert
            assertThat(menu.isDisplayed()).isTrue();
        }
    }

    private Menu 메뉴_생성_요청(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(10);

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menu.setDisplayed(Boolean.TRUE);

        return menuRepository.save(menu);
    }

    private Product 제품_생성_요청(String name, BigDecimal price) {
        Product product = createProduct(name, price);
        return productService.create(product);
    }

    private Product createProduct() {
        return createProduct(null, null);
    }

    private Product createProduct(BigDecimal price) {
        return createProduct(null, price);
    }

    private Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

}