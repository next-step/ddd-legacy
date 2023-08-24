package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.TEST_PRODUCT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @Test
    @DisplayName("상품을 신규로 등록한다.")
    void createTest() {
        // given
        Product createRequest = TEST_PRODUCT();
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(productRepository.save(any(Product.class))).willReturn(createRequest);

        // when
        productService.create(createRequest);

        // then
        verify(purgomalumClient, times(1)).containsProfanity(anyString());
        verify(productRepository, times(1)).save(any());
    }

    @DisplayName("상품의 이름이 널값이면 안된다.")
    @Test
    void createNameEmptyTest() {
        // given
        Product nameTest1 = TEST_PRODUCT();
        nameTest1.setName(null);
        Product nameTest2 = TEST_PRODUCT();
        nameTest2.setName(" ");
        Product nameTest3 = TEST_PRODUCT();
        nameTest3.setName("");

        // when && then
        assertThatThrownBy(() -> productService.create(nameTest1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatNoException().isThrownBy(() -> productService.create(nameTest2));
        assertThatNoException().isThrownBy(() -> productService.create(nameTest3));
    }

    @DisplayName("상품의 이름은 부적절한 영어 이름이면 안된다.")
    @ParameterizedTest
    @CsvSource(value = {"fuck", "shit"})
    void createNameTest(String name) {
        // given
        Product createRequest = TEST_PRODUCT();
        createRequest.setName(name);
        given(purgomalumClient.containsProfanity(name)).willReturn(true);

        // when && then
        assertThatThrownBy(() -> productService.create(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 변경한다")
    void changePriceTest() {
        // given
        Product product = TEST_PRODUCT();
        Product changeProduct = TEST_PRODUCT();
        changeProduct.setPrice(new BigDecimal(900));
        UUID productId = product.getId();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(productId)).willReturn(List.of());

        // when
        Product result = productService.changePrice(any(UUID.class), changeProduct);

        // then
        verify(productRepository, times(1)).findById(productId);
        verify(menuRepository, times(1)).findAllByProductId(productId);
        assertThat(result.getPrice()).isEqualTo(changeProduct.getPrice());
    }
    //TODO: 연관된 메뉴가격 관련 테스트 필요!
}