package kitchenpos.application;

import static kitchenpos.application.MenuProductFixture.뿌링클_1개;
import static kitchenpos.application.MenuProductFixture.콜라_1개;
import static kitchenpos.application.ProductFixture.뿌링클;
import static kitchenpos.application.ProductFixture.콜라;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock(lenient = true)
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @DisplayName("상품 가격 0원 이하 불가")
    @ParameterizedTest(name = "상품금액: [{arguments}]")
    @MethodSource("priceException")
    void createPriceException(BigDecimal price) {
        //given
        Product product = 상품_금액(price);

        //when
        ThrowingCallable actual = () -> productService.create(product);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> priceException() {
        return Stream.of(
            Arguments.of((BigDecimal) null),
            Arguments.of(BigDecimal.valueOf(-1))
        );
    }

    @DisplayName("상품의 이름에 비속어 사용 불가")
    @ParameterizedTest(name = "상품 이름: [{arguments}]")
    @MethodSource("constructorNameException")
    void createNameException(String name) {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(10_000L));
        product.setName(name);

        given(purgomalumClient.containsProfanity("비속어")).willReturn(true);

        //when
        ThrowingCallable actual = () -> productService.create(product);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> constructorNameException() {
        return Stream.of(
            Arguments.of((String) null),
            Arguments.of("비속어")
        );
    }

    @DisplayName("가격 변경")
    @ParameterizedTest(name = "변경할 가격: [{0}], 진열 여부: [{1}]")
    @MethodSource("changePrice")
    void changePrice(long price, boolean expectedDisplayed) {
        //given
        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(뿌링클_1개, 콜라_1개));
        menu.setPrice(BigDecimal.valueOf(11_000L));

        Product 변경할_금액 = 상품_금액(price);

        given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(뿌링클));
        given(menuRepository.findAllByProductId(any(UUID.class))).willReturn(Collections.singletonList(menu));

        //when
        Product product = productService.changePrice(뿌링클.getId(), 변경할_금액);

        //then
        assertAll(
            () -> assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(price)),
            () -> assertThat(menu.isDisplayed()).isEqualTo(expectedDisplayed)
        );
    }

    private static Stream<Arguments> changePrice() {
        return Stream.of(
            Arguments.of(8_000L, false),
            Arguments.of(15_000L, true)
        );
    }

    @DisplayName("가격 변경 예외")
    @ParameterizedTest(name = "변경할 가격: [{arguments}]")
    @MethodSource("priceException")
    void changePriceException(BigDecimal price) {
        //given
        Product 뿌링클_가격_변경 = 상품_금액(price);

        //when
        ThrowingCallable actual = () -> productService.changePrice(뿌링클.getId(), 뿌링클_가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("등록되지 않은 상품의 가격 변경 예외")
    @Test
    void changePriceNotExistProductException() {
        //given
        Product 뿌링클_가격_변경 = 상품_금액(10_000L);

        given(productRepository.findById(any(UUID.class))).willThrow(IllegalArgumentException.class);

        //when
        ThrowingCallable actual = () -> productService.changePrice(뿌링클.getId(), 뿌링클_가격_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("모든 상품 조회")
    @Test
    void findAll() {
        //given
        given(productRepository.findAll()).willReturn(Arrays.asList(뿌링클, 콜라));

        //when
        List<Product> products = productService.findAll();

        //then
        assertThat(products).containsExactly(뿌링클, 콜라);

    }

    private Product 상품_금액(long price) {
        return 상품_금액(BigDecimal.valueOf(price));
    }

    private Product 상품_금액(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);
        return product;
    }
}
