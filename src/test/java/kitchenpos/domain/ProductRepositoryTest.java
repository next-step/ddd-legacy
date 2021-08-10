package kitchenpos.domain;

import kitchenpos.DummyData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest extends DummyData {

    @Mock
    private ProductRepository productRepository;

    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @DisplayName("상품 생성")
    @Test
    void createProduct() {
        Product product = products.get(0);

        given(productRepository.save(product)).willReturn(product);

        Product create = productRepository.save(product);

        assertAll(
            () -> assertThat(product.getId()).isEqualTo(create.getId()),
            () -> assertThat(product.getPrice()).isEqualTo(create.getPrice()),
            () -> assertThat(product.getName()).isEqualTo(create.getName())
        );
    }

    @DisplayName("상품 가격 변경")
    @Test
    void changeProductPrice() {
        BigDecimal changePrice = BigDecimal.valueOf(10000);

        Product product = products.get(0);

        given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));

        Product findProduct = productRepository.findById(PRODUCT_ID).get();
        findProduct.setPrice(changePrice);

        given(productRepository.save(findProduct)).willReturn(findProduct);

        Product changeProduct = productRepository.save(findProduct);

        assertAll(
            () -> assertThat(changeProduct.getPrice()).isEqualTo(findProduct.getPrice()),
            () -> assertThat(changeProduct.getId()).isEqualTo(findProduct.getId())
        );
    }

    @DisplayName("상품 내역 확인")
    @Test
    void findAll() {
        given(productRepository.findAll()).willReturn(products);

        List<Product> findAll = productRepository.findAll();

        verify(productRepository).findAll();
        verify(productRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(products.containsAll(findAll)).isTrue(),
                () -> assertThat(products.size()).isEqualTo(findAll.size())
        );
    }
}