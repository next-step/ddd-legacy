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
import org.junit.jupiter.api.DisplayName;
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

    @AfterEach
    void tearDown() {
        menuRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void createProduct_Success() {
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

    @DisplayName("상품 가격을 입력하지 않으면 등록할 수 없다.")
    @Test
    void createProduct_WhenPriceIsNull_ThrowsException() {
        // given
        Product request = new Product();
        request.setName(burger().getName());
        request.setPrice(null);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격이 0원 미만이면 등록할 수 없다.")
    @Test
    void createProduct_WhenPriceIsNegative_ThrowsException() {
        // given
        Product request = new Product();
        request.setName(burger().getName());
        request.setPrice(valueOf(-8000));

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("상품 이름이 존재하지 않으면 등록할 수 없다.")
    @Test
    void createProduct_WhenNameIsNull_ThrowsException() {
        // given
        Product request = new Product();
        request.setName(null);
        request.setPrice(BURGER_PRICE);

        // when & then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에 부적절한 단어(비속어)가 포함되면 등록할 수 없다.")
    @Test
    void createProduct_WhenContainsProfanity_ThrowsException() {
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

    @DisplayName("상품을 등록할 때 이름에 부적절한 단어(비속어)가 포함되지 않으면 정상적으로 등록할 수 있다.")
    @Test
    void createProduct_WhenNameIsValid_Success() {
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

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changeProductPrice_Success() {
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

    @DisplayName("상품 가격 변경시 가격이 null 이면 변경할 수 없다.")
    @Test
    void changeProductPrice_WhenPriceIsNull_ThrowsException() {
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

    @DisplayName("상품 가격 변경시 가격이 0원 미만이면 변경할 수 없다.")
    @Test
    void changeProductPrice_WhenPriceIsNegative_ThrowsException() {
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

    @DisplayName("존재하지 않은 상품 ID로 가격을 변경할 수 없다.")
    @Test
    void changeProductPrice_WhenProductNotFound_ThrowsException() {
        // given
        UUID nonExistentProductUd = UUID.randomUUID();
        BigDecimal changedPrice = BigDecimal.valueOf(10000);

        Product updateProduct = new Product();
        updateProduct.setPrice(changedPrice);

        // when & then
        assertThatThrownBy(() -> productService.changePrice(nonExistentProductUd, updateProduct))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품을 전체 조회할 수 있다.")
    @Test
    void findAllProducts_Success() {
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
