package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    private static final String PRODUCT_NAME = "맛있는 찌낑";
    private static final BigDecimal SAMPLE_PRICE = BigDecimal.valueOf(3000);

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private Product input;
    private Product saved;

    @BeforeEach
    void setUp() {
        input = new Product();
        input.setName(PRODUCT_NAME);
        input.setPrice(SAMPLE_PRICE);

        saved = new Product();
        saved.setName(PRODUCT_NAME);
        saved.setPrice(SAMPLE_PRICE);
        saved.setId(1L);
    }

    @DisplayName("가격이 null 이거나 0보다 작으면 IllegalArgumentException 에러를 낸다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("provideNegativePrice")
    void createFail(BigDecimal bigDecimal) {
        Product sample = new Product();
        sample.setPrice(bigDecimal);
        assertThrows(IllegalArgumentException.class, () -> productBo.create(sample));
    }

    @DisplayName("정상적으로 저장되는 경우")
    @Test
    void create() {
        given(productDao.save(input))
                .willReturn(saved);

        Product result = productBo.create(input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(PRODUCT_NAME);
        assertThat(result.getPrice()).isEqualTo(SAMPLE_PRICE);
    }

    @DisplayName("상품 리스트 조회")
    @Test
    void list() {
        given(productDao.findAll())
                .willReturn(Collections.singletonList(saved));

        List<Product> result = productBo.list();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(PRODUCT_NAME);
        assertThat(result.get(0).getPrice()).isEqualTo(SAMPLE_PRICE);
    }

    private static Stream<Arguments> provideNegativePrice() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-3))
        );
    }
}
