package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.repository.MenuFakeRepository;
import kitchenpos.repository.ProductFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductService sut;

    private MenuRepository menuRepository;

    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productRepository = new ProductFakeRepository();
        menuRepository = new MenuFakeRepository();
        sut = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    class 상품_신규_등록 {

        @DisplayName("상품을 신규 등록한다")
        @Test
        void testCreate() {
            // given
            var request = ProductFixture.create();
            given(purgomalumClient.containsProfanity(request.getName())).willReturn(false);

            // when
            Product actual = sut.create(request);

            // then
            Product expected = productRepository.findById(actual.getId()).get();
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("상품명에 욕설이 포함되면 상품을 신규 등록할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"fuck", "dummy", "dork"})
        void testCreateWhenProductNameContainPurg(String productName) {
            // given
            var request = ProductFixture.create(productName);
            given(purgomalumClient.containsProfanity(productName)).willReturn(true);

            // when // then
            assertThatThrownBy(() -> sut.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품명이 null이면 상품을 신규 등록할 수 없다.")
        @Test
        void testCreateWhenProductNameIsNull() {
            // given
            var request = ProductFixture.create((String) null);

            // when // then
            assertThatThrownBy(() -> sut.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품의 가격이 0미만이면 상품을 신규 등록할 수 없다.")
        @ParameterizedTest
        @ValueSource(ints = {-10_000, -1_000})
        void testCreateWhenProductPriceIsNegative(int productPrice) {
            // given
            var request = ProductFixture.create(productPrice);

            // when // then
            assertThatThrownBy(() -> sut.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품의 가격은 null일 수 없다 상품을 신규 등록할 수 없다.")
        @ParameterizedTest
        @NullSource
        void testCreateWhenProductPriceIsNull(BigDecimal productPrice) {
            // given
            var request = ProductFixture.create(productPrice);

            // when // then
            assertThatThrownBy(() -> sut.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class 상품_가격_수정 {

        @DisplayName("상품의 가격을 수정한다")
        @Test
        void testChangePrice() {
            // given
            var request = productRepository.save(ProductFixture.create(10_000));

            // when
            Product actual = sut.changePrice(request.getId(), request);

            // then
            assertThat(actual.getId()).isEqualTo(request.getId());
            assertThat(actual.getPrice()).isEqualTo(request.getPrice());
            assertThat(actual.getName()).isEqualTo(request.getName());
        }

        @DisplayName("상품의 가격을 0미만의 숫자로 수정할 수 없다")
        @ParameterizedTest
        @ValueSource(ints = {-10_000, -1_000})
        void testChangePriceWhenPriceIsNegative(int price) {
            // given
            var request = ProductFixture.create(price);

            // when // then
            assertThatThrownBy(() -> sut.changePrice(request.getId(), request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("존재하지 않는 상품의 가격은 수정할 수 없다")
        @Test
        void testChangePriceWhenProductIdIsNotExist() {
            // given
            var request = ProductFixture.create();

            // when // then
            assertThatThrownBy(() -> sut.changePrice(request.getId(), request))
                .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("상품의 가격을 수정할 때, 상품이 포함된 메뉴 가격이 상품 가격의 합보다 비싸면 메뉴는 숨김처리된다")
        @Test
        void testChangePriceWhenMenuWithChangedProductIsHide() {
            // given
            var product = productRepository.save(ProductFixture.create(10_000));
            Menu menu = menuRepository.save(MenuFixture.create(12_000, List.of(MenuProductFixture.create(product, 1))));

            var request = ProductFixture.create(5_000);
            // when
            Product actual = sut.changePrice(product.getId(), request);

            // then
            Menu findMenu = menuRepository.findById(menu.getId()).get();

            assertThat(actual.getId()).isEqualTo(product.getId());
            assertThat(actual.getPrice()).isEqualTo(product.getPrice());
            assertThat(actual.getName()).isEqualTo(product.getName());
            assertThat(findMenu.isDisplayed()).isFalse();
        }
    }

    @Nested
    class 상품_조회 {

        @DisplayName("모든 상품을 조회한다")
        @Test
        void testFindAll() {
            // given
            productRepository.save(ProductFixture.create("product1"));
            productRepository.save(ProductFixture.create("product2"));
            productRepository.save(ProductFixture.create("product3"));

            // when
            List<Product> actual = sut.findAll();

            // then
            assertThat(actual).hasSize(3);
            assertThat(actual.get(0).getName()).isEqualTo("product1");
            assertThat(actual.get(1).getName()).isEqualTo("product2");
            assertThat(actual.get(2).getName()).isEqualTo("product3");
        }
    }
}
