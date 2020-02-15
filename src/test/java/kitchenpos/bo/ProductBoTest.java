package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.dao.TestProductDao;
import kitchenpos.model.Product;
import kitchenpos.model.ProductBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.bo.Fixture.치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class ProductBoTest {

    private final ProductDao productDao = new TestProductDao();

    private ProductBo productBo;
    private Product product;

    @BeforeEach
    void setUp() {
        product = 치킨();
        productBo = new ProductBo(productDao);
    }

    @Nested
    @DisplayName("제품 생성 테스트")
    class ProductCreateTest {
        @Test
        @DisplayName("새로운 제풍을 생성 할 수 있다.")
        void create() {
            //given - when
            Product expected = productBo.create(product);

            //then
            Assertions.assertAll(
                    () -> assertThat(expected).isNotNull(),
                    () -> assertThat(expected.getId()).isEqualTo(product.getId()),
                    () -> assertThat(expected.getPrice()).isEqualTo(product.getPrice()),
                    () -> assertThat(expected.getName()).isEqualTo(product.getName())
            );
        }

        @ParameterizedTest
        @ValueSource(longs = {-1000, -3000})
        @DisplayName("제품의 가격은 0 이상의 숫자이다.")
        void creat2(long price) {
            //given
            Product wrongPriceProduct = ProductBuilder
                    .aProduct()
                    .withPrice(BigDecimal.valueOf(price))
                    .withId(1L)
                    .withName("잘못된가격의상품")
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(wrongPriceProduct));
        }
    }

    @Test
    @DisplayName("전체 제품 리스트를 조회 해 볼 수 있다.")
    void list() {
        //given
        Product actual = productBo.create(product);

        //when
        List<Product> expected = productBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(expected).isNotNull(),
                () -> assertThat(expected.stream().anyMatch(i -> {
                    Long expectedId = i.getId();
                    Long actualId = actual.getId();

                    return expectedId.equals(actualId);
                }))
        );
    }

}
