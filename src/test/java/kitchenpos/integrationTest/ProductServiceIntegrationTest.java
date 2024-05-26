package kitchenpos.integrationTest;

import kitchenpos.application.ProductService;
import kitchenpos.domain.*;
import kitchenpos.fixtures.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class ProductServiceIntegrationTest {
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    void 상품을_등록_할_수_있다() {
        final Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        final Product actual = productService.create(product);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 가격이_음수면_상품_등록_실패() {
        final Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(-16000L));
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름에_비속어가_포함되면_상품_등록_실패() {
        final Product product = ProductFixture.create("비속어", BigDecimal.valueOf(16000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 메뉴에_존재하지않는_상품의_가격을_변경할_수_있다() {
        final Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        final Product savedProduct = productService.create(product);

        final Product updateRequest = new Product();
        updateRequest.setPrice(BigDecimal.valueOf(18000L));

        final Product updatedProduct = productService.changePrice(savedProduct.getId(), updateRequest);
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(18000L));
    }


    @Test
    void 메뉴에_존재하는_상품의_가격을_메뉴가격보다_크게_변경함() {
        final Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        final Product savedProduct = productService.create(product);
        final Menu menu = MenuFixture.create("후라이드치킨 단품", BigDecimal.valueOf(16000L), true, MenuGroupFixture.create("치킨"),
                Collections.singletonList(
                        MenuProductFixture.create(savedProduct, 1, savedProduct.getId())));
        menuRepository.save(menu);

        final Product updateRequest = new Product();
        updateRequest.setPrice(BigDecimal.valueOf(18000L));

        final Product updatedProduct = productService.changePrice(savedProduct.getId(), updateRequest);
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(18000L));
    }

    @Test
    void 메뉴에_존재하는_상품의_가격을_메뉴가격보다_작게_변경하면_메뉴가_숨겨짐() {
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        final Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        final Product savedProduct = productService.create(product);
        final Menu menu = MenuFixture.create("후라이드치킨 단품", BigDecimal.valueOf(16000L), true, MenuGroupFixture.create("치킨"),
                Collections.singletonList(
                        MenuProductFixture.create(savedProduct, 1, savedProduct.getId())));
        menuRepository.save(menu);

        final Product updateRequest = new Product();
        updateRequest.setPrice(BigDecimal.valueOf(15000L));

        final Product updatedProduct = productService.changePrice(savedProduct.getId(), updateRequest);
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(15000L));

        final Menu updatedMenu = menuRepository.findById(menu.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(updatedMenu.isDisplayed()).isEqualTo(false);
    }


    @Test
    void 존재하지_않는_상품의_가격을_변경_실패() {
        final Product updateRequest = new Product();
        updateRequest.setPrice(BigDecimal.valueOf(18000L));
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), updateRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 가격이_음수면_상품_가격_변경_실패() {
        final Product product = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        final Product savedProduct = productService.create(product);

        final Product updateRequest = new Product();
        updateRequest.setPrice(BigDecimal.valueOf(-18000L));

        assertThatThrownBy(() -> productService.changePrice(savedProduct.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모든_상품을_조회할_수_있다() {
        final Product product1 = ProductFixture.create("후라이드치킨", BigDecimal.valueOf(16000L));
        final Product product2 = ProductFixture.create("양념치킨", BigDecimal.valueOf(17000L));
        given(purgomalumClient.containsProfanity(any())).willReturn(false);
        productService.create(product1);
        productService.create(product2);

        final List<Product> products = productService.findAll();
        assertThat(products).hasSize(2);
    }
}
