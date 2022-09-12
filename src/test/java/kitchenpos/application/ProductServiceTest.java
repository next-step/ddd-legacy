package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.domain.ProductFixture.product;
import static kitchenpos.fixture.request.ProductRequestFixture.changeProductPriceRequest;
import static kitchenpos.fixture.request.ProductRequestFixture.createProductRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest {
    private ProductService productService;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void beforeEach() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    @DisplayName("상품 목록을 조회할 수 있다")
    void findProduct() {
        // given
        productRepository.save(product());

        // when
        final List<Product> result = productService.findAll();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("상품 가격을 변경할 수 있다")
    void changePrice() {
        // given
        final UUID productId = productRepository.save(product()).getId();
        final Product request = changeProductPriceRequest();

        // when
        final Product result = productService.changePrice(productId, request);

        // then
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1"})
    @DisplayName("상품 가격을 변경할 수 있다")
    void changePriceButNotPrice(final BigDecimal input) {
        // given
        final UUID productId = productRepository.save(product()).getId();
        final Product request = changeProductPriceRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.changePrice(productId, request)
        );
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 가격을 변경할 수 없다")
    void changePriceButNotProduct() {
        // given
        final Product request = changeProductPriceRequest();

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                productService.changePrice(null, request)
        );
    }


    @Test
    @DisplayName("상품이 메뉴에 등록되어 있다면 메뉴에 등록 된 상품의 가격과 수량을 곱한 값이 상품의 가격보다 낮은 경우 메뉴를 숨긴다.")
    void changePriceGreaterThanOriginPriceMultiplyTwiceThanMenuHide() {
        // given
        final UUID productId = productRepository.save(product()).getId();
        final Product request = changeProductPriceRequest(50_000L);

        // when
        final Product result = productService.changePrice(productId, request);

        // then
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("상품을 추가할 수 있다")
    void addProduct() {
        // given
        final Product request = createProductRequest();

        // when
        final Product result = productService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(request.getName()),
                () -> assertThat(result.getPrice()).isEqualTo(request.getPrice())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1.1"})
    @DisplayName("가격이 없거나, 가격이 0원 이하이면 상품을 추가할 수 없다")
    void createMenuNotPrice(final BigDecimal input) {
        // given
        final Product request = createProductRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(request)
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"맛없어"})
    @DisplayName("이름이 없거나, 이름에 비속어가 들어가 있으면 상품을 추가할 수 없다")
    void createMenuNameIsPurgomalum(final String input) {
        // given
        final Product request = createProductRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                productService.create(request)
        );
    }
}
