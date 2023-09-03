package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.fake.FakeProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    MenuRepository menuRepository;

    @Mock
    PurgomalumClient purgomalumClient;

    ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(new FakeProductRepository(), menuRepository, purgomalumClient);
    }

    @Test
    void 상품을_생성한다() {
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

        final Product 결과 = productService.create(양념치킨());

        assertThat(결과.getId()).isNotNull();
    }

    @Test
    void 상품의_가격을_변경한다() {
        given(menuRepository.findAllByProductId(any())).willReturn(List.of(MenuFixture.간장치킨_메뉴()));
        Product 등록된_간장치킨 = productService.create(간장치킨());

        상품가격_변경하기(BigDecimal.valueOf(20000L), 등록된_간장치킨);
        final Product 결과 = productService.changePrice(등록된_간장치킨.getId(), 등록된_간장치킨);

        assertThat(결과.getPrice()).isEqualTo(BigDecimal.valueOf(20000L));
    }

    @Test
    void 상품을_조회한다() {
        productService.create(양념치킨());
        final List<Product> 결과 = productService.findAll();

        assertThat(결과).hasSize(1);
    }

    @ValueSource(strings = {"비속어 입니다", "이 나쁜 사람"})
    @ParameterizedTest
    void 비속어가_포함된_상품은_등록할_수_없다(final String name) {
        final Product 비속어_상품 = 상품_생성(name, BigDecimal.valueOf(10000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);

        assertThatThrownBy(() -> productService.create(비속어_상품))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Product 상품_생성(final String name, final BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private void 상품가격_변경하기(final BigDecimal price, final Product product) {
        product.setPrice(price);
    }
}
