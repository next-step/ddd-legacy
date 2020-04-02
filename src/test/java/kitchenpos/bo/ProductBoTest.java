package kitchenpos.bo;

import kitchenpos.Fixtures;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    ProductDao productDao;

    @InjectMocks
    ProductBo productBo;

    private Product defaultProduct;
    private List<Product> defaultProductList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        defaultProduct = Fixtures.getProduct(1L, "후라이드 치킨", BigDecimal.valueOf(1000L));
        defaultProductList.add(defaultProduct);
        defaultProductList.add(
                Fixtures.getProduct(2L, "양념치킨", BigDecimal.valueOf(2000L)));
        defaultProductList.add(
                Fixtures.getProduct(3L, "구운치킨", BigDecimal.valueOf(3000L)));
    }

    @DisplayName("정산적인 값으로 제품이 생성된다.")
    @Test
    public void createNormal() {
        given(productDao.save(defaultProduct)).willReturn(new Product());

        Product createdProduct = productBo.create(defaultProduct);

        assertThat(createdProduct).isNotNull();
    }

    static Stream<BigDecimal> emptyAndNegativeValue() {
        return Stream.of(BigDecimal.valueOf(-1000L), null);
    }

    @DisplayName("제품의 가격이 없거나 제품의 가격이 0미만이면 생성되지 않는다.")
    @ParameterizedTest
    @MethodSource("emptyAndNegativeValue")
    public void createNotEmptyAndNegative(BigDecimal values) {
        defaultProduct.setPrice(values);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> productBo.create(defaultProduct));
    }

    @DisplayName("모든 제품을 조회할 수 있다.")
    @Test
    public void list() {
        given(productDao.findAll()).willReturn(defaultProductList);

        List<Product> productList = productBo.list();

        assertAll(
                () -> assertEquals(productList.size(), defaultProductList.size()),
                () -> {
                    for(int i = 0 ; i < productList.size() ; i++) {
                        assertEquals(productList.get(i).getId(), defaultProductList.get(i).getId());
                        assertEquals(productList.get(i).getName(), defaultProductList.get(i).getName());
                        assertEquals(productList.get(i).getPrice(), defaultProductList.get(i).getPrice());
                    }
                }
        );
    }
}