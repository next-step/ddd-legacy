package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName(value = "ProductService 테스트")
@Import(PurgomalumConfiguration.class)
public class ProductTest {

    private static final BigDecimal BIG_DECIMAL_MINUS_ONE = BigDecimal.valueOf(-1);
    private static final String TEST_PRODUCT_NAME = "TEST치킨";
    private static final UUID 후라이드치킨_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");

    @Autowired
    private ProductService productService;
    @SpyBean
    private ProductRepository productRepository;
    @Autowired
    private PurgomalumClient mockPurgomalumClient;
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;


    @DisplayName(value = "상품 등록 기능")
    @Nested
    class ProductCreateTest {

        @DisplayName(value = "가격은 0원 이상이어야 한다.")
        @Test
        void createProduct() {
            //상품명이나 가격이 없을때 에러처리
            Product product = Product(후라이드치킨_UUID, "", BIG_DECIMAL_MINUS_ONE);

            ThrowingCallable throwingCallable = () -> productService.create(product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "상품명이 없거나 비속어가 있으면 안됩니다.")
        @ParameterizedTest
        @CsvSource(value = {",", "fucking 맛있는 치킨"})
        void productInvalidName(final String productName) {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = Product(후라이드치킨_UUID, productName, BigDecimal.ONE);
            Mockito.when(mockPurgomalumClient.containsProfanity(productName)).thenReturn(true);

            //에러처리
            ThrowingCallable throwingCallable = () -> productService.create(product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "상품을 등록합니다.")
        @Test
        void productCreate() {
            //상품명에 빈값이나 비속어가 들어간 경우
            Product product = Product(후라이드치킨_UUID, TEST_PRODUCT_NAME, BigDecimal.ONE);
            //에러처리
            productService.create(product);

            //행위검증
            verify(productRepository, times(1)).save(ArgumentCaptor.forClass(Product.class).capture());
        }

    }

    @DisplayName(value = "상품 가격 변경 기능")
    @Nested
    class ProductPriceChangeTest {

        private static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal(20000);
        private static final String TEST_MENU_NAME = "후라이드 치킨메뉴";

        @DisplayName(value = "변경할 상품의 가격은 0원 이상이어야 한다.")
        @Test
        void zeroProductPrice() {
            //상품명이나 가격이 없을때 에러처리
            Product product = Product(후라이드치킨_UUID, TEST_PRODUCT_NAME, BIG_DECIMAL_MINUS_ONE);

            ThrowingCallable throwingCallable = () -> productService.changePrice(후라이드치킨_UUID, product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 상품들의 총 가격 합이 메뉴의 가격보다 크면, 유효하지 않습니다")
        @Test
        void invalidTotalProductPrice() {

            Product product = Product(후라이드치킨_UUID, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
            //상품을 등록한다.
            productService.create(product);
            //메뉴에 상품을 등록한다.
            Menu menu = new Menu();
            menu.setId(후라이드치킨_UUID);
            menu.setName(TEST_MENU_NAME);
            menu.setPrice(TEST_PRODUCT_PRICE);
          /*  menu.setMenuGroup();
            menu.setDisplayed(true);
            menu.setMenuProducts();
            menuService.create()*/
            //등록된 상품을 꺼내온다.
            //상품 가격을 변경한다.
            //상품이 비활성화되어있는지 체크한다.


            //

            ThrowingCallable throwingCallable = () -> productService.changePrice(후라이드치킨_UUID, product);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }
    }
















    private static Product Product(String name, int price) {
        return Product(null, name, new BigDecimal(price));
    }

    private static Product Product(UUID uuid, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
