package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityClient;
import kitchenpos.helper.InMemoryMenuRepository;
import kitchenpos.helper.InMemoryProductRepository;
import kitchenpos.helper.InMemoryProfanityClient;
import kitchenpos.helper.MenuFixture;
import kitchenpos.helper.ProductFixture;
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

    @DisplayName("상품 가격 변경 테스트")
    @Nested
    class ChangePriceTest {

        @DisplayName("상품 가격을 변경 할 수 있다.")
        @Test
        void test01() {
            // given
            Product product = ProductFixture.create(6000);
            productRepository.save(product);
            var request = new Product();
            request.setPrice(BigDecimal.valueOf(4500));

            // when
            Product actual = testTarget.changePrice(product.getId(), request);

            // then
            assertThat(actual.getPrice())
                .isEqualTo(request.getPrice());
        }

        @DisplayName("상품 가격은 0원 이상이다.")
        @Test
        void test02() {
            // given
            Product product = ProductFixture.create(6000);
            productRepository.save(product);
            Product request = new Product();
            request.setPrice(BigDecimal.valueOf(-1));

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.changePrice(product.getId(), request));
        }

        @DisplayName("상품 가격 변경으로 인해, 해당 상품을 메뉴 상품으로 갖는 메뉴의 가격이 메뉴 상품 가격의 총합보다 커지는 경우, 메뉴를 감춘다.")
        @Test
        void test03() {
            // given
            Product product = ProductFixture.create("양념 치킨", 6000);
            productRepository.save(product);

            Menu menu1 = MenuFixture.create("양념 치킨 한마리", 6000, product, 1, true);
            Menu menu2 = MenuFixture.create("양념 치킨 두마리", 9000, product, 2, true);
            menuRepository.save(menu1);
            menuRepository.save(menu2);

            var request = new Product();
            request.setPrice(BigDecimal.valueOf(4500));

            // when
            testTarget.changePrice(product.getId(), request);

            // then
            assertAll(
                () -> assertThat(menu1.isDisplayed()).isFalse(),
                () -> assertThat(menu2.isDisplayed()).isTrue()
            );
        }
    }

    @DisplayName("상품 목록 조회 테스트")
    @Nested
    class FindAllTest {

        @DisplayName("상품 목록을 조회 할 수 있다.")
        @Test
        void test01() {
            // given
            Product product1 = ProductFixture.create();
            Product product2 = ProductFixture.create();
            productRepository.save(product1);
            productRepository.save(product2);

            // when
            List<Product> actual = testTarget.findAll();

            // then
            assertThat(actual)
                .anyMatch(product -> product.getId().equals(product1.getId()))
                .anyMatch(product -> product.getId().equals(product2.getId()));
        }
    }

}