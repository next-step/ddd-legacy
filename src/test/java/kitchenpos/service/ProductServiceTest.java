package kitchenpos.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private Product 강정치킨;
    private Menu 오늘의치킨;

    @BeforeEach
    void init() {
        강정치킨 = ProductFixture.Data.강정치킨();
        productRepository.save(강정치킨);

        MenuGroup 추천메뉴 = MenuGroupFixture.builder()
                .name("추천 메뉴")
                .build();
        menuGroupRepository.save(추천메뉴);

        오늘의치킨 = MenuFixture.builder()
                .name("오늘의 치킨")
                .price(new BigDecimal(1000))
                .menuGroup(추천메뉴)
                .menuProduct(
                        MenuProductFixture.create()
                                .product(강정치킨)
                                .quantity(1)
                                .build()
                )
                .displayed(true)
                .build();
        menuRepository.save(오늘의치킨);
    }

    @Test
    void 상품_생성_실패__가격이_null() {
        Product product = ProductFixture.builder()
                .price(null)
                .build();

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_실패__가격이_음수() {
        Product product = ProductFixture.builder()
                .price(new BigDecimal(-1))
                .build();

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_실패__이름이_null() {
        Product product = ProductFixture.builder()
                .price(new BigDecimal(0))
                .name(null)
                .build();

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_실패__이름에_욕설_포함() {
        Product product = ProductFixture.builder()
                .price(new BigDecimal(0))
                .name("fuck")
                .build();

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격_변경_실패__가격이_null() {
        UUID productId = 강정치킨.getId();
        Product product = new Product();
        product.setPrice(null);

        assertThatThrownBy(() -> productService.changePrice(productId, product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격_변경_실패__가격이_음수() {
        UUID productId = 강정치킨.getId();
        Product request = new Product();
        request.setPrice(new BigDecimal(-1));

        assertThatThrownBy(() -> productService.changePrice(productId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_가격_변경_실패__상품이_존재하지_않음() {
        UUID productId = UUID.randomUUID();
        Product request = new Product();
        request.setPrice(new BigDecimal(0));

        assertThatThrownBy(() -> productService.changePrice(productId, request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 상품_가격_변경_성공__가격변경으로_인해_기존_메뉴의_가격이_메뉴상품의_가격총합보다_커져서_메뉴가_숨겨짐() {
        UUID productId = 강정치킨.getId();
        Product request = new Product();
        request.setPrice(new BigDecimal(999));

        assertDoesNotThrow(() -> productService.changePrice(productId, request));
        Menu menuOfProduct = menuRepository.findById(오늘의치킨.getId()).get();
        assertThat(menuOfProduct.isDisplayed()).isFalse();
    }
}
