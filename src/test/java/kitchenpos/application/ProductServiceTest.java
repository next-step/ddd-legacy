package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import kitchenpos.helper.InMemoryMenuRepository;
import kitchenpos.helper.InMemoryProductRepository;
import kitchenpos.helper.InMemoryProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final ProfanityClient purgomalumClient = new InMemoryProfanityClient();
    private ProductService testTarget;

    @BeforeEach
    void setUp() {
        testTarget = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품 등록 테스트")
    @Nested
    class CreateTest {

        @DisplayName("상품을 등록 할 수 있다.")
        @Test
        void test01() {
            // given
            var request = new Product();
            request.setName("양념 치킨");
            request.setPrice(BigDecimal.valueOf(6000));

            // when
            Product actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(request.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(request.getPrice())
            );
        }

        @DisplayName("상품 가격은 0원 이상이다.")
        @Test
        void test02() {
            // given
            var request = new Product();
            request.setName("양념 치킨");
            request.setPrice(BigDecimal.valueOf(-1));

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }

        @DisplayName("상품 이름은 비어있을 수 없고, 비속어를 포함 할 수 없다.")
        @ParameterizedTest(name = "[{index}] name={0}")
        @ValueSource(strings = {"욕설이 포함된 이름", "비속어가 포함된 이름"})
        @NullAndEmptySource
        void test03(String name) {
            // given
            var request = new Product();
            request.setName(name);
            request.setPrice(BigDecimal.valueOf(6000));

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }
    }

}