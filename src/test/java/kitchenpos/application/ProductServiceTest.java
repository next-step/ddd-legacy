package kitchenpos.application;

import static kitchenpos.builder.TestFixtureFactory.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kitchenpos.builder.TestFixtureFactory;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        menuRepository = mock(MenuRepository.class);
        purgomalumClient = mock(PurgomalumClient.class);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    @DisplayName("상품을 등록할 수 있다")
    void create() {
        // given
        Product request = createProductRequest("김치", 5000);
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Product result = productService.create(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("김치");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(5000));
    }

    @Test
    @DisplayName("상품 가격은 0원 미만이면 예외가 발생한다.")
    void product_price_exception() {
        // given
        Product request = createProductRequest("김치", -1000);

        // when // then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 이름에 비속어를 넣으면 예외가 발생한다.")
    void product_name_exception() {
        // given
        Product request = createProductRequest("fuck", 5000);
        when(purgomalumClient.containsProfanity("fuck")).thenReturn(true);

        // when // then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 변경할 수 있다")
    void change_price() {
        // given
        Product product = createProduct("김치", 5000);
        Product request = createProductRequest("김치", 6000);
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of());

        // when
        Product result = productService.changePrice(product.getId(), request);

        // then
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(6000));
    }

    @Test
    @DisplayName("상품 가격이 변하면 메뉴의 판매 가격이 재료 가격의 총합보다 낮은 메뉴는 게시가 중단된다")
    void change_price_exception() {
        // given
        Product product = createProduct("김치", 5000);
        Menu menu = TestFixtureFactory.createMenuWithProductAndGroup("김치찌개", 7000, product);
        Product request = createProductRequest("김치", 8000);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));

        // when
        productService.changePrice(product.getId(), request);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("전체 상품을 조회할 수 있다")
    void find_all() {
        // given
        List<Product> products = List.of(
                createProduct("김치", 5000),
                createProduct("된장", 3000)
        );
        when(productRepository.findAll()).thenReturn(products);

        // when
        List<Product> result = productService.findAll();

        // then
        assertThat(result).hasSize(2);
    }

    private Product createProductRequest(String name, long price) {
        return new Product(name, BigDecimal.valueOf(price));
    }
}
