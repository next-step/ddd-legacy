package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductService sut;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        sut = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 신규 등록한다")
    @Test
    void testCreate() {
        // given
        var request = ProductFixture.create();
        given(purgomalumClient.containsProfanity(request.getName())).willReturn(false);
        given(productRepository.save(any(Product.class))).willReturn(request);

        // when
        Product actual = sut.create(request);

        // then
        assertThat(actual).isEqualTo(request);
    }

    @DisplayName("상품명에 욕설이 포함되면 상품을 신규 등록할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "dummy", "dork"})
    void testCreateWhenProductNameContainPurg(String productName) {
        // given
        var request = ProductFixture.create(productName);
        given(purgomalumClient.containsProfanity(productName)).willReturn(true);

        // when // then
        assertThatThrownBy(() -> sut.create(request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명이 null이면 상품을 신규 등록할 수 없다.")
    @Test
    void testCreateWhenProductNameIsNull() {
        // given
        var request = ProductFixture.create(null);

        // when // then
        assertThatThrownBy(() -> sut.create(request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격은 null일 수 없다 상품을 신규 등록할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-10_000, -1_000})
    void testCreateWhenProductPriceIsNegative(int productPrice) {
        // given
        var request = ProductFixture.create(productPrice);

        // when // then
        assertThatThrownBy(() -> sut.create(request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 수정한다")
    @Test
    void testChangePrice() {
        // given
        var request = ProductFixture.create(10_000);

        given(productRepository.findById(request.getId())).willReturn(Optional.of(request));

        // when
        Product actual = sut.changePrice(request.getId(), request);

        // then
        assertThat(actual.getId()).isEqualTo(request.getId());
        assertThat(actual.getPrice()).isEqualTo(request.getPrice());
        assertThat(actual.getName()).isEqualTo(request.getName());
    }

    @DisplayName("상품의 가격을 0미만의 숫자로 수정할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {-10_000, -1_000})
    void testChangePriceWhenPriceIsNegative(int price) {
        // given
        var request = ProductFixture.create(price);

        // when // then
        assertThatThrownBy(() -> sut.changePrice(request.getId(), request))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 상품을 조회한다")
    @Test
    void testChangePriceWhenPriceIsNull() {
        // given
        var products = List.of(
            ProductFixture.create("product1"),
            ProductFixture.create("product2"),
            ProductFixture.create("product3")
        );

        given(productRepository.findAll()).willReturn(products);

        // when
        List<Product> actual = sut.findAll();

        // then
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).getName()).isEqualTo("product1");
        assertThat(actual.get(1).getName()).isEqualTo("product2");
        assertThat(actual.get(2).getName()).isEqualTo("product3");
    }
}
