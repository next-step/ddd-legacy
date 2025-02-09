package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.TestConstant.*;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.productFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.*;


@DisplayName(value = "ProductService 테스트")
public class ProductTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakeDefaultPurgomalumClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName(value = "상품 등록 기능")
    @Nested
    class ProductCreateTest {

        @DisplayName(value = "가격은 0원 이상이어야 한다.")
        @Test
        void createProductTest() {
            //상품명이나 가격이 없을때 에러처리
            Product product = createProduct(후라이드치킨_PRODUCT_UUID, "", BIG_DECIMAL_MINUS_ONE);
            assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
        }

        @DisplayName(value = "상품명이 없거나 비속어가 있으면 안됩니다.")
        @Test
        void productInvalidName() {
            //상품명에 빈값이나 비속어가 들어간 경우
                Product product = createProduct(후라이드치킨_PRODUCT_UUID, 비속어_NAME, BigDecimal.ONE);
            //에러처리
            assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
        }

        @DisplayName(value = "상품을 등록합니다.")
        @Test
        void productCreate() {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = createProduct(후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, BigDecimal.ONE);
            //에러처리
            var responseProduct = productService.create(product);

            assertThat(productRepository.findById(responseProduct.getId())).isNotNull();
            }

    }

    @DisplayName(value = "상품 가격 변경 기능")
    @Nested
    class ProductPriceChangeTest {


        @DisplayName(value = "상품의 가격을 변경합니다")
        @Test
        void changeProductPrice() {

            Product product = createProduct();
            productRepository.save(product);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(createMenuGroup(), menuProduct);
            menuRepository.save(menu);

            product.setPrice(BigDecimal.valueOf(21000));
            productService.changePrice(product.getId(), product);

            assertThat(menuRepository.findById(후라이드치킨_MENU_UUID).orElseThrow().isDisplayed()).isTrue();

        }

        @DisplayName(value = "변경할 상품의 가격은 0원 이상이어야 한다.")
        @Test
        void zeroProductPrice() {
            //상품명이나 가격이 없을때 에러처리
            Product product = createProduct(BIG_DECIMAL_MINUS_ONE);
            assertThatIllegalArgumentException().isThrownBy(() -> productService.changePrice(후라이드치킨_PRODUCT_UUID, product));
        }

        @DisplayName(value = "메뉴의 가격이 메뉴의 상품들의 총 가격 합보다 크면, 비노출처리합니다")
        @Test
        void invalidTotalProductPrice() {
            Product product = createProduct();
            productRepository.save(product);
            Menu menu = createMenu(createMenuGroup(), createMenuProduct(product, 1));
            menuRepository.save(menu);
            product.setPrice(BigDecimal.valueOf(19000));

            productService.changePrice(product.getId(), product);
            assertThat(menuRepository.findById(후라이드치킨_MENU_UUID).orElseThrow().isDisplayed()).isFalse();
        }
    }

    @DisplayName(value = "모든 상품 조회 기능")
    @Nested
    class AllProductFindTest {
        @DisplayName(value = "모든 상품을 조회합니다")
        @Test
        void changeProductPrice() {
            Product product = createProduct();
            productRepository.save(product);
            List<Product> products = productService.findAll();
            assertThat(products.size()).isEqualTo(1);
        }
    }
}
