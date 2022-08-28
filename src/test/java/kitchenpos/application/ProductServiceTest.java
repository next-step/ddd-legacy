package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.constant.Fixtures;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @DisplayName("제품 등록")
    @Nested
    public class CreateTest {
        @DisplayName("정상 동작")
        @Test
        void create() {
            // given
            given(purgomalumClient.containsProfanity(any())).willReturn(Boolean.FALSE);
            given(productRepository.save(any())).willReturn(any());

            // when
            productService.create(Fixtures.PRODUCT);

            // then
            then(productRepository).should().save(any());
        }

        @DisplayName("가격이 null 이거나 0보다 작을수 없음")
        @ParameterizedTest
        @NullSource
        @ValueSource(longs = -1)
        void createWithInvalidPrice(Long price) {
            // given
            Product product = new Product();
            if (price != null) {
                product.setPrice(BigDecimal.valueOf(price));
            }

            // when then
            assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
        }

        @DisplayName("이름이 null 일 수 없음")
        @Test
        void createWithNullName() {
            // given
            Product product = new Product();
            product.setPrice(BigDecimal.TEN);

            // when then
            assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
        }

        @DisplayName("이름에 욕설이 포함될수 없음")
        @Test
        void createWithProfanityName() {
            // given
            given(purgomalumClient.containsProfanity(any())).willReturn(Boolean.TRUE);

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> productService.create(Fixtures.PRODUCT)
            );
        }
    }

    @DisplayName("제품 가격 수정")
    @Nested
    public class ChangePriceTest {
        @DisplayName("해당 제품이 포함된 메뉴중 하위 제품의 가격 총합이 메뉴 가격보다 작을 경우 메뉴를 숨김 처리")
        @Test
        void changePrice() {
            // given
            UUID productId = UUID.randomUUID();

            Product product = new Product();
            product.setName("SampleProduct");
            product.setPrice(BigDecimal.valueOf(1000));

            given(productRepository.findById(productId)).willReturn(Optional.of(Fixtures.PRODUCT));
            given(menuRepository.findAllByProductId(productId)).willReturn(List.of(Fixtures.MENU));

            // when
            Product result = productService.changePrice(productId, product);

            // then
            assertThat(result.getPrice()).isEqualTo("1000");
            assertThat(Fixtures.MENU.isDisplayed()).isFalse();
        }

        @DisplayName("가격이 null 이거나 0보다 작을수 없음")
        @ParameterizedTest
        @NullSource
        @ValueSource(longs = -1)
        void changePriceWithInvalidPrice(Long price) {
            // given
            Product product = new Product();
            if (price != null) {
                product.setPrice(BigDecimal.valueOf(price));
            }

            // when then
            assertThatIllegalArgumentException().isThrownBy(
                () -> productService.changePrice(UUID.randomUUID(), product)
            );
        }
    }

    @DisplayName("모든 제품 조회")
    @Test
    void findAll() {
        // given
        given(productRepository.findAll()).willReturn(List.of(Fixtures.PRODUCT));

        // when
        List<Product> results = productService.findAll();

        // then
        assertThat(results).containsExactly(Fixtures.PRODUCT);
    }
}
