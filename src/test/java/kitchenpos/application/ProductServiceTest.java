package kitchenpos.application;

import static java.util.List.of;
import static java.util.stream.Collectors.toList;
import static kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum.NORMAL;
import static kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum.SLANG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import kitchenpos.domain.Product;
import kitchenpos.testHelper.SpringBootTestHelper;
import kitchenpos.testHelper.fake.PurgomalumClientFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceTest extends SpringBootTestHelper {

    @Autowired
    ProductService productService;

    @Autowired
    PurgomalumClientFake purgomalumClient;

    @BeforeEach
    public void init() {
        super.init();
    }

    @DisplayName("등록할 상품의 가격이 0보다 작으면 에러를 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3})
    void test1(int price) {
        //given
        Product request = new Product("name", BigDecimal.valueOf(price));

        //when && then
        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("등록할 상품의 이름은 반드시 있어야 한다")
    @ParameterizedTest
    @NullSource
    void test2(String name) {
        //given
        purgomalumClient.setReturn(NORMAL);
        Product request = new Product(name, BigDecimal.valueOf(1L));

        //when && //then
        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("등록할 상품의 이름은 비속어를 넣을 수 없다")
    @Test
    void test3() {
        //given
        purgomalumClient.setReturn(SLANG);
        Product request = new Product("name", BigDecimal.valueOf(1L));

        //when && //then
        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 정보를 저장할수 있다")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 10L, 100L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L})
    void test4(long price) {
        //given
        purgomalumClient.setReturn(NORMAL);
        Product request = new Product("name", BigDecimal.valueOf(price));

        //when
        Product result = productService.create(request);

        //then
        assertAll(
            () -> assertThat(result.getId()).isNotNull(),
            () -> assertThat(result.getName()).isEqualTo(request.getName()),
            () -> assertThat(result.getPrice()).isEqualTo(request.getPrice())
        );
    }

    @DisplayName("상품의 가격을 수정할수 있다")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 10L, 100L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L})
    void test5(long price) {
        //given
        purgomalumClient.setReturn(NORMAL);
        Product savedProduct = productService.create(new Product("name", BigDecimal.valueOf(5L)));
        Product request = new Product("name", BigDecimal.valueOf(price));

        //when
        Product changedProduct = productService.changePrice(savedProduct.getId(), request);

        //then
        assertAll(
            () -> assertThat(changedProduct.getPrice()).isEqualByComparingTo(request.getPrice())
        );
    }

    @DisplayName("모든 상품의 정보를 조회할수 있다")
    @ParameterizedTest
    @MethodSource("test6MethodSource")
    void test6(List<Product> products) {
        //given
        purgomalumClient.setReturn(NORMAL);
        for (Product product : products) {
            productService.create(product);
        }
        List<String> expectNames = products.stream()
            .map(Product::getName)
            .collect(toList());

        //when
        List<Product> resultProducts = productService.findAll();

        //then
        assertAll(
            () -> assertThat(resultProducts).extracting("name")
                .containsExactlyElementsOf(expectNames)
        );

    }

    static Stream<List<Product>> test6MethodSource() {
        return Stream.of(
            of(new Product("P1", BigDecimal.valueOf(1L)), new Product("P2", BigDecimal.valueOf(2L))),
            of(new Product("P3", BigDecimal.valueOf(3L))),
            List.of()
        );
    }
}