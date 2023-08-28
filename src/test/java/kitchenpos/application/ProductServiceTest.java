package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixtures.메뉴_등록;
import static kitchenpos.fixture.MenuFixtures.메뉴_상품_등록;
import static kitchenpos.fixture.ProductFixtures.상품_등록;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;

@DisplayName("상품")
public class ProductServiceTest extends ApplicationTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }


    @DisplayName("상품 전체를 조회한다.")
    @Test
    public void findAll() {
        // given
        Product 등심_돈까스 = 상품_등록("등심돈까스", 15000);
        Product 안심_돈까스 = 상품_등록("안심_돈까스", 18000);
        given(productRepository.findAll()).willReturn(Arrays.asList(등심_돈까스, 안심_돈까스));

        // when
        List<Product> products = productService.findAll();

        // then
        assertAll(
                () -> assertThat(products.size()).isEqualTo(2)
        );
    }

    @DisplayName("상품을 등록한다.")
    @ParameterizedTest
    @ValueSource(strings = {"등심돈까스"})
    public void create(String name) {
        // given
        int price = 15000;
        Product product = 상품_등록(name, price);
        given(purgomalumClient.containsProfanity(product.getName())).willReturn(false);
        given(productRepository.save(any())).willReturn(product);

        // when
        Product createdProduct = productService.create(product);

        // then
        assertAll(
                () -> assertThat(createdProduct.getId()).isInstanceOf(UUID.class),
                () -> assertThat(createdProduct.getName()).isEqualTo(name),
                () -> assertThat(createdProduct.getPrice()).isEqualTo(BigDecimal.valueOf(price))
        );
    }

    @DisplayName("상품 등록 시 상품 이름(비속어)을 체크 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck"})
    void createCheckName(String name) {
        // given
        Product product = 상품_등록(name, 17000);
        when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 등록시 상품 가격(음수)을 체크 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-10000})
    void createCheckPrice(int price) {
        // given
        Product product = 상품_등록("등심돈까스", price);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품가격을 변경한다.")
    @Test
    public void changePrice() {
        // given
        int afterPrice = 18000;
        Product afterProduct = 상품_등록("등심돈까스", afterPrice);

        Product product = 상품_등록("등심돈까스", 15000);
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        Menu menu = 메뉴_등록("등심세트", 20000, true, null,
                Arrays.asList(
                    메뉴_상품_등록(상품_등록("등심돈까스", 15000), 1L),
                    메뉴_상품_등록(상품_등록("음료", 5000), 1L)));
        given(menuRepository.findAllByProductId(any())).willReturn(Arrays.asList(menu));

        // when
        Product expectedProduct = productService.changePrice(UUID.randomUUID(), afterProduct);

        // then
        assertAll(
                () -> assertThat(menu.isDisplayed()).isFalse(),
                () -> assertThat(expectedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(afterPrice))
        );
    }

    @DisplayName("상품 가격 변경시 가격(음수)을 체크 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100000})
    public void changePriceAndCheckPrice(int price) {
        // given
        Product product = 상품_등록("등심돈까스", price);

        // when, then
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격 변경 시 메뉴들의 상품 가격의 합보다 비싼 경우 미노출 처리한다.")
    @ParameterizedTest
    @CsvSource({"20000,15000","30000,25000"})
    public void changePriceAndCheckDisplay(int menuPrice, int menuProductPrice) {
        // given
        Product createdProduct = 상품_등록("등심돈까스", menuProductPrice);

        Product product = 상품_등록("등심돈까스", 15000);
        given(productRepository.findById(any())).willReturn(Optional.of(product));

        MenuProduct menuProduct = 메뉴_상품_등록(상품_등록("등심돈까스", menuProductPrice), 1L);
        Menu menu = 메뉴_등록("등심돈까스+등심돈까스", menuPrice, true, null, Arrays.asList(menuProduct, menuProduct));

        given(menuRepository.findAllByProductId(any()))
                .willReturn(Arrays.asList(menu, menu));

        // when
        productService.changePrice(UUID.randomUUID(), createdProduct);

        // then
        assertAll(
                () -> assertThat(menu.isDisplayed()).isFalse());
    }
}
