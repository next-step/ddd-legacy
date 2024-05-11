package kitchenpos.application;

import kitchenpos.application.testFixture.ProductFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("상품(product) 서비스 테스트")
@Nested
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    void changePrice() {
    }

    @Test
    void findAll() {
    }

    @Nested
    @DisplayName("상품 생성시,")
    class CreateProduct {

        @DisplayName("상품이 정상 생성된다.")
        @Test
        void createTest() {
            // given
            var product = ProductFixture.newOne(UUID.randomUUID());
            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(productRepository.save(any())).willReturn(product);

            // when
            var actual = productService.create(product);

            // then
            Assertions.assertThat(actual.getName()).isEqualTo("닭고기 300g");
        }

    }
}
