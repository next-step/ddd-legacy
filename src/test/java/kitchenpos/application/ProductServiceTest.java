package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductServiceTest {
    @SpyBean
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

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
    @MethodSource("provideNonExistProductIdAndPrice")
    @ParameterizedTest
    public void changePrice_non_exist_product(BigDecimal price, UUID productId) {
        //given
        Product product = new Product();
        product.setPrice(price);
        product.setId(productId);

        //when & then
        assertThrows(NoSuchElementException.class, () -> productService.changePrice(productId, product));
    }

    @DisplayName("상품 이름이 존재할 경우 상품 추가 성공한다.")
    @MethodSource("provideExistProductIdAndPrice")
    @ParameterizedTest
    public void changePrice_exist_product(BigDecimal price, UUID productId) {
        //given
        Product product = new Product();
        product.setPrice(price);
        product.setId(productId);

        //when & then
        assertThat(productService.changePrice(productId, product)).isNotNull();
    }

    public static Stream<Arguments> provideExistProductIdAndPrice() {
        Stream<UUID> nonExistProductId = InputProvider.provideExistProductId();
        Stream<BigDecimal> validPrice = InputProvider.provideValidPrice();
        return nonExistProductId.flatMap(
                productId -> validPrice.map(price -> Arguments.of(price, productId))
        );
    }

    public static Stream<Arguments> provideNonExistProductIdAndPrice() {
        Stream<UUID> nonExistProductId = InputProvider.provideNonExistProductId();
        Stream<BigDecimal> validPrice = InputProvider.provideValidPrice();
        return nonExistProductId.flatMap(
                productId -> validPrice.map(price -> Arguments.of(price, productId))
        );
    }
}
