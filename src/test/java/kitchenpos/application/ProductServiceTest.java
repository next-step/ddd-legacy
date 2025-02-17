package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ProductServiceTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        menuRepository = mock(MenuRepository.class);
        purgomalumClient = mock(PurgomalumClient.class);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 생성한다")
    @Nested
    class CreateProductTest {

        @Test
        @DisplayName("상품을 정상적으로 생성한다")
        void create() {
            // given
            Product request = createProduct("멕시칸치킨", BigDecimal.valueOf(12000));

            given(purgomalumClient.containsProfanity(anyString()))
                .willReturn(false);
            given(productRepository.save(any(Product.class)))
                .willAnswer(t -> {
                    Product saved = t.getArgument(0);
                    saved.setId(request.getId());
                    return saved;
                });

            // when
            Product created = productService.create(request);

            // then
            assertThat(created.getId()).isEqualTo(request.getId());
            assertThat(created.getName()).isEqualTo(request.getName());
            assertThat(created.getPrice()).isEqualByComparingTo(request.getPrice());
        }

        @Test
        @DisplayName("상품의 가격은 0원 이상이여야 한다.")
        void create_invalid_price() {
            // given
            Product request = createProduct("음수상품", BigDecimal.valueOf(-1));

            given(purgomalumClient.containsProfanity("0원짜리"))
                .willReturn(true);

            // when & then
            assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품의 이름은 비속어가 포함될 수 없다.")
        void create_profanity_name() {
            // given
            Product request = createProduct("비속어", BigDecimal.valueOf(12000));

            given(purgomalumClient.containsProfanity("비속어"))
                .willReturn(true);

            // when & then
            assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("상품의 가격 수정하기")
    @Nested
    class ChangePrice {

        @Test
        @DisplayName("상품의 가격 정상 수정하기")
        void changePrice() {
            // given
            Product product = createProduct(BigDecimal.valueOf(12000));
            Product request = createProduct(BigDecimal.valueOf(14000));

            given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
            given(menuRepository.findAllByProductId(product.getId()))
                .willReturn(List.of());

            // when
            Product updated = productService.changePrice(product.getId(), request);

            // then
            assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(14000));
        }

        @Test
        @DisplayName("가격 변경 후, 변경된 상품이 속한 메뉴의 가격이 총 합(상품 * 개수) 높다면 메뉴를 비전시로 변경된다.")
        void changePrice_success_hide() {
            // given
            Product product = createProduct(BigDecimal.valueOf(5000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setQuantity(2);
            menuProduct.setProduct(product);

            Menu menu = createMenu(BigDecimal.valueOf(10000), List.of(menuProduct), true);

            Product request = new Product();
            request.setPrice(BigDecimal.valueOf(9000));

            given(productRepository.findById(product.getId()))
                .willReturn(Optional.of(product));
            given(menuRepository.findAllByProductId(product.getId()))
                .willReturn(List.of(menu));

            // when
            productService.changePrice(product.getId(), request);

            // then
            assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(9000));
            assertThat(menu.isDisplayed()).isTrue();
        }
    }

    @Test
    @DisplayName("상품 정보를 전부 가져올 수 있다.")
    void findAll() {
        // given
        Product product1 = createProduct("BHC", BigDecimal.valueOf(23000));
        Product product2 = createProduct("BBQ", BigDecimal.valueOf(25000));
        Product product3 = createProduct("네네", BigDecimal.valueOf(18000));

        given(productRepository.findAll())
            .willReturn(List.of(product1, product2, product3));

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products).hasSize(3);
        assertThat(products.get(0).getName()).isEqualTo("BHC");
        assertThat(products.get(1).getName()).isEqualTo("BBQ");
        assertThat(products.get(2).getName()).isEqualTo("네네");
    }
}
