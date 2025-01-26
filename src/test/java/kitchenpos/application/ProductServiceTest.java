package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("상품 등록하기")
    @Nested
    class ProductRegisterTest {

        @DisplayName("이름과 가격을 입력할 수 있다")
        @Test
        void it_can_input_name_and_price() {
            // given
            Product request = new Product();
            request.setName("후라이드치킨");
            request.setPrice(new BigDecimal(16000));

            // when
            Product product = productService.create(request);

            // then
            assertAll(
                    () -> assertThat(product.getName()).isEqualTo(request.getName()),
                    () -> assertThat(product.getPrice()).isEqualTo(request.getPrice()),
                    () -> assertThat(product.getId()).isNotNull()
            );
        }

        @DisplayName("이름에는 비속어와 같이 부적절한 단어는 사용할 수 없다")
        @Test
        void it_cannot_use_inappropriate_words() {
            // given
            Product request = new Product();
            request.setName("holy shit 맛있는 치킨");
            request.setPrice(new BigDecimal(16000));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @DisplayName("이름은 반드시 입력해야 합니다")
        @Test
        void name_must_be_input() {
            // given
            Product request = new Product();
            request.setPrice(new BigDecimal(16000));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @DisplayName("상품의 가격은 0원 이상이어야 한다")
        @Test
        void price_must_be_over_0() {
            // given
            Product request = new Product();
            request.setName("JPA 치킨");
            request.setPrice(new BigDecimal(-1));

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("상품 가격 변경하기")
    @Nested
    class ProductChangePriceTest {

        private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        private static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
        private static final UUID 치킨류_MENU_GROUP_UUID = UUID.fromString("d9bc21ac-cc10-4593-b506-4a40e0170e02");
        private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
        private static final BigDecimal 후라이드치킨_MENU_DEFAULT_PRICE = new BigDecimal(20000);
        private static final String 후라이드치킨_PRODUCT_NAME = "후라이드치킨";
        private static final String 후라이드_치킨_MENU_NAME = "후라이드 치킨 메뉴";
        private static final String 치킨류_MENU_GROUP_NAME = "치킨류";

        @BeforeEach
        void setup() {
            Product product = new Product();
            product.setId(후라이드치킨_PRODUCT_UUID);
            product.setName(후라이드치킨_PRODUCT_NAME);
            product.setPrice(후라이드치킨_DEFAULT_PRICE);
            product = productRepository.save(product);

            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(치킨류_MENU_GROUP_UUID);
            menuGroup.setName(치킨류_MENU_GROUP_NAME);
            menuGroupRepository.save(menuGroup);

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setId(후라이드치킨_MENU_UUID);
            menu.setName(후라이드_치킨_MENU_NAME);
            menu.setPrice(후라이드치킨_MENU_DEFAULT_PRICE);
            menu.setMenuGroup(menuGroup);
            menu.setMenuProducts(List.of(menuProduct));

            menuRepository.save(menu);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("상품의 가격을 변경할 수 있다")
        @Test
        void it_can_change_price() {
            // given
            Product product = productRepository.findById(후라이드치킨_PRODUCT_UUID)
                    .orElseThrow(NoSuchElementException::new);
            BigDecimal newPrice = new BigDecimal(21000);
            product.setPrice(newPrice);

            // when
            Product changedProduct = productService.changePrice(product.getId(), product);

            // then
            assertThat(changedProduct.getPrice()).isEqualTo(newPrice);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("상품의 가격은 0원 이상이어야 한다")
        @Test
        void price_must_be_over_0() {
            // given
            Product product = productRepository.findById(후라이드치킨_PRODUCT_UUID)
                    .orElseThrow(NoSuchElementException::new);
            BigDecimal newPrice = new BigDecimal(-1);
            product.setPrice(newPrice);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> productService.changePrice(product.getId(), product);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("메뉴의 총 상품 가격이 메뉴 가격보다 작으면 메뉴는 전시되지 않는다")
        @Test
        void menu_should_not_be_displayed_when_product_prices_are_less_than_menu_price() {
            // given
            Product product = productRepository.findById(후라이드치킨_PRODUCT_UUID)
                    .orElseThrow(NoSuchElementException::new);
            BigDecimal newPrice = new BigDecimal(15000);
            product.setPrice(newPrice);

            // when
            productService.changePrice(product.getId(), product);

            // then
            Menu menu = menuRepository.findById(후라이드치킨_MENU_UUID)
                    .orElseThrow(NoSuchElementException::new);
            assertThat(menu.isDisplayed()).isFalse();
        }
    }

    @DisplayName("상품 목록 조회하기")
    @Nested
    class ProductListTest {
        private static final int TOTAL_PRODUCT_COUNT = 6;

        @SqlGroup({
                @Sql(value = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @DisplayName("모든 상품을 조회할 수 있다")
        @Test
        void it_can_retrieve_all_products() {
            // when
            List<Product> products = productService.findAll();

            // then
            assertThat(products).hasSize(TOTAL_PRODUCT_COUNT);
        }
    }
}
