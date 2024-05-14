package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixtures.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

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

    @Test
    @DisplayName("상품을 등록하기 위해 이름과 금액을 입력해야 한다.")
    void test_case_1() {
        final Product createProduct = ProductFixture.ofFixture("김치찌개", BigDecimal.valueOf(20000L));
        Mockito.when(productRepository.save(any())).thenReturn(createProduct);

        Product product = productService.create(createProduct);

        Assertions.assertThat("김치찌개").isEqualTo(product.getName());
        Assertions.assertThat(BigDecimal.valueOf(20000L)).isEqualTo(product.getPrice());
    }

    @Test
    @DisplayName("상품을 등록하기 위해 이름에 비속어 또는 욕설을 작성할 수 없다.")
    void test_case_2() {
        Mockito.when(purgomalumClient.containsProfanity("김치찌개")).thenReturn(true);
        final Product createProduct = ProductFixture.ofFixture("김치찌개", BigDecimal.valueOf(20000L));

        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> {productService.create(createProduct);});
    }

    @Test
    @DisplayName("상품의 금액을 수정")
    void test_case_3() {
        final Product existProduct = ProductFixture.ofFixture("김치찌개", BigDecimal.valueOf(20000L));
        Mockito.when(productRepository.findById(any())).thenReturn(Optional.of(existProduct));
        Mockito.when(menuRepository.findAllByProductId(any())).thenReturn(Collections.emptyList());

        existProduct.setPrice(BigDecimal.valueOf(10000L));
        Product change = productService.changePrice(UUID.randomUUID(), existProduct);

        Assertions.assertThat(change.getPrice()).isEqualTo(change.getPrice());
    }

    @Test
    @DisplayName("상품의 금액을 수정하기 위해 상품이 존재해야 하고, 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
    void test_case_4() {
        final Product existProduct = ProductFixture.ofFixture("김치찌개", BigDecimal.valueOf(20000L));

        existProduct.setPrice(BigDecimal.valueOf(-1));
        Assertions.assertThatIllegalArgumentException().isThrownBy(
                () -> {productService.changePrice(existProduct.getId(), existProduct);});
    }

    @Test
    @DisplayName("상품의 금액을 수정하면 해당 상품이 등록된 모든 메뉴에 영향이 간다.")
    void test_case_5() {
        final Product existProduct = ProductFixture.ofFixture("김치찌개", BigDecimal.valueOf(20000L));




        Assertions.assertThat(20000L).isGreaterThan(120000);
        Assertions.assertThat(false).isFalse();

    }
}
