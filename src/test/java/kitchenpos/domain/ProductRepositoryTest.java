package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @DisplayName("상품 생성")
    @Test
    void createProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setPrice(BigDecimal.valueOf(100));
        product.setName("후라이드");

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

        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setPrice(BigDecimal.valueOf(100));
        product.setName("후라이드");

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
        List<Product> products = new ArrayList<>();

        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setPrice(BigDecimal.valueOf(100));
        product.setName("후라이드");

        Product product2 = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(110));
        product.setName("양념");

        products.add(product);
        products.add(product2);

        given(productRepository.findAll()).willReturn(products);

        List<Product> findAll = productRepository.findAll();

        assertThat(products.containsAll(findAll)).isTrue();
    }
}