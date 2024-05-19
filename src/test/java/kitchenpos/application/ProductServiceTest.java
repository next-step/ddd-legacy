package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @Mock
    private MenuRepository menuRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("모든 `상품`를 조회할 수 있다.")
    @Test
    void findAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));

        assertThat(productService.findAll()).hasSize(2);
    }

    @Nested
    class CreateProductTests {

        @DisplayName("`상품`을 생성할 수 있다.")
        @Test
        void createProductWithValidInput() {
            Product validProduct = ProductFixture.createProduct();

            when(purgomalumClient.containsProfanity(any())).thenReturn(false);
            when(productRepository.save(any())).then(invocation -> invocation.getArgument(0));

            var product = productService.create(validProduct);

            assertThat(product).isNotNull();
            assertThat(product.getName()).isEqualTo(validProduct.getName());
            assertThat(product.getPrice()).isEqualTo(validProduct.getPrice());
            assertThat(product.getId()).isNotNull();
        }

        @DisplayName("`상품`의 가격은 0 이상이어야 한다")
        @Test
        void createProductWithNegativePrice() {
            Product invalidRequest = ProductFixture.createProduct();
            invalidRequest.setPrice(BigDecimal.valueOf(-1000));

            assertThrows(IllegalArgumentException.class,
                () -> productService.create(invalidRequest));
        }

        @DisplayName("`상품`의 가격은 null이 아니어야 한다")
        @Test
        void createProductWithNullPrice() {
            Product request = ProductFixture.createProduct();
            request.setPrice(null);

            assertThrows(IllegalArgumentException.class, () -> productService.create(request));
        }

        @DisplayName("`상품`의 이름은 null이 아니어야 한다")
        @Test
        void createProductWithNullName() {
            Product request = ProductFixture.createProduct();
            request.setName(null);

            assertThrows(IllegalArgumentException.class, () -> productService.create(request));
        }

        @DisplayName("`상품`의 이름에 욕설이 포함되어 있으면 생성할 수 없다")
        @Test
        void createProductWithProfanityInName() {
            Product request = ProductFixture.createProduct();
            request.setName("대충 나쁜 단어");

            when(purgomalumClient.containsProfanity(any())).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> productService.create(request));
        }
    }

    @Nested
    class ChangeProductTests {

        @DisplayName("`상품`의 가격을 변경할 수 있다")
        @Test
        void changeProductPriceWithValidInput() {
            Product existingProduct = ProductFixture.createProduct();

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(existingProduct);

            Menu menu = MenuFixture.createMenu();
            menu.setMenuProducts(List.of(menuProduct));

            Product request = new Product();
            request.setName(existingProduct.getName());
            request.setPrice(menu.getPrice().add(BigDecimal.valueOf(1000)));

            when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
            when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

            productService.changePrice(existingProduct.getId(), request);

            assertThat(existingProduct.getPrice()).isEqualTo(request.getPrice());
            assertThat(menu.isDisplayed()).isTrue();
        }

        @Test
        void changeProductPriceWithNullPrice() {
            Product request = new Product();
            request.setName("상품 이름");
            request.setPrice(null);

            assertThrows(IllegalArgumentException.class,
                () -> productService.changePrice(UUID.randomUUID(), request));
        }

        @Test
        void changeProductPriceWithNonExistingProduct() {
            Product request = new Product();
            request.setName("상품 이름");
            request.setPrice(BigDecimal.valueOf(1000));

            when(productRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                () -> productService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("'상품'의 가격이 변경되어, '상품'이 포함된 기존 '메뉴' 가격보다 높으면 숨김 처리 된다.")
        @Test
        void changeProductPriceWithHigherPriceThanMenu() {
            Product existingProduct = ProductFixture.createProduct();

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(existingProduct);

            Menu menu = MenuFixture.createMenu();
            menu.setMenuProducts(List.of(menuProduct));

            Product request = new Product();
            request.setName(existingProduct.getName());
            request.setPrice(existingProduct.getPrice().subtract(BigDecimal.valueOf(1000)));

            when(productRepository.findById(any())).thenReturn(Optional.of(existingProduct));
            when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

            productService.changePrice(existingProduct.getId(), request);

            assertThat(menu.isDisplayed()).isFalse();
        }
    }
}
