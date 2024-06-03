package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @InjectMocks
    private ProductService productService;

    @DisplayName("ProductService.create 메서드 테스트")
    @Nested
    class create {
        @DisplayName("상품을 등록할 수 있다.")
        @Test
        void create() {
            // given
            Product request = ProductFixture.createProductRequest(BigDecimal.valueOf(10_000), "후라이드 치킨");
            Product product = ProductFixture.createProduct(BigDecimal.valueOf(10_000), "후라이드 치킨");
            given(productRepository.save(any())).willReturn(product);

            // when
            Product actual = productService.create(request);

            // then
            assertThat(actual.getId()).isNotNull();
        }

        @DisplayName("상품의 가격으로 NULL 또는 0보다 작으면 상품을 등록할 수 없습니다.(IllegalArgumentException)")
        @NullSource
        @ValueSource(strings = {"-1", "-100"})
        @ParameterizedTest
        void price_NULLorNegative(final BigDecimal price) {
            // given
            Product request = ProductFixture.createProductRequest(price, "후라이드 치킨");

            // when then
            assertThatThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품의 이름으로 Null 값을 사용할 수 없습니다.(IllegalArgumentException)")
        @NullSource
        @ParameterizedTest
        void name_NULL(final String name) {
            // given
            Product request = ProductFixture.createProductRequest(BigDecimal.valueOf(10_000), name);

            // when then
            assertThatThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품의 이름에 비속어가 포함될 수 없습니다.(IllegalArgumentException)")
        @ValueSource(strings = {"비속어", "나쁜말"})
        @ParameterizedTest
        void name_Profanity(final String name) {
            // given
            Product request = ProductFixture.createProductRequest(BigDecimal.valueOf(10_000), name);
            given(purgomalumClient.containsProfanity(name)).willReturn(true);

            // when then
            assertThatThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("")
    @Nested
    class changePrice {
        @DisplayName("상품의 가격을 변경시킬 수 있다.")
        @Test
        void changePrice() {
            Product request = ProductFixture.createProductRequest(BigDecimal.valueOf(10_000), "후라이드 치킨");
            Product product = ProductFixture.createProduct(BigDecimal.valueOf(11_000), "후라이드 치킨");
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 2L);
            Menu menu = MenuFixture.createMenuRequest(menuProduct, null, 20_000L, "메뉴", true);
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            Product changedProduct = productService.changePrice(product.getId(), request);

            // then
            assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(10_000));
        }

        @DisplayName("상품 가격 변경 값함에 따라 메뉴 값이 더 커지면 메뉴를 비노출 시킨다.")
        @Test
        void changePrice_Display_false() {
            // given
            Product request = ProductFixture.createProductRequest(BigDecimal.valueOf(9_000), "후라이드 치킨");
            Product product = ProductFixture.createProduct(BigDecimal.valueOf(11_000), "후라이드 치킨");
            given(productRepository.findById(any())).willReturn(Optional.of(product));

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 2L);
            Menu menu = MenuFixture.createMenuRequest(menuProduct, null, 20_000L, "메뉴", true);
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            Product changedProduct = productService.changePrice(product.getId(), request);

            // then
            assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(9_000));
            assertThat(menu.isDisplayed()).isFalse();
        }

        @DisplayName("변경 값이 NULL 값이거나 음수인 경우 예외가 발생한다.")
        @NullSource
        @ValueSource(strings = {"-1", "-100"})
        @ParameterizedTest
        void changePrice_NULLorNegative(BigDecimal price) {
            // given
            Product request = ProductFixture.createProductRequest(price, "후라이드 치킨");

            // when then
            assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("상품 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        Product product1 = ProductFixture.createProduct(BigDecimal.valueOf(11_000), "상품1");
        Product product2 = ProductFixture.createProduct(BigDecimal.valueOf(12_000), "상품2");
        Product product3 = ProductFixture.createProduct(BigDecimal.valueOf(13_000), "상품3");
        given(productRepository.findAll()).willReturn(List.of(product1, product2, product3));

        List<Product> products = productService.findAll();

        assertThat(products).hasSize(3)
                .extracting("price", "name")
                .containsExactlyInAnyOrder(
                        tuple(BigDecimal.valueOf(11_000), "상품1"),
                        tuple(BigDecimal.valueOf(12_000), "상품2"),
                        tuple(BigDecimal.valueOf(13_000), "상품3")
                );
    }

}
