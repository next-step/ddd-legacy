package kitchenpos;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import racingcar.Car;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private MenuRepository menuRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        menuRepository = new InMemoryMenuRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    public void create() {
        final Product request = new Product();
        request.setName("황금올리브");
        request.setPrice(BigDecimal.valueOf(20000L));

        final Product actual = productService.create(request);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("황금올리브");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(20000L));
    }

    @ParameterizedTest(name = "상품 등록 시, 가격은 필수로 입력되어야 하며 0원 이상이어야 한다. ")
    @NullSource
    @ValueSource(strings = "-1")
    public void create_input_null_and_negative(BigDecimal price) {
        final Product request = new Product();
        request.setName("황금올리브");
        request.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @ParameterizedTest(name = "상품 등록 시, 이름은 필수로 입력되 비속어가 포함되어있으면 안된다.")
    @NullSource
    @ValueSource(strings = "욕설")
    public void create_input_null_and_profanity(String name) {
        final Product request = new Product();
        request.setName(name);
        request.setPrice(BigDecimal.valueOf(20000L));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }
}
