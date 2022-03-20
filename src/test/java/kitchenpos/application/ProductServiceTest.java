package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;


@DisplayName("[상품]")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;


    @Test
    @DisplayName("상품의 가격은 0보다 크거나 같아야 한다")
    public void productPriceLessThanZeroTest() {
        Product product = ProductFixture.상품_가격_0원_미만();

        AssertionsForClassTypes.assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격은 0보다 크거나 같아야 한다")
    public void productNamePriceNullTest() {
        Product product = ProductFixture.상품_가격_이름_NULL();

        AssertionsForClassTypes.assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품이 정상적으로 등록된다.")
    public void productNamePurgomalumTest() {

        Product request = ProductFixture.상품();

        lenient().when(purgomalumClient.containsProfanity(request.getName())).thenReturn(false);
        given(productRepository.save(any())).willReturn(request);

        Product product = productService.create(request);

        assertThat(product.getId()).isEqualTo(request.getId());
        assertThat(product.getName()).isEqualTo(request.getName());
        assertThat(product.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("변경하고자 하는 상품이 있어야 한다.")
    public void existProductTest() {
        UUID uuid = UUID.randomUUID();
        Product requestProduct = ProductFixture.변경_상품(uuid);

        given(productRepository.findById(uuid)).willReturn(Optional.of(requestProduct));

        Optional<Product> product = productRepository.findById(uuid);
        assertThat(product.isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("상품의 가격이 정상적으로 변경되었다")
    public void changePriceTest() {
        UUID uuid = UUID.randomUUID();
        Product requestProduct = ProductFixture.변경_값();
        Menu menu = MenuFixture.메뉴();

        given(productRepository.findById(uuid)).willReturn(Optional.of(ProductFixture.변경_상품(uuid)));
        given(menuRepository.findAllByProductId(uuid)).willReturn(Collections.singletonList(menu));

        Product product = productService.changePrice(uuid, requestProduct);

        assertThat(product.getPrice()).isEqualTo(requestProduct.getPrice());
    }

    @Test
    @DisplayName("메뉴 상품의 가격의 합이 메뉴의 가격보다 클 경우 메뉴의 판매를 중단(false)한다")
    public void validateUpdateProductTest() {
        UUID uuid = UUID.randomUUID();
        Product requestProduct = ProductFixture.변경_값();
        Menu menu = MenuFixture.메뉴();

        given(productRepository.findById(uuid)).willReturn(Optional.of(ProductFixture.변경_상품(uuid)));
        given(menuRepository.findAllByProductId(uuid)).willReturn(Collections.singletonList(menu));

        Product product = productService.changePrice(uuid, requestProduct);
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("상품의 전체 목록 조회")
    public void searchProductAllTest() {
        List<Product> requestProducts = ProductFixture.상품_목록();
        given(productRepository.findAll()).willReturn(requestProducts);
        List<Product> products = productRepository.findAll();

        assertThat(requestProducts.size()).isEqualTo(products.size());
    }
}