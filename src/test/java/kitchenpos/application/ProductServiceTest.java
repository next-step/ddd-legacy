package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    public void create() {

        //given
        Product request = new Product();
        request.setName("양념치킨");
        request.setPrice(BigDecimal.valueOf(20000));

        Product product = new Product();
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(20000));
        product.setId(UUID.randomUUID());

        given(purgomalumClient.containsProfanity(any()))
                .willReturn(false);


        given(productRepository.save(any(Product.class)))
                .willReturn(product);

        //when
        Product response = productService.create(request);

        //then
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getName(), response.getName());

    }

    @Test
    public void changePrice() {

        //given
        Product request = new Product();
        request.setId(UUID.randomUUID());
        request.setPrice(BigDecimal.valueOf(30000));

        given(productRepository.findById(request.getId()))
                .willReturn(Optional.of(request));

        given(menuRepository.findAllByProductId(request.getId()))
                .willReturn(anyList());

        //when
        Product response = productService.changePrice(request.getId(), request);

        //then
        assertEquals(BigDecimal.valueOf(30000), response.getPrice());

    }

    @Test
    public void findAll() {

        //given
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(20000));
        product1.setName("후라이드치킨");

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setPrice(BigDecimal.valueOf(20000));
        product2.setName("양념치킨");

        given(productRepository.findAll())
                .willReturn(Arrays.asList(product1, product2));

        //when
        List<Product> response = productService.findAll();


        //then
        assertEquals(2, response.size());
        assertEquals(Arrays.asList(product1, product2), response);


    }

}