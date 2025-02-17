package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);

    private final ProductService productService = new ProductService(productRepository,
        menuRepository,
        purgomalumClient);

    @ParameterizedTest
    @DisplayName("상품 등록 시 가격이 없거나 0 보다 작으면 예외를 발생 시킨다.")
    @ValueSource(ints = {-1, -2})
    void create(int price) {
        var request = new Product();
        request.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 0보다 작을 수 없습니다.");
    }

    @Test
    @DisplayName("상품 이름에 비속어가 포함되면 예외를 발생 시킨다.")
    void createWithBadName() {

        var request = new Product();
        request.setPrice(BigDecimal.valueOf(10_000));
        request.setName("욕설");

        when(purgomalumClient.containsProfanity("욕설")).thenReturn(Boolean.TRUE);

        assertThatThrownBy(
            () -> productService.create(request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("비속어는 사용할 수 없습니다.");
    }

    @Test
    @DisplayName("가격 수정 시 0보다 작은 값을 입력하면 예외를 발생 시킨다.")
    void changePrice() {
        var request = new Product();
        request.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(
            () -> productService.changePrice(UUID.randomUUID(), request)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 0보다 작을 수 없습니다.");
    }
}