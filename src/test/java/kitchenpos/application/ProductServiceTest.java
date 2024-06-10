package kitchenpos.application;

import static kitchenpos.TestFixture.createMenu;
import static kitchenpos.TestFixture.createMenuProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import kitchenpos.TestFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.menu.TestMenuRepository;
import kitchenpos.fake.product.TestProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServiceTest {

    @Test
    @DisplayName("상품생성 성공 테스트")
    void create_product_success() {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = BigDecimal.ONE;
        String name = "goodName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when
        Product product = productService.create(request);

        // then
        assertAll(
                () -> assertThat(product.getPrice()).isEqualTo(price),
                () -> assertThat(product.getName()).isEqualTo(name),
                () -> assertThat(product.getId()).isNotNull()
        );
    }

    @Test
    @DisplayName("가격은 반드시 존재해야 한다")
    @ValueSource(longs = {-1L})
    void create_product_fail_price1() {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = null;
        String name = "goodName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @ParameterizedTest
    @DisplayName("가격은 0보다 작을 수 없다")
    @ValueSource(longs = {-1L})
    void create_product_fail_price2(long negativePrice) {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = BigDecimal.valueOf(negativePrice);
        String name = "goodName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }


    @ParameterizedTest
    @DisplayName("이름은 반드시 있어야 한다")
    @NullAndEmptySource
    void create_product_fail_name1(String name) {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (n) -> false
        );
        BigDecimal price = BigDecimal.valueOf(10L);
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @ParameterizedTest
    @DisplayName("이름에 비속어가 포함되면 안된다")
    @ValueSource(strings = {"badname"})
    void create_product_fail_name2(String badName) {
        // given
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> true
        );
        BigDecimal price = BigDecimal.valueOf(10L);
        String name = badName;
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
    }

    @Test
    @DisplayName("상품의 가격을 변동했을 때 가격변경된 제품을 포함하는 메뉴의 가격이 메뉴에 포함된 제품들의 가격 * 수량의 합계보다 크다면 메뉴를 숨긴다")
    void create_change_product_price() {
        // given
        MenuRepository testMenuRepository = new TestMenuRepository();
        ProductRepository testProductRepository = new TestProductRepository();
        ProductService productService = new ProductService(
                testProductRepository,
                testMenuRepository,
                (name) -> false
        );
        BigDecimal price = BigDecimal.valueOf(10);
        String name = "testName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);
        Product product = productService.create(request);

        List<MenuProduct> menuProducts = List.of(createMenuProduct(product, 1L));
        Menu menu = createMenu(BigDecimal.valueOf(5), menuProducts);
        testMenuRepository.save(menu);



        // when
        Product changeProductRequest = new Product();
        BigDecimal newPrice = BigDecimal.valueOf(1);
        changeProductRequest.setPrice(newPrice);
        productService.changePrice(product.getId(), changeProductRequest);

        // then
        assertAll(
                () -> assertThat(testProductRepository.findById(product.getId()).get().getPrice()).isEqualTo(newPrice),
                () -> assertFalse(testMenuRepository.findById(menu.getId()).get().isDisplayed())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1L})
    @DisplayName("가격은 존재하며 0이상이다")
    void create_change_product_price_fail(Long invalidPrice) {
        // given
        MenuRepository testMenuRepository = new TestMenuRepository();
        ProductRepository testProductRepository = new TestProductRepository();
        ProductService productService = new ProductService(
                testProductRepository,
                testMenuRepository,
                (name) -> false
        );
        BigDecimal price = BigDecimal.valueOf(10);
        String name = "testName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);
        Product product = productService.create(request);


        // when
        Product changeProductRequest = new Product();
        BigDecimal newPrice = invalidPrice == null ? null : BigDecimal.valueOf(invalidPrice);
        changeProductRequest.setPrice(newPrice);
        assertThrows(IllegalArgumentException.class, () ->productService.changePrice(product.getId(), changeProductRequest));
    }

    @Test
    @DisplayName("모든 제품 조회")
    void find_all() {
        ProductService productService = new ProductService(
                new TestProductRepository(),
                new TestMenuRepository(),
                (name) -> false
        );
        BigDecimal price = BigDecimal.valueOf(10);
        String name = "testName";
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);
        Product product = productService.create(request);

        List<Product> products = productService.findAll();
        assertAll(
                () -> assertThat(products.size()).isEqualTo(1),
                () -> assertThat(products.get(0).getId()).isEqualTo(product.getId())
        );
    }
}
