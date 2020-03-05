package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.dao.TestProductDao;
import kitchenpos.model.Product;
import kitchenpos.model.ProductBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.bo.Fixture.치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class ProductBoTest {

    private final ProductDao productDao = new TestProductDao();

    private ProductBo productBo;

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(productDao);
    }

    @Nested
    @DisplayName("제품 생성 테스트")
    class ProductCreateTest {
        @Test
        @DisplayName("새로운 제풍을 생성 할 수 있다.")
        void create() {
            //given
            Product expected = 치킨();

            //when
            Product actual = productBo.create(expected);

            //then
            Assertions.assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
                    () -> assertThat(actual.getName()).isEqualTo(expected.getName())
            );
        }

        @ParameterizedTest
        @ValueSource(longs = {-1000, -3000})
        @DisplayName("제품의 가격은 0 이상의 숫자이다.")
        void creat2(long price) {
            //given
            Product actual = ProductBuilder
                    .aProduct()
                    .withPrice(BigDecimal.valueOf(price))
                    .withId(1L)
                    .withName("잘못된가격의상품")
                    .build();

            //when then
            assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(actual));
        }
    }

    @Test
    @DisplayName("제품 리스트를 조회 해 볼 수 있다.")
    void list() {
        //given
        Product expected = productBo.create(치킨());

        //when
        List<Product> actual = productBo.list();

        //then
        Assertions.assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(Collections.singletonList(expected))
        );
    }
}