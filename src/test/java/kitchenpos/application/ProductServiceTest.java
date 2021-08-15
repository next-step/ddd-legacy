package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.application.fixture.MenuFixture.SHOW_MENU_REQUEST;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_ONE_REQUEST;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT_ONE_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Product dummyProduct;

    private Menu dummyMenu;

    private MenuGroup dummyMenuGroup;

    @BeforeEach
    void setUp() {
        dummyProduct = new Product();
        dummyProduct.setId(UUID.randomUUID());
        dummyProduct.setName("홈런볼");

        dummyMenuGroup = new MenuGroup();
        dummyMenuGroup.setId(UUID.randomUUID());
        dummyMenuGroup.setName("추천 메뉴");

        dummyMenu = new Menu();
        dummyMenu.setId(UUID.randomUUID());
        dummyMenu.setName("치킨");
    }

    @DisplayName("상품 등록 성공")
    @Test
    void createProduct() {
        // Given
        final Product request = new Product();
        request.setName("홈런볼");
        request.setPrice(BigDecimal.valueOf(160000));

        // When
        final Product data = productService.create(request);

        // Then
        final Product actual = productRepository.findById(data.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(actual.getId()).isEqualTo(data.getId());
        assertThat(actual.getName()).isEqualTo(data.getName());
        assertThat(actual.getPrice()).isEqualTo(data.getPrice());
    }

    @DisplayName("상품 등록 실패 - 빈 메뉴 이름 또는 비속어")
    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"fuck"})
    void createProductFailNameNullOrVulgarism(final String name) {
        // Given
        final Product request = new Product();
        request.setName(name);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("상품 등록 실패 - 가격 음수")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1"})
    void createProductFailPriceMinus(final BigDecimal price) {
        // Given
        final Product request = new Product();
        request.setName("라면");
        request.setPrice(price);

        // When, Then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(request));
    }

    @DisplayName("상품 가격 변경")
    @Test
    void changeProductPrice() {
        // Given
        final Product product = productRepository.save(PRODUCT_ONE_REQUEST());
        final BigDecimal expectedPrice = BigDecimal.valueOf(18000);

        final Product request = new Product();
        request.setPrice(expectedPrice);

        // When
        Product actual = productService.changePrice(product.getId(), request);

        // Then
        assertThat(actual.getPrice()).isEqualTo(expectedPrice);
    }

    @DisplayName("상품 가격 변경 - 메뉴의 가격 > 상품의 수량 * 가격, 메뉴 비노출")
    @Test
    void changeProductPriceAndChangeMenuDisplayed() {
        // Given
        productRepository.save(PRODUCT_ONE_REQUEST());
        menuGroupRepository.save(MENU_GROUP_ONE_REQUEST());
        menuRepository.save(SHOW_MENU_REQUEST());

        final BigDecimal expectedPrice = BigDecimal.valueOf(14000);
        final Product request = new Product();
        request.setPrice(expectedPrice);

        // When
        final Product actual = productService.changePrice(PRODUCT_ONE_REQUEST().getId(), request);

        // Then
        final Menu result = menuRepository.findById(SHOW_MENU_REQUEST().getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(result.isDisplayed()).isFalse();
        assertThat(actual.getPrice()).isEqualTo(expectedPrice);
    }
}
