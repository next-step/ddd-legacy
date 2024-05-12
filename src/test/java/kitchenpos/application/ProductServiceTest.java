package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @ParameterizedTest
    @DisplayName("상품의 가격은 null 이나 음수일 수 없다")
    @MethodSource("nullAndNegativePrice")
    void nullAndNegativePrice(BigDecimal price) {
        Product request = new Product();
        request.setPrice(price);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(request));
    }

    @Test
    @DisplayName("상품의 이름은 null 일 수 없다")
    void nullName() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);
        request.setName(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(request));
    }

    @Test
    @DisplayName("상품의 이름은 비속어 일 수 없다")
    void profanityName() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);
        request.setName("비속어");

        when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(request));
    }

    @Test
    @DisplayName("상품 등록 정상 케이스")
    void create() {
        Product request = new Product();
        request.setPrice(BigDecimal.ZERO);
        request.setName("상품 이름");

        when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);
        when(productRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));
        Product savedProduct = service.create(request);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(savedProduct.getName()).isEqualTo("상품 이름");
    }

    @Test
    void changePrice() {
    }

    @Test
    void findAll() {
    }

    static Stream<Arguments> nullAndNegativePrice() {
        return Stream.of(
                null,
                Arguments.of(BigDecimal.valueOf(-1))
        );
    }
}
