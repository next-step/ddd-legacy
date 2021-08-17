package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class ProductServiceTest {

    private Product request;

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        request = new Product();
        request.setId(UUID.randomUUID());
        request.setName("김치볶음밥");
        request.setPrice(BigDecimal.valueOf(7000));
    }

    @DisplayName("상품을 등록한다.")
    @Test
    void productCreateTest() {
        Product product = productService.create(request);
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("김치볶음밥");
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(7000));
    }

    @DisplayName("상품등록시 상품명에 비속어를 사용할 수 없다.")
    @Test
    void 상품명_비속어_등록테스트() {
        request.setName("shit");
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록시 상품가격은 0보다 작을 수 없다.")
    @ValueSource(longs = {-1, -1000})
    @ParameterizedTest
    void 상품가격테스트(long price) {
        request.setPrice(BigDecimal.valueOf(price));
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경한다.")
    @Test
    void changePrice() {
        productRepository.save(request);
        request.setPrice(BigDecimal.valueOf(9_000));
        Product actual = productService.changePrice(request.getId(), request);
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(9_000));
    }

    @DisplayName("상품가격 변경시 상품가격은 0보다 작을수 없다.")
    @ValueSource(longs = {-1, -1000})
    @ParameterizedTest
    void ZERO_보다작은_상품가격_수정_테스트(long price) {
        productRepository.save(request);
        request.setPrice(BigDecimal.valueOf(price));
        assertThatThrownBy(() -> productService.changePrice(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품가격 변경시 해당 상품이 포함된 메뉴의 가격보다 크다면 해당 메뉴는 노출되지 않는다.")
    @Test
    void 메뉴가격보다_비싼_상품가격_수정_테스트() {
        Menu menu = createMenu();

        request.setPrice(BigDecimal.valueOf(5_000));
        productService.changePrice(request.getId(), request);

        Menu actual = menuRepository.findById(menu.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isDisplayed()).isEqualTo(false);
    }

    private Menu createMenu() {
        productRepository.save(request);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("테스트메뉴그룹");
        menuGroupRepository.save(menuGroup);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(request);
        menuProduct.setProductId(request.getId());
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("테스트메뉴");
        menu.setPrice(BigDecimal.valueOf(10_000));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menuRepository.save(menu);
        return menu;
    }

    @Test
    void findAll() {
        List<Product> products = productService.findAll();
        assertThat(products.size()).isEqualTo(25);
    }
}