package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class ProductServiceTest {

    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final ProductService productService;
    private final PurgomalumClient purgomalumClient;

    ProductServiceTest(MenuGroupRepository menuGroupRepository, ProductRepository productRepository, MenuRepository menuRepository, ProductService productService, PurgomalumClient purgomalumClient) {
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
        this.productService = productService;
        this.purgomalumClient = purgomalumClient;
    }

    private Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return productRepository.save(product);
    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroupRepository.save(menuGroup);
    }

    private Menu createMenu(String name, BigDecimal price, List<MenuProduct> menuProducts, MenuGroup menuGroup) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        return menuRepository.save(menu);
    }

    @Nested
    @DisplayName("상품 생성")
    class CreateProductTest {

        @Test
        @DisplayName("상품을 생성할 수 있다.")
        void create() {
            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setName("간장치킨");
            product.setPrice(BigDecimal.valueOf(17000));

            Product result = productService.create(product);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getName()).isEqualTo("간장치킨");
            Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("상품 생성 시, 상품 가격이 올바르지 않으면 IllegalArgumentException 예외 발생")
        void cannotCreateProductWithInvalidPrice(BigDecimal invalidPrice) {
            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(invalidPrice);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.create(product));
        }
    }

    @Nested
    @DisplayName("상품 가격 변경")
    class ChangeProductPriceTest {

        @Test
        @DisplayName("상품의 가격을 변경할 수 있다.")
        void changePrice() {
            Product product = createProduct("간장치킨", BigDecimal.valueOf(17000));
            Product changedProduct = new Product();
            changedProduct.setId(UUID.randomUUID());
            changedProduct.setPrice(BigDecimal.valueOf(18000));

            Product result = productService.changePrice(product.getId(), changedProduct);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(18000));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("상품의 가격 변경 시, 가격이 올바르지 않으면 IllegalArgumentException 예외 발생")
        void cannotChangeProductPriceWithInvalidPrice(BigDecimal invalidPrice) {
            Product product = createProduct("간장치킨", BigDecimal.valueOf(17000));

            Product productWithInvalidPrice = new Product();
            productWithInvalidPrice.setId(UUID.randomUUID());
            productWithInvalidPrice.setPrice(invalidPrice);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.changePrice(product.getId(), productWithInvalidPrice));
        }

        @Test
        @DisplayName("존재하지 않는 상품의 가격을 변경하려고 하면 NoSuchElementException 예외 발생")
        void cannotChangePriceIfProductNotFound() {
            Product changedProduct = new Product();
            changedProduct.setId(UUID.randomUUID());
            changedProduct.setPrice(BigDecimal.valueOf(16000));

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), changedProduct));
        }

        @Test
        @DisplayName("상품 가격 변경 시 메뉴 가격이 전체 상품 가격보다 크면 판매 불가능으로 변경한다.")
        void changeProductPriceShouldDisableExpensiveMenu() {
            MenuGroup menuGroup = createMenuGroup("한마리메뉴");

            Product product = createProduct("간장치킨", BigDecimal.valueOf(17000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);

            Menu menu = createMenu("간장치킨", BigDecimal.valueOf(18000), List.of(menuProduct), menuGroup);

            Product changedProduct = new Product();
            changedProduct.setId(UUID.randomUUID());
            changedProduct.setPrice(BigDecimal.valueOf(16000));

            productService.changePrice(product.getId(), changedProduct);

            Menu result = menuRepository.findById(menu.getId()).orElseThrow();
            Assertions.assertThat(result.isDisplayed()).isFalse();
        }
    }

    @Nested
    @DisplayName("상품 조회")
    class FindProductTest {

        @Test
        @DisplayName("모든 상품을 조회할 수 있다.")
        void findAll() {
            Product product1 = createProduct("간장치킨", BigDecimal.valueOf(17000));
            Product product2 = createProduct("순살치킨", BigDecimal.valueOf(17000));

            List<Product> result = productService.findAll();

            Assertions.assertThat(result).hasSize(2);
            Assertions.assertThat(result)
                    .extracting(Product::getName)
                    .containsExactlyInAnyOrder("간장치킨", "순살치킨");

            BigDecimal totalPrice = result.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Assertions.assertThat(totalPrice).isEqualByComparingTo(BigDecimal.valueOf(34000));
        }
    }
}
