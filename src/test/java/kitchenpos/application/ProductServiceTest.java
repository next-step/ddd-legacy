package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.spy.SpyProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductService productService;

    @Spy
    private SpyProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void beforeEach() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품 등록")
    @Nested
    class ProductCreateTestGroup {

        @DisplayName("상품 가격이 없으면 예외 발생")
        @Test
        void createTest1() {
            // given
            final Product request = ProductFixture.createProductWithPrice(null);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("상품 가격이 음수 값이면 예외 발생")
        @Test
        void createTest2() {
            // given
            final Product request = ProductFixture.createProductWithPrice(-1);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("상품 이름이 없으면 예외 발생")
        @Test
        void createTest3() {
            // given
            final Product request = ProductFixture.createProductWithName(null);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("상품 이름이 비속어라면 예외 발생")
        @Test
        void createTest4() {
            // given
            final Product request = ProductFixture.createProduct();

            given(purgomalumClient.containsProfanity(any()))
                    .willReturn(true);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("상품이 등록됨")
        @Test
        void createTest5() {
            // given
            final Product request = ProductFixture.createProduct();
            Product product = ProductFixture.createProductWithRequest(request);

            given(purgomalumClient.containsProfanity(any()))
                    .willReturn(false);

            // when
            Product actual = productService.create(request);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getName()).isEqualTo(request.getName());
            assertThat(actual.getPrice()).isEqualTo(request.getPrice());
        }
    }

    @DisplayName("상품의 가격 변경")
    @Nested
    class ChangePriceTestGroup {

        @DisplayName("상품 가격이 없으면 예외 발생")
        @Test
        void changePriceTest1() {
            // given
            final UUID productId = UUID.randomUUID();
            final Product request = ProductFixture.createProductWithPrice(null);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.changePrice(productId, request));
        }

        @DisplayName("상품 가격이 음수 값이면 예외 발생")
        @Test
        void changePriceTest2() {
            // given
            final UUID productId = UUID.randomUUID();
            final Product request = ProductFixture.createProductWithPrice(-1);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.changePrice(productId, request));
        }

        @DisplayName("등록된 상품이 아니면 예외 발생")
        @Test
        void changePriceTest3() {
            // given
            final UUID productId = UUID.randomUUID();
            final Product request = ProductFixture.createProduct();

            given(productRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> productService.changePrice(productId, request));
        }

        @DisplayName("상품의 가격과 수량을 곱한 가격이 메뉴 가격보다 크면 전시 된 메뉴를 숨기고 상품 가격을 변경")
        @Test
        void changePriceTest4() {
            // given
            final UUID productId = UUID.randomUUID();
            final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();
            final Product product = ProductFixture.createProductWithPrice(500);
            final MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 1);
            final Menu menu = MenuFixture.createMenu(menuGroup,
                    "치킨 한마리",
                    500,
                    List.of(menuProduct),
                    true);
            final Product request = ProductFixture.createProductWithPrice(300);

            List<Menu> menus = List.of(menu);

            given(productRepository.findById(any()))
                    .willReturn(Optional.of(product));
            given(menuRepository.findAllByProductId(any()))
                    .willReturn(menus);

            // when
            Product actual = productService.changePrice(productId, request);

            // then
            assertThat(menu.isDisplayed()).isFalse();
            assertThat(actual.getPrice()).isEqualTo(request.getPrice());
        }

        @DisplayName("상품 가격을 변경")
        @Test
        void changePriceTest5() {
            // given
            final UUID productId = UUID.randomUUID();
            final Product request = ProductFixture.createProductWithPrice(1000);
            final Product product = ProductFixture.createProductWithPrice(2000);

            given(productRepository.findById(any()))
                    .willReturn(Optional.of(product));
            given(menuRepository.findAllByProductId(any()))
                    .willReturn(List.of(MenuFixture.createMenu()));

            // when
            Product actual = productService.changePrice(productId, request);

            // then
            assertThat(actual.getPrice()).isEqualTo(new BigDecimal(1000));
        }
    }

    @DisplayName("등록된 상품 목록을 모두 조회")
    @Test
    void findAllTest() {

        // given
        final Product product = ProductFixture.createProductWithPrice(500);

        given(productRepository.findAll())
                .willReturn(List.of(product));

        // when
        List<Product> actual = productService.findAll();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.size()).isOne();
    }
}