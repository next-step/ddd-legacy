package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.MENU;
import static kitchenpos.fixture.ProductFixture.PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("상품을 생성한다.")
    void createProduct() {
        // given
        Product expected = PRODUCT();
        given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
        given(productRepository.save(any(Product.class))).willReturn(expected);

        // when
        Product actual = productService.create(expected);

        // then
        verify(productRepository, times(1)).save(any());
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"-500", "-1"})
    @DisplayName("상품 가격은 0 이상이어야 한다.")
    void priceMoreThanOrEqualToZero(String price) {
        // given
        Product expected = PRODUCT();
        expected.setPrice(BigDecimal.valueOf(Integer.parseInt(price)));

        // when & then
        AssertionsForClassTypes.assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름이 없거나 욕이 있으면 안 된다.")
    @ParameterizedTest
    @CsvSource(value = {"arse", "bastard", "null"})
    void productNameCannotBeNullOrProfanity(String name) {
        // given
        Product expected = PRODUCT();

        // when
        String value = Objects.isNull(name) ? null : name;
        expected.setName(value);
        given(purgomalumClient.containsProfanity(name)).willReturn(true);

        // then
        assertThatThrownBy(() -> productService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격은 0 이상이어야 한다.")
    void newPriceGreaterOrEqualToZero() {
        // given
        Product expected = PRODUCT();

        // when
        expected.setPrice(new BigDecimal(-1));

        // then
        AssertionsForClassTypes.assertThatThrownBy(() -> productService.changePrice(expected.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 바꾼 경우 값이 동일해야 한다.")
    void changedProductPriceIsSame() {
        // given
        Product expected = PRODUCT();
        UUID productId = expected.getId();
        Menu menu = MENU();
        given(productRepository.findById(productId)).willReturn(Optional.of(expected));
        given(menuRepository.findAllByProductId(productId)).willReturn(List.of(menu));

        // when
        expected.setPrice(new BigDecimal(30000));
        Product actual = productService.changePrice(productId, expected);

        // then
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    @DisplayName("모든 상품을 가져온다.")
    void findAllMenu() {
        // given
        given(productRepository.findAll()).willReturn(List.of(new Product(), new Product()));

        // when
        List<Product> actual = productService.findAll();

        // then
        verify(productRepository, times(1)).findAll();
        assertThat(actual).hasSize(2);
    }
}
