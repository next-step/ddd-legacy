package kitchenpos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kitchenpos.application.ProductService;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private Product chicken;

    private Menu chickenMenu;

    @BeforeEach
    void setUp() {
        chicken = ProductFixture.init().create();
        chickenMenu = MenuFixture.init().create();
    }

    @Nested
    @DisplayName("상품 등록")
    class 상품_등록 {

        @Test
        @DisplayName("상품 등록 성공")
        void 상품등록_성공() {
            when(productRepository.save(Mockito.any(Product.class))).thenReturn(chicken);

            var result = productService.create(chicken);

            assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getName(), chicken.getName()),
                () -> assertEquals(result.getPrice(), chicken.getPrice())
            );

        }

        @DisplayName("상품명을 반드시 가지며 비속어를 포함하면 안된다.")
        @ParameterizedTest
        @ValueSource(strings = {"나쁜", "XXX"})
        void 상품명_비속어_검사(final String name) {

            when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

            chicken = ProductFixture.test(name, null).create();
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(chicken));
        }

        @DisplayName("상품가격은 0원 이상이어야 한다.")
        @ParameterizedTest
        @ValueSource(ints = {-10000, 0, 10000})
        void 상품가격_허용범위_검사(final int price) {
            chicken = ProductFixture.test(null, new BigDecimal(price)).create();

            if (price < 0) {
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.create(chicken));
            }
        }

    }

    @Nested
    @DisplayName("상품 수정")
    class 상품_수정 {

        @DisplayName("상품가격이 0원 이상이어야 한다.")
        @ParameterizedTest
        @ValueSource(ints = {-10000, 0, 10000})
        void 상품가격_허용범위_검사(final int price) {
            chicken = ProductFixture.test(null, new BigDecimal(price)).create();

            if (price < 0) {
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.changePrice(chicken.getId(), chicken));
            }
        }

        @DisplayName("메뉴의 가격이 메뉴 구성 상품들의 총 금액보다 크면 해당 메뉴는 숨겨진다.")
        @ParameterizedTest
        @CsvSource({"100000, 100"})
        void 가격비교_숨김처리(final int price1, final int price2) {
            chicken = ProductFixture.test(null, new BigDecimal(price1)).create();

            when(productRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(chicken));

            chickenMenu = MenuFixture.test(
                null,
                null,
                null,
                true,
                List.of(new MenuProductFixture(
                    new ProductFixture(
                        null,
                        null,
                        new BigDecimal(price2)
                    ).create(),
                    100
                ).create())
            ).create();

            when(menuRepository.findAllByProductId(Mockito.any()))
                .thenReturn(List.of(chickenMenu));

            productService.changePrice(chicken.getId(), chicken);

            assertThat(chickenMenu.isDisplayed()).isFalse();
        }

    }

    @Nested
    @DisplayName("상품 조회")
    class 상품_조회 {

        @Test
        @DisplayName("상품 조회시 특정 조건 없이 상품의 모든 목록을 조회할 수 있다.")
        void 상품목록_조회() {
            when(productRepository.findAll()).thenReturn(List.of(chicken));
            List<Product> result = productService.findAll();

            assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertEquals(result.size(), 1)
            );
        }
    }

}