package kitchenpos.application;

import static java.math.BigDecimal.valueOf;
import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import static kitchenpos.fixtures.ProductFixtures.BURGER_NAME;
import static kitchenpos.fixtures.ProductFixtures.BURGER_PRICE;
import static kitchenpos.fixtures.ProductFixtures.PROFANITY;
import static kitchenpos.fixtures.ProductFixtures.burger;
import static kitchenpos.fixtures.ProductFixtures.pizza;
import kitchenpos.infra.PurgomalumClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    /**
     * 매 테스트 실행 후 DB를 정리하여 일관된 테스트 환경을 유지한다.
     */
    @AfterEach
    void tearDown() {
        menuRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    void 상품을_등록할_수_있다() {
        // given
        Product request = burger();

        // when
        Product savedProduct = productService.create(request);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(BURGER_NAME);
        assertThat(savedProduct.getPrice()).isEqualTo(BURGER_PRICE);
    }

    @Test
    void 상품_가격을_입력하지_않으면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(burger().getName());
        request.setPrice(null);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격이_0원_미만이면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(burger().getName());
        request.setPrice(valueOf(-8000));

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 상품_이름이_존재하지_않으면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(null);
        request.setPrice(BURGER_PRICE);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_이름에_부적절한_단어가_포함되면_등록할_수_없다() {
        // given
        Product request = new Product();
        request.setName(PROFANITY);
        request.setPrice(BURGER_PRICE);

        // when
        when(purgomalumClient.containsProfanity(request.getName())).thenReturn(true);

        // then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);

        verify(purgomalumClient, times(1)).containsProfanity(request.getName()); // API 호출되었는지 검증
    }

    @Test
    void 상품을_등록할_때_이름에_부적절한_단어가_포함되지_않으면_정상적으로_등록할_수_있다() {
        // given
        Product request = burger();

        // when
        when(purgomalumClient.containsProfanity(request.getName())).thenReturn(false);
        Product savedProduct = productService.create(request);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(BURGER_NAME);
        verify(purgomalumClient, times(1)).containsProfanity(request.getName()); // API 호출되었는지 검증
    }

    @Test
    void 상품의_가격을_변경할_수_있다() {
        // given
        Product request = burger();
        productRepository.save(request);

        UUID productId = request.getId();
        BigDecimal CHANGE_BURGER_PRICE = BigDecimal.valueOf(10000);

        // when
        Product updateProduct = new Product();
        updateProduct.setPrice(CHANGE_BURGER_PRICE);
        productService.changePrice(productId, updateProduct);

        // then
        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        // 값만 비교
        assertThat(updatedProduct.getPrice().compareTo(CHANGE_BURGER_PRICE)).isEqualTo(0);

        // scale 제거하고 비교
        assertThat(updatedProduct.getPrice().stripTrailingZeros())
            .isEqualTo(CHANGE_BURGER_PRICE.stripTrailingZeros()); // stripTrailingZeros()를 사용하면 소수점이 필요 없는 경우 자동으로 정리함
    }

    @Test
    void 상품_가격_변경시_가격이_null이면_변경할_수_없다() {
        // given
        Product request = burger();
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
        Product request = burger();
        productRepository.save(request);

        UUID productId = request.getId();
        BigDecimal CHANGE_BURGER_PRICE = BigDecimal.valueOf(-10000);

        // when
        Product updateProduct = new Product();
        updateProduct.setPrice(CHANGE_BURGER_PRICE);

        // then
        assertThatThrownBy(() -> productService.changePrice(productId, updateProduct))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않은_상품_ID로_가격을_변경할_수_없다() {
        // given
        UUID nonExistentProductUd = UUID.randomUUID();
        BigDecimal changedPrice = BigDecimal.valueOf(10000);

        Product updateProduct = new Product();
        updateProduct.setPrice(changedPrice);

        // when & then
        assertThatThrownBy(() -> productService.changePrice(nonExistentProductUd, updateProduct))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 등록된_상품을_전체_조회할_수_있다() {
        // given
        Product burger = burger();
        Product pizza = pizza();

        // when
        productService.create(burger);
        productService.create(pizza);

        // then
        List<Product> products = productRepository.findAll();
        assertNotNull(products);
        assertThat(products).hasSize(2);
    }
}
