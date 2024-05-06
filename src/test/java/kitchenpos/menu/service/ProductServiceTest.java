package kitchenpos.menu.service;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.fixture.ProductFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("새로운 상품을 추가 할 수 있다.")
    void create() {
        Product 떡볶이 = ProductFixture.떡볶이;

        Mockito.when(purgomalumClient.containsProfanity(Mockito.any()))
                        .thenReturn(false);
        Mockito.when(productRepository.save(Mockito.any()))
                .thenReturn(떡볶이);

        Product result = productService.create(떡볶이);

        Assertions.assertThat(result.getName()).isEqualTo(떡볶이.getName());
    }
}
