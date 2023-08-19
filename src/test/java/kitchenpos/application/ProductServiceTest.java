package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@DisplayName("상품")
class ProductServiceTest extends ApplicationTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @DisplayName("이름")
    @Nested
    class name {
        @DisplayName("[예외] 상품의 이름은 공백일 수 없다.")
        @ParameterizedTest
        @NullSource
        void name_test_1(String name) {
            //given
            Product product = ProductFixture.create(name);
            //then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);

        }

        @DisplayName("[예외] 상품의 이름은 비속어일 수 없다.")
        @Test
        void name_test_2() {
            //given
            when(purgomalumClient.containsProfanity(any())).thenReturn(true);
            //when
            Product product = ProductFixture.create("떡볶이");
            //then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("가격")
    @Nested
    class price {
        @DisplayName("[예외] 상품의 가격은 공백일 수 없다.")
        @ParameterizedTest
        @NullSource
        void price_test_1(BigDecimal price) {
            //given
            Product product = ProductFixture.create(price);
            //then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);

        }

        @DisplayName("[예외] 상품의 가격은 0원 이상이다.")
        @ParameterizedTest
        @ValueSource(longs = {-1, -2})
        void price_test_2(long price) {
            //given
            Product product = ProductFixture.create(BigDecimal.valueOf(price));
            //then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);

        }
    }

    @DisplayName("[성공] 상품을 등록한다.")
    @Test
    void create_test_1() {
        //given
        Product product = ProductFixture.create("떡볶이", BigDecimal.valueOf(1000));
        when(productRepository.save(any())).thenReturn(product);
        //when
        Product created = productService.create(product);
        //then
        assertThat(created).isEqualTo(product);
    }

    @DisplayName("가격 바꾸기")
    @Nested
    class price_change {
        @DisplayName("[성공] 상품 가격을 바꾼다.")
        @Test
        void price_change_test_1() {
            //given
            Product product = ProductFixture.create("떡볶이", BigDecimal.valueOf(1000));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));
            //when
            product.setPrice(BigDecimal.valueOf(3000));
            Product changed = productService.changePrice(product.getId(), product);
            //then
            assertThat(changed.getPrice()).isEqualTo(product.getPrice());
        }

        @DisplayName("[예외] 상품 가격은 0원 이상이어야 한다.")
        @Test
        void price_change_test_2() {
            //given
            Product product = ProductFixture.create("떡볶이", BigDecimal.valueOf(1000));
            //when
            product.setPrice(BigDecimal.valueOf(-1));
            //then
            assertThatThrownBy(() -> productService.changePrice(product.getId(), product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        /**
         * given 상품 2개를 모은 메뉴를 만든다
         * when 상품 1개의 가격을 올린다.
         * then 메뉴가 숨겨진다.
         */
        @DisplayName("[성공] 상품 가격을 바꿀 때 메뉴가격이 구성상품 가격의 합보다 작으면 숨긴다. ")
        @Test
        void price_change_test_3() {
            //given
            Product product1 = ProductFixture.create(BigDecimal.valueOf(1000));
            Product product2 = ProductFixture.create(BigDecimal.valueOf(1000));
            Menu menu = MenuFixture.create(
                    BigDecimal.valueOf(2000)
                    , MenuProductFixture.createDefaulsWithProduct(product1, product2));

            when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
            when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));
            assertThat(menu.isDisplayed()).isTrue();
            //when
            product1.setPrice(BigDecimal.valueOf(1500));
            productService.changePrice(product1.getId(), product1);
            //then
            assertThat(menu.isDisplayed()).isFalse();
        }

    }
}
