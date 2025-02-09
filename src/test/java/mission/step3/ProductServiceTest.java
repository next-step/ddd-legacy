package mission.step3;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("상품을 생성한다")
    void create() {
        // given
        Product request = new Product();
        request.setName("아메리카노");
        request.setPrice(BigDecimal.valueOf(4000));

        given(purgomalumClient.containsProfanity(anyString()))
                .willReturn(false);
        given(productRepository.save(any(Product.class)))
                .willAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        // when
        Product created = productService.create(request);

        // then
        assertThat(created.getName()).isEqualTo("아메리카노");
        assertThat(created.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4000));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 이름에 부적절한 단어가 포함되어 있으면 예외가 발생한다")
    void createWithProfanity() {
        // given
        Product request = new Product();
        request.setName("부적절한 이름");
        request.setPrice(BigDecimal.valueOf(4000));

        given(purgomalumClient.containsProfanity(anyString()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격이 null이면 예외가 발생한다")
    void createWithNullPrice() {
        // given
        Product request = new Product();
        request.setName("아메리카노");
        request.setPrice(null);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격이 음수이면 예외가 발생한다")
    void createWithNegativePrice() {
        // given
        Product request = new Product();
        request.setName("아메리카노");
        request.setPrice(BigDecimal.valueOf(-1000));

        // when & then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 변경한다")
    void changePrice() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(4000));

        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(4500));

        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(productId))
                .willReturn(List.of());

        // when
        Product updated = productService.changePrice(productId, request);

        // then
        assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));
    }

    @Test
    @DisplayName("상품 가격 인상으로 메뉴 가격이 원가보다 낮아지면 판매 중지로 변경된다")
    void changePriceToHigherThanMenuPrice() {
        // given
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(4000));

        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(9000));
        menu.setDisplayed(true);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        menu.setMenuProducts(List.of(menuProduct));

        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(5000));

        given(productRepository.findById(productId))
                .willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(productId))
                .willReturn(List.of(menu));

        // when
        productService.changePrice(productId, request);

        // then
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(5000));
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("모든 상품을 조회한다")
    void findAll() {
        // given
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setName("아메리카노");
        product1.setPrice(BigDecimal.valueOf(4000));

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("카페라떼");
        product2.setPrice(BigDecimal.valueOf(4500));

        given(productRepository.findAll())
                .willReturn(List.of(product1, product2));

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("아메리카노");
        assertThat(products.get(1).getName()).isEqualTo("카페라떼");
    }
}
