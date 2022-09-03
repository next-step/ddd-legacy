package kitchenpos.application;

import java.math.BigDecimal;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
}}
