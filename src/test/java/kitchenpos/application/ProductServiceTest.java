package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/*
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest 스프링 컨텍스트 컨테이너가 올라오는게 너무 오래걸림 => MockitoExtension 사용
*/
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    // @MockBean // 가짜 객체를 MockBean으로 덮어씌움
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp(){
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    /**
     * SpringBootTest 통합테스트 문제점: 외부 API 호출을 한다면 많은 테스트로 인해 과금이 일어날 수 있음.
     * 해결방법 : MockBean 사용
     */
    @Test
    void 상품을_등록할_수_있다(){
        final Product product = createProductRequest("후라이드", 20_000L);
        final var response = createProduct("후라이드", 20_000L);

        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        given(productRepository.save(any())).willReturn(response);

        final Product actual = productService.create(product);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 상품의_가격이_올바르지_않으면_예외가_발생한다(){
        final Product product = createProductRequest(-1000L);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }

    // test fixture
    private static Product createProductRequest() {
        return createProductRequest(20_000L);
    }

    private static Product createProductRequest(final long price) {
        return createProductRequest("후라이드", price);
    }

    private static Product createProductRequest(final String name, final long price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private static Product createProduct(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}