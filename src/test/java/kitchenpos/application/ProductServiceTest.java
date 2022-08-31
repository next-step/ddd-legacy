package kitchenpos.application;

import static kitchenpos.test.constant.MethodSource.NEGATIVE_NUMBERS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.test.Fixture;
import kitchenpos.test.UnitTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ProductServiceTest extends UnitTestCase {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Fixture.createProduct();
    }

    @DisplayName("제품 등록")
    @Nested
    class CreateTest {

        @DisplayName("제품 이름과 가격으로 등록한다.")
        @Test
        void success() {
            // when then
            assertThatCode(() -> service.create(product))
                    .doesNotThrowAnyException();
        }

        @DisplayName("제품 이름은 비어 있을 수 없다.")
        @Test
        void error1() {
            // given
            Product request = new Product();
            request.setPrice(BigDecimal.valueOf(16_000));
            request.setName(null);

            // when then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.create(request));
        }

        @DisplayName("제품 이름은 비속어를 포함할 수 없다.")
        @Test
        void error2() {
            // given
            given(purgomalumClient.containsProfanity(any()))
                    .willReturn(Boolean.TRUE);

            // when then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.create(product));
        }

        @DisplayName("제품 가격은 0원 이상 입력 가능하다.")
        @ParameterizedTest
        @NullSource
        @MethodSource(NEGATIVE_NUMBERS)
        void error3(BigDecimal actual) {
            // given
            Product request = new Product();
            request.setPrice(actual);

            // when then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.create(request));
        }
    }

    @DisplayName("제품 수정")
    @Nested
    class ChangePriceTest {

        @DisplayName("제품 가격을 수정할 수 있다.")
        @Test
        void success() {
            // given
            UUID id = product.getId();
            BigDecimal requestPrice = BigDecimal.valueOf(1_000);

            Product request = new Product();
            request.setPrice(requestPrice);

            given(productRepository.findById(any()))
                    .willReturn(Optional.ofNullable(product));

            // when then
            assertThat(service.changePrice(id, request))
                    .hasFieldOrPropertyWithValue("price", requestPrice);
        }

        @DisplayName("제품 가격은 0원 이상 입력 가능하다.")
        @ParameterizedTest
        @NullSource
        @MethodSource(NEGATIVE_NUMBERS)
        void error1(BigDecimal actual) {
            // given
            Product request = new Product();
            request.setPrice(actual);

            // when then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> service.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("제품이 등록된 메뉴의 가격이, "
                + "메뉴 제품의 가격 합보다 큰 경우 메뉴는 비활성화 된다.")
        @Test
        void success_disabledMenu() {
            // given
            UUID id = product.getId();
            BigDecimal requestPrice = BigDecimal.valueOf(1_000);
            Product request = new Product();
            request.setPrice(requestPrice);

            given(productRepository.findById(any()))
                    .willReturn(Optional.ofNullable(product));

            // when
            BigDecimal menuProductPrice = Fixture.PRICES_FOR_ALL_PRODUCTS_ON_THE_MENU;
            Menu menu = Fixture.createMenu();
            menu.setPrice(menuProductPrice.add(BigDecimal.ONE));
            given(menuRepository.findAllByProductId(any()))
                    .willReturn(List.of(menu));

            service.changePrice(id, request);

            // then
            assertThat(menu)
                    .hasFieldOrPropertyWithValue("displayed", Boolean.FALSE);
        }
    }

    @DisplayName("등록된 제품을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        given(productRepository.findAll())
                .willReturn(List.of(product));

        // when then
        assertThat(service.findAll())
                .contains(product);
    }
}