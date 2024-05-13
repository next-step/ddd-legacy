package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("`상품`을 생성할 수 있다.")
    @Test
    void createProductWithValidInput() {
        Product given = new Product();
        given.setName("상품 이름");
        given.setPrice(BigDecimal.valueOf(1000));

        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.save(any())).then(invocation -> invocation.getArgument(0));

        var product = productService.create(given);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(given.getName());
        assertThat(product.getPrice()).isEqualTo(given.getPrice());
        assertThat(product.getId()).isNotNull();
    }

    @DisplayName("`상품`의 가격은 0 이상이어야 한다")
    @Test
    void createProductWithNegativePrice() {
        Product request = new Product();
        request.setName("상품 이름");
        request.setPrice(BigDecimal.valueOf(-1000));

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @DisplayName("`상품`의 가격은 null이 아니어야 한다")
    @Test
    void createProductWithNullPrice() {
        Product request = new Product();
        request.setName("상품 이름");
        request.setPrice(null);

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @DisplayName("`상품`의 이름은 null이 아니어야 한다")
    @Test
    void createProductWithNullName() {
        Product request = new Product();
        request.setName(null);
        request.setPrice(BigDecimal.valueOf(1000));

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @DisplayName("`상품`의 이름에 욕설이 포함되어 있으면 생성할 수 없다")
    @Test
    void createProductWithProfanityInName() {
        Product request = new Product();
        request.setName("대충 나쁜 단어");
        request.setPrice(BigDecimal.valueOf(1000));

        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @DisplayName("`상품`의 가격을 변경할 수 있다")
    @Test
    void changeProductPriceWithValidInput() {
        Product request = new Product();
        request.setName("상품 이름");
        request.setPrice(BigDecimal.valueOf(2000));

        Product existingProduct = new Product();
        existingProduct.setId(UUID.randomUUID());
        existingProduct.setName("상품 이름");
        existingProduct.setPrice(BigDecimal.valueOf(3000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(existingProduct);
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setMenuProducts(List.of(menuProduct));
        menu.setPrice(BigDecimal.valueOf(1500));
        menu.setDisplayed(true);

        when(productRepository.findById(any())).thenReturn(java.util.Optional.of(existingProduct));
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

        when(productRepository.findById(any())).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class,
            () -> productService.changePrice(UUID.randomUUID(), request));
    }

    @DisplayName("'상품'의 가격이 변경되어, '상품'이 포함된 기존 '메뉴' 가격보다 높으면 숨김 처리 된다.")
    @Test
    void changeProductPriceWithHigherPriceThanMenu() {
        Product request = new Product();
        request.setName("상품 이름");
        request.setPrice(BigDecimal.valueOf(1000));

        Product existingProduct = new Product();
        existingProduct.setId(UUID.randomUUID());
        existingProduct.setName("상품 이름");
        existingProduct.setPrice(BigDecimal.valueOf(2000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(existingProduct);
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setMenuProducts(List.of(menuProduct));
        menu.setPrice(BigDecimal.valueOf(1500));
        menu.setDisplayed(true);

        when(productRepository.findById(any())).thenReturn(java.util.Optional.of(existingProduct));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

        productService.changePrice(existingProduct.getId(), request);

        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("모든 `상품`를 조회할 수 있다.")
    @Test
    void findAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));

        assertThat(productService.findAll()).hasSize(2);
    }
}
