package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductRepository productRepository =  new InMemoryProductRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private ProductService productService;

    @BeforeEach
    void setUp(){
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    public void create() {

        //given
        Product request = ProductTestFixture.createProductRequest("후라이드치킨", 20000L);
        Product product = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 20000L);

        //when
        Product response = productService.create(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getName(), response.getName());

    }

    @Test
    public void changePrice() {

        //given
        UUID productId = UUID.randomUUID();
        Product product = ProductTestFixture.createProduct(productId, "양념치킨",22000L);
        productRepository.save(product);

        UUID menuId = UUID.randomUUID();
        final Menu menu = MenuTestFixture.createMenu(menuId, "양념치킨", 22000L, true, product);
        menuRepository.save(menu);
        Product request = ProductTestFixture.createProductRequest(23000L);

        //when
        productService.changePrice(product.getId(), request);

        //then
        Product responseProduct = productRepository.findById(product.getId()).get();
        assertThat(responseProduct.getPrice()).isEqualTo(request.getPrice());

        Menu response = menuRepository.findById(menuId).get();
        assertThat(response.isDisplayed()).isTrue();

    }

    @Test
    public void findAll() {

        //given
        Product product1 = ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L);
        Product product2 = ProductTestFixture.createProduct(UUID.randomUUID(), "양념치킨", 18000L);
        productRepository.save(product1);
        productRepository.save(product2);

        //when
        List<Product> response = productService.findAll();

        //then
        assertEquals(2, response.size());
        assertThat(response
                .stream().anyMatch(res -> res.getName().equals(product1.getName())))
                .isTrue();
        assertThat(response
                .stream().anyMatch(res -> res.getName().equals(product2.getName())))
                .isTrue();

    }

}