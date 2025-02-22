package kitchenpos.application;

import static java.math.BigDecimal.valueOf;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import static kitchenpos.fixtures.ProductFixtures.양념치킨;
import static kitchenpos.fixtures.ProductFixtures.후라이드치킨;
import static kitchenpos.fixtures.ProductFixtures.후라이드치킨_가격;
import kitchenpos.infra.PurgomalumClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    void 상품을_등록할_수_있다() {
        // given
        Product request = 후라이드치킨();

        // when
        Product savedProduct = productService.create(request);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("후라이드치킨");
        assertThat(savedProduct.getPrice()).isEqualTo(valueOf(16000));
    }

    @Test
    void 상품_가격을_입력하지_않으면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(후라이드치킨().getName());
        request.setPrice(null);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격이_0원_미만이면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(후라이드치킨().getName());
        request.setPrice(valueOf(-16000));

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 상품_이름이_존재하지_않으면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(null);
        request.setPrice(후라이드치킨_가격);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void 상품의_가격을_변경할_수_있다() {
        // given
        Product request = 후라이드치킨();
        productRepository.save(request);

        UUID productId = request.getId();
        BigDecimal 후라이드치킨_가격 = BigDecimal.valueOf(16000);

        // when
        Product updateProduct = new Product();
        updateProduct.setPrice(후라이드치킨_가격);
        productService.changePrice(productId, updateProduct);

        // then
        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        // 값만 비교
        assertThat(updatedProduct.getPrice().compareTo(후라이드치킨_가격)).isEqualTo(0);

        // scale 제거하고 비교
        assertThat(updatedProduct.getPrice().stripTrailingZeros())
            .isEqualTo(후라이드치킨_가격.stripTrailingZeros()); // stripTrailingZeros()를 사용하면 소수점이 필요 없는 경우 자동으로 정리함
    }

    @Test
    void 상품_가격_변경시_가격이_null_이면_변경할_수_없다() {
        // given
        Product request = 후라이드치킨();
        productRepository.save(request);

        UUID productId = request.getId();

        // when
        Product updateProduct = new Product();
        updateProduct.setPrice(null);

        // then
        assertThatThrownBy(() -> productService.changePrice(productId, updateProduct))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격_변경시_가격이_0원_미만이면_변경할_수_없다() {
        // given
        Product request = 후라이드치킨();
        productRepository.save(request);

        UUID productId = request.getId();
        BigDecimal 후라이드치킨_가격 = BigDecimal.valueOf(-16000);

        // when
        Product updateProduct = new Product();
        updateProduct.setPrice(후라이드치킨_가격);

        // then
        assertThatThrownBy(() -> productService.changePrice(productId, updateProduct))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않은_상품_ID로_가격을_변경할_수_없다() {
        // given
        UUID nonExistentProductUd = UUID.randomUUID();
        BigDecimal changedPrice = BigDecimal.valueOf(16000);

        Product updateProduct = new Product();
        updateProduct.setPrice(changedPrice);

        // when & then
        assertThatThrownBy(() -> productService.changePrice(nonExistentProductUd, updateProduct))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 등록된_상품을_전체_조회할_수_있다() {
        // given
        Product burger = 후라이드치킨();
        Product pizza = 양념치킨();

        // when
        productService.create(burger);
        productService.create(pizza);

        // then
        List<Product> products = productRepository.findAll();
        assertNotNull(products);
        assertThat(products).hasSize(2);
    }
}
