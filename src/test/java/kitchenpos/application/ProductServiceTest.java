package kitchenpos.application;

import kitchenpos.application.fakeobject.FakeMenuRepository;
import kitchenpos.application.fakeobject.FakeProductRepository;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private ProductService productService;

    private FakeProductRepository fakeProductRepository;

    private FakeMenuRepository fakeMenuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        this.fakeMenuRepository = new FakeMenuRepository();
        this.fakeProductRepository = new FakeProductRepository();
        this.productService = new ProductService(fakeProductRepository, fakeMenuRepository, purgomalumClient);
    }

    @DisplayName("가격 정보가 없거나 음수일 경우 상품 추가 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideNullOrMinusPrice")
    @ParameterizedTest
    public void create_invalid_price(BigDecimal price) {
        //given
        Product product = new Product();
        product.setPrice(price);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> productService.create(product));
    }

    @DisplayName("상품 이름이 이미 존재할 경우 상품 추가 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidPrice")
    @ParameterizedTest
    public void create_exist_name(BigDecimal price) {
        //given
        Product product = new Product();
        product.setPrice(price);
        product.setName("test");
        Mockito.when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> productService.create(product));
    }

    @DisplayName("상품 이름이 존재하지 않고 유효한 가격일 경우 상품 추가 성공한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideValidPrice")
    @ParameterizedTest
    public void create_non_exist_name(BigDecimal price) {
        //given
        Product product = new Product();
        product.setPrice(price);
        product.setName("test");
        Mockito.when(purgomalumClient.containsProfanity(product.getName())).thenReturn(false);

        //when & then
        assertThat(productService.create(product)).isNotNull();
    }

    @DisplayName("가격이 유효하지 않을 경우 가격변경에 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideNullOrMinusPrice")
    @ParameterizedTest
    public void changePrice_invalid_price(BigDecimal price) {
        //given
        Product product = new Product();
        product.setPrice(price);
        product.setName("test");

        //when & then
        assertThrows(IllegalArgumentException.class, () -> productService.changePrice(null, product));
    }

    @DisplayName("상품이 존재하지 않을 경우 가격변경에 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistProductId")
    @ParameterizedTest
    public void changePrice_non_exist_product(UUID productId) {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);
        product.setId(fakeProductRepository.findAll().get(0).getId());

        //when & then
        assertThrows(NoSuchElementException.class, () -> productService.changePrice(productId, product));
    }

    @DisplayName("상품 이름이 존재할 경우 상품 추가 성공한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideExistProductId")
    @ParameterizedTest
    public void changePrice_exist_product(UUID productId) {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.ONE);
        product.setId(productId);
        fakeMenuRepository.setMenuProductsOnMenu(fakeProductRepository.findAll());

        //when & then
        assertThat(productService.changePrice(productId, product)).isNotNull();
    }
}
