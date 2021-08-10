package kitchenpos.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    @DisplayName("상품 생성")
    @Test
    void createProduct() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));
        product.setName("후라이드");

        given(productRepository.save(product)).willReturn(product);

        Product create = productRepository.save(product);

        assertAll(
            () -> assertThat(product.getPrice()).isEqualTo(create.getPrice()),
            () -> assertThat(product.getName()).isEqualTo(create.getName())
        );
    }
}