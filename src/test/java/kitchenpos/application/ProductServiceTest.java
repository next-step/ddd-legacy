package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.MockPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.domain.MenuFixture.MenuGroupFixture.한마리메뉴;
import static kitchenpos.domain.MenuFixture.MenuProductFixture.메뉴상품_후라이드;
import static kitchenpos.exception.ProductExceptionMessage.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductServiceTest {

    private final ProductRepository productRepository = new FakeProductRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final PurgomalumClient purgomalumClient = new MockPurgomalumClient();
    private final ProductService service = new ProductService(productRepository, menuRepository, purgomalumClient);

    @DisplayName("상품 생성 성공")
    @Test
    void create_success() {
        Product product = createProduct(BigDecimal.valueOf(15000), "후라이드");

        Product result = service.create(product);

        Product savedProduct = productRepository.findById(result.getId()).get();

        assertThat(savedProduct.getName()).isEqualTo(result.getName());
        assertThat(savedProduct.getPrice()).isEqualTo(result.getPrice());
    }

    @DisplayName("상품 가격이 null 이면 예외를 발생시킨다.")
    @Test
    void create_price_null() {
        Product product = createProduct(null, "비속어후라이드");

        assertThatThrownBy(() -> service.create(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRODUCT_PRICE_MORE_ZERO);
    }

    @DisplayName("상품 가격이 음수면 예외를 발생시킨다.")
    @Test
    void create_price_negative() {
        Product product = createProduct(BigDecimal.valueOf(-1), "비속어후라이드");

        assertThatThrownBy(() -> service.create(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRODUCT_PRICE_MORE_ZERO);
    }

    @DisplayName("상품 이름에 비속어가 포함되면 예외를 발생시킨다.")
    @Test
    void create_name_purgomalum() {
        Product product = createProduct(BigDecimal.valueOf(15000), "비속어후라이드");

        assertThatThrownBy(() -> service.create(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRODUCT_NAME_CONTAINS_PURGOMALUM);
    }

    @DisplayName("상품 가격 변경 성공, 메뉴 노출")
    @Test
    void change_price_success() {
        Product savedProduct = productRepository.save(createProduct(BigDecimal.valueOf(15000), "후라이드"));
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(savedProduct)), true)
        );
        Product request = createProduct(BigDecimal.valueOf(30000), "");

        service.changePrice(savedProduct.getId(), request);

        assertThat(savedMenu.isDisplayed()).isTrue();
    }

    @DisplayName("상품 가격 변경시 상품을 사용중인 메뉴 가격이 메뉴상품 가격합 보다 크면 메뉴를 노출하지 않는다.")
    @Test
    void change_price_display() {
        Product savedProduct = productRepository.save(createProduct(BigDecimal.valueOf(15000), "후라이드"));
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(savedProduct)), true)
        );
        Product request = createProduct(BigDecimal.valueOf(3000), "");

        service.changePrice(savedProduct.getId(), request);

        assertThat(savedMenu.isDisplayed()).isFalse();
    }


    @DisplayName("상품 가격 변경시 요청가격이 null 이면 예외를 발생시킨다.")
    @Test
    void change_price_null() {
        Product request = createProduct(null, "후라이드");

        assertThatThrownBy(() -> service.changePrice(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRODUCT_PRICE_MORE_ZERO);
    }

    @DisplayName("상품 가격 변경시 요청가격이 음수면 예외를 발생시킨다.")
    @Test
    void change_price_negative() {
        Product request = createProduct(BigDecimal.valueOf(-1), "후라이드");

        assertThatThrownBy(() -> service.changePrice(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PRODUCT_PRICE_MORE_ZERO);
    }

    @DisplayName("상품 가격 변경시 상품이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void change_price_not_found_product() {
        productRepository.save(createProduct(BigDecimal.valueOf(15000), "후라이드"));
        Product request = createProduct(BigDecimal.valueOf(15000), "");

        assertThatThrownBy(() -> service.changePrice(UUID.randomUUID(), request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_PRODUCT);
    }

    private Product createProduct(BigDecimal price, String name) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);
        return product;
    }
}
