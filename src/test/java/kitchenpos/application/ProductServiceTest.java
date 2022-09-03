package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService testService;

    @DisplayName("상품 등록")
    @Nested
    class Create {
        @DisplayName("가격은 음수가 아니어야 한다.")
        @Test
        void negativePrice() {
            // given
            final var request = new Product();
            request.setPrice(new BigDecimal(-1));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름은 비어 있지 않아야 한다.")
        @NullSource
        @ParameterizedTest
        void nullName(String name) {
            // given
            final var request = new Product();
            request.setPrice(new BigDecimal(1000));
            request.setName(name);

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("이름에 비속어가 포함되지 않아야 한다.")
        @Test
        void nameContainsProfanity() {
            // given
            final var request = new Product();
            request.setPrice(new BigDecimal(1000));
            request.setName("심한말");

            when(purgomalumClient.containsProfanity("심한말")).thenReturn(true);

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품을 등록할 수 있다.")
        @Test
        void create() {
            // given
            final var request = new Product();
            request.setPrice(new BigDecimal(1000));
            request.setName("상품1");

            when(purgomalumClient.containsProfanity("상품1")).thenReturn(false);
            //// 서비스에서 생성한 상품 객체를 그대로 반환
            when(productRepository.save(any())).thenAnswer((invocationOnMock) -> invocationOnMock.getArgument(0));

            // when
            final var result = testService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getPrice()).isEqualTo(new BigDecimal(1000)),
                    () -> assertThat(result.getName()).isEqualTo("상품1"),
                    () -> assertThat(result.getId()).isNotNull()
            );
        }
    }

    @DisplayName("상품 가격 변경")
    @Nested
    class ChangePrice {
        private final UUID existingProductId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        @DisplayName("변경하려는 가격은 음수가 아니어야 한다.")
        @Test
        void illegalTargetPrice() {
            // given
            final var targetPrice = new BigDecimal(-1000);
            final var request = new Product();
            request.setPrice(targetPrice);

            // when
            assertThatThrownBy(() -> testService.changePrice(existingProductId, request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품의 가격을 바꿀 수 있다.")
        @Test
        void changePrice() {
            // given
            //// 기존 상품
            final var existingProduct = new Product();
            existingProduct.setId(existingProductId);
            existingProduct.setName("연어");
            existingProduct.setPrice(new BigDecimal(1000));
            when(productRepository.findById(existingProductId)).thenReturn(Optional.of(existingProduct));

            //// 요청
            final var targetPrice = new BigDecimal(8000);
            final var request = new Product();
            request.setPrice(targetPrice);

            // when
            final var result = testService.changePrice(existingProductId, request);

            // then
            assertThat(result.getPrice()).isEqualTo(new BigDecimal(8000));
        }

        @DisplayName("상품이 속한 메뉴의 총 가격이 상품 가격 합보다 커지면, 그 메뉴는 비공개로 변경된다.")
        @ParameterizedTest(name = "메뉴 가격 = {0} 변경하려는 상품 가격 = {1}, 메뉴 공개 여부 = {2}")
        @CsvSource({
                "1000, 500, false",
                "1000, 2000, true"
        })
        void changePrice(
                BigDecimal menuPrice,
                BigDecimal targetProductPrice,
                boolean expectedDisplayed
        ) {
            // given
            //// 기존 상품
            final var existingProduct = new Product();
            existingProduct.setId(existingProductId);
            existingProduct.setName("연어");
            existingProduct.setPrice(new BigDecimal(1000));
            when(productRepository.findById(existingProductId)).thenReturn(Optional.of(existingProduct));

            //// 기존 메뉴
            final var menuProduct = new MenuProduct();
            menuProduct.setProduct(existingProduct);
            menuProduct.setQuantity(1);
            final var menu = new Menu();
            menu.setDisplayed(true);
            menu.setPrice(menuPrice);
            menu.setMenuProducts(List.of(menuProduct));
            when(menuRepository.findAllByProductId(existingProductId)).thenReturn(List.of(menu));

            //// 요쳥
            final var request = new Product();
            request.setPrice(targetProductPrice);

            // when
            testService.changePrice(existingProductId, request);
            final var actualDisplayed = menu.isDisplayed();

            // then
            assertThat(actualDisplayed).isEqualTo(expectedDisplayed);
        }
    }
}
