package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp(){
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    void 상품을_등록할_수_있다(){
        final Product product = createProductRequest("후라이드", 20_000L);
        final Product actual = productService.create(product);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 상품의_가격이_올바르지_않으면_예외가_발생한다(){
        final Product product = createProductRequest(-1000L);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @ValueSource(strings = {"욕설이 포함된 단어", "비속어"})
    @ParameterizedTest
    void 상품의_이름에_비속어가_포함되어_있으면_예외가_발생한다(final String name){
        final Product request = createProductRequest(name);
        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(request));
    }

    @DisplayName("상품 가격이 변경될 때 메뉴 가격이 메뉴 상품 금액의 합보다 크면 메뉴가 숨겨진다")
    @Test
    void changePrice(){
        //given
        final UUID productId = UUID.randomUUID();
        final Product product = createProduct(productId, "후라이드", 20_000L);
        productRepository.save(product);

        final var menuId = UUID.randomUUID();
        final Menu menu = createMenu(menuId, 15000L, true, product);
        menuRepository.save(menu);

        final var request = createProductRequest(10000L);

        //when
        productService.changePrice(productId, request);

        //then
        final Menu actual = menuRepository.findById(menuId).get();
        assertThat(actual.isDisplayed()).isFalse();
    }

    @Test
    void 상품의_목록을_조회할_수_있다(){
        productRepository.save(createProduct("후라이드",15000L));
        productRepository.save(createProduct("양념치킨",17000L));
        final List<Product> actual = productService.findAll();
        assertThat(actual).hasSize(2);

    }

    @NotNull
    private static Menu createMenu(UUID menuId, long price, boolean displayed, Product product) {
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }

    private static Product createProductRequest(final String name) {
        return createProductRequest(name,20_000L);
    }

    private static Product createProductRequest() {
        return createProductRequest(20_000L);
    }

    private static Product createProductRequest(final long price) {
        return createProductRequest("후라이드", price);
    }

    private static Product createProductRequest(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private static Product createProduct(final String name, final long price){
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        return createProduct(product.getId(), name, price);
    }

    private static Product createProduct(final UUID id, final String name, final long price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}