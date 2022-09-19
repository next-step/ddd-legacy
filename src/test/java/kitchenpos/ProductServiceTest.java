package kitchenpos;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.factory.ProductFactory;
import kitchenpos.fakeobject.FakeProfanityClient;
import kitchenpos.fakeobject.InMemoryMenuRepository;
import kitchenpos.fakeobject.InMemoryProductRepository;
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

import java.math.BigDecimal;
import java.util.List;

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
        final Product request = ProductFactory.of("황금올리브", BigDecimal.valueOf(20000L));

        final Product actual = productService.create(request);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("황금올리브");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(20000L));
    }

    @ParameterizedTest(name = "상품 등록 시, 가격은 필수로 입력되어야 하며 0원 이상이어야 한다. ")
    @NullSource
    @ValueSource(strings = "-1")
    public void create_input_null_and_negative(BigDecimal price) {
        final Product request = ProductFactory.of(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }


    @ParameterizedTest(name = "상품 등록 시, 이름은 필수로 입력되 비속어가 포함되어있으면 안된다.")
    @NullSource
    @ValueSource(strings = {"욕설이 포함된 이름", "비속어가 포함된 이름"})
    public void create_input_null_and_profanity(String name) {
        final Product request = ProductFactory.of(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("상품의 가격을 수정할 수 있다.")
    @Test
    public void changePrice() {
        final Product product = ProductFactory.of("황금올리브", BigDecimal.valueOf(20000L));
        Product create = productRepository.save(product);

        final Product request = new Product();
        request.setPrice(BigDecimal.valueOf(30000L));

        Product actual = productService.changePrice(create.getId(), request);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("황금올리브");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(30000L));
    }


    @ParameterizedTest(name = "상품 수정시 시, 가격은 필수로 입력되어야 하며 0원 이상이어야 한다. ")
    @NullSource
    @ValueSource(strings = "-1000")
    public void changePrice_input_null_and_negative(BigDecimal price) {
        final Product product = ProductFactory.getDefaultProduct();
        productRepository.save(product);

        final Product request = new Product();
        request.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(product.getId(), request));
    }

    // Menu '가'는 상품 'A','B','C'로 구성되어있다.
    // Menu '가'의 가격 10000, 상품 'A' 4000, 'B' 4000, 'C' 3000 일때
    // 상품 'A'의 가격을 2500으로 변경하면 메뉴에 포함된 상품들의 총 가격이 9500 되므로, 메뉴 가격보다 싸진다.
    // 이 경우에는 메뉴 진열이 불가능하다.
    @DisplayName("상품 수정 시, 해당 상품이 포함된 메뉴의 가격이 메뉴에 속한 상품들의 총 가격보다 비싸다면 메뉴 진열이 불가능하다.")
    @Test
    public void changePrice_expansive_then_menu_price() {
        // 메뉴 테스트 코드 작성할때 같이 작성.
    }

    @DisplayName("상품 목록을 조회한다.")
    @Test
    public void findAll() {
        final Product product = ProductFactory.getDefaultProduct();
        productRepository.save(product);

        List<Product> products = productService.findAll();

        assertThat(products).hasSize(1);
    }
}
