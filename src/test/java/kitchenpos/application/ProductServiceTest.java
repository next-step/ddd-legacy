package kitchenpos.application;

import fixtures.MenuBuilder;
import fixtures.MenuGroupBuilder;
import fixtures.MenuProductBuilder;
import fixtures.ProductBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {


    @Autowired
    private ProductService productService;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuRepository menuRepository;


    @DisplayName("상품을 생성한다")
    @Test
    void createProductTest() {

        // given
        Product product = new ProductBuilder().anProduct().build();

        // when
        Product savedProduct = productService.create(product);

        // then
        assertNotNull(savedProduct.getId());
        assertEquals(product.getName(), savedProduct.getName());
        assertEquals(product.getPrice(), savedProduct.getPrice());
    }

    @DisplayName("상품 생성 시 이름은 공백을 허용한다")
    @EmptySource
    @ParameterizedTest
    void productNameWithBlankTest(String name) {

        // given
        Product product = new ProductBuilder().anProduct().with(name, BigDecimal.ONE).build();

        // when
        Product savedProduct = productService.create(product);

        // then
        assertNotNull(savedProduct.getId());
        assertEquals(product.getName(), savedProduct.getName());
        assertEquals(product.getPrice(), savedProduct.getPrice());
    }

    @DisplayName("상품 생성 시 가격은 0 이상이어야 한다")
    @Test
    void productPriceIsZeroOrPositiveTest() {

        // given
        Product created = new ProductBuilder().anProduct().with("후라이드치킨", BigDecimal.ZERO).build();

        // when
        Product sut = productService.create(created);

        // then
        assertNotNull(sut.getId());
    }


    @DisplayName("상품의 가격을 변경한다")
    @Test
    void changePriceTest() {

        Product product = new ProductBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .build();

        Product created = productService.create(product);

        // when
        created.setPrice(BigDecimal.valueOf(20_000));

        // then
        Product sut = productService.changePrice(created.getId(), created);
        assertThat(sut.getPrice()).isEqualTo(BigDecimal.valueOf(20_000));
    }

    @DisplayName("변경한 상품 가격으로 계산한 메뉴상품 가격보다 메뉴 가격이 크면 메뉴가 숨김처리 된다")
    @Test
    void changeProductPriceTest() {


        Product product = new ProductBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .build();
        Product created = productService.create(product);
        Menu menu = createMenu(created);

        // when
        created.setPrice(BigDecimal.TEN);
        productService.changePrice(created.getId(), created);

        // then
        Menu sut = menuRepository.findById(menu.getId()).get();
        assertThat(sut.isDisplayed()).isFalse();
    }

    private Menu createMenu(Product product) {

        MenuProduct menuProduct = new MenuProductBuilder()
                .withProduct(product)
                .withQuantity(1)
                .build();

        MenuGroup menuGroup = menuGroupRepository.save(new MenuGroupBuilder().withName("한 마리 메뉴").build());
        return menuRepository.save(new MenuBuilder()
                .with("치킨", BigDecimal.valueOf(10_000))
                .withMenuGroup(menuGroup)
                .withMenuProducts(List.of(menuProduct))
                .build());
    }
}
