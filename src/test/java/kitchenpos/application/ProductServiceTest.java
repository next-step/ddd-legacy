package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.MoneyConstants.*;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.ProductFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private ProductService productService;

    final private static String FAIL_PREFIX = "[실패] ";

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("상품 등록 테스트")
    class SaveProduct {

        @ParameterizedTest
        @ValueSource(longs = {빵원, 만원})
        @DisplayName("상품을 정상적으로 등록할 수 있다.")
        void success(final long price) {
            var product = createProduct(상품명, price);
            var response = createProduct(상품명, price);

            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(productRepository.save(any())).willReturn(response);

            Product actual = productService.create(product);

            assertAll(
                    "상품 정보 그룹 Assertions",
                    () -> assertNotNull(actual.getId()),
                    () -> assertEquals(actual.getName(), response.getName()),
                    () -> assertEquals(actual.getPrice(), response.getPrice())
            );
        }

        @Test
        @DisplayName("[실패] 싱픔의 가격은 필수로 입력해야한다.")
        void priceFailTest() {
            var product = new Product();

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1_000L, -10_000L, -1L})
        @DisplayName("[실패] 0원보다 적게 입력하는 경우 등록할 수 없다.")
        void priceFailTest2(final long input) {
            final var product = createProduct(input);

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @Test
        @DisplayName("[실패] 상품 이름을 입력하지 않는 경우 등록할 수 없다.")
        void nameFailTest() {
            final var product = createProductWithoutName();

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }

        @ParameterizedTest
        @ValueSource(strings = {"욕설", "욕설 포함된"})
        @DisplayName("[실패] 상품 이름에 욕설이 포함되어있는 경우 등록할 수 없다.")
        void nameFailTest2(final String name) {
            final var product = createProduct(name);

            assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        }


    }

    @Nested
    @DisplayName("상품 가격 변경 테스트")
    class ChangePrice {

        @ParameterizedTest
        @ValueSource(longs = {오천원, 빵원})
        @DisplayName("상품 가격은 변경할 수 있다.")
        void success(final long changingPrice) {
            final var product = createProduct(만원);
            Menu menu = createMenu(product);

            given(productRepository.findById(product.getId())).willReturn(Optional.ofNullable(product));
            given(menuRepository.findAllByProductId(product.getId())).willReturn(List.of(menu));

            product.setPrice(BigDecimal.valueOf(changingPrice));

            Product response = productService.changePrice(product.getId(), product);

            assertAll(
                    "변경된 상품 정보 그룹 Assertions",
                    () -> assertEquals(response.getId(), product.getId()),
                    () -> assertEquals(response.getPrice(), BigDecimal.valueOf(changingPrice))
            );
        }

        @Test
        @DisplayName(FAIL_PREFIX + "변경 가격을 입력하지 않는 경우 변경할 수 없다.")
        void priceFailTest() {
            final var product = createProduct(만원);

            product.setPrice(null);

            assertThrows(IllegalArgumentException.class, () -> productService.changePrice(product.getId(), product));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -10_000L})
        @DisplayName(FAIL_PREFIX + "0원보다 적게 입력하는 경우 변경할 수 없다.")
        void priceFailTest2(final long changingPrice) {
            final var product = createProduct(만원);

            product.setPrice(BigDecimal.valueOf(changingPrice));

            assertThrows(IllegalArgumentException.class, () -> productService.changePrice(product.getId(), product));
        }

        @Test
        @DisplayName(FAIL_PREFIX + "등록되어 있지 않은 상품 정보인 경우 변경할 수 없다.")
        void notFoundTest() {
            final var product = createProduct(만원);

            assertThrows(NoSuchElementException.class, () -> productService.changePrice(product.getId(), product));
        }

        @Test
        @DisplayName(FAIL_PREFIX + "금액 변경으로 인해 해당 상품이 포함된 메뉴의 가격이 메뉴 구성 전체 상품의 총 금액보다 비싸지는 경우 메뉴가 숨겨진다.")
        void undisplayed() {
            final var product = createProduct(오천원);
            final var menu = createMenu(오천원, product);

            given(productRepository.findById(product.getId())).willReturn(Optional.ofNullable(product));
            given(menuRepository.findAllByProductId(product.getId())).willReturn(List.of(menu));

            product.setPrice(BigDecimal.valueOf(만원));
            productService.changePrice(product.getId(), product);

            assertEquals(menu.isDisplayed(), false);
        }
    }

}
