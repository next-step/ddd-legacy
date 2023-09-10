package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixure;
import kitchenpos.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest extends ServiceTest {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;

    public ProductServiceTest(final ProductService productService,
                              final ProductRepository productRepository,
                              final MenuRepository menuRepository,
                              final MenuGroupRepository menuGroupRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        final Product product = ProductFixure.create("양념치킨", 1000);

        Product createProduct = productService.create(product);

        assertAll(
                () -> assertThat(product.getName()).isEqualTo(createProduct.getName()),
                () -> assertThat(product.getPrice()).isEqualTo(createProduct.getPrice())
        );
    }

    @DisplayName("상품 등록시, 가격은 0보다 미만이 될 수 없다.")
    @Test
    void createZeroPriceException() {
        final Product product = ProductFixure.create("양념치킨", -1000);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 등록시, 이름에 비속어가 들어갈 수 없다.")
    @Test
    void createPurgomalumClientException() {
        final Product product = ProductFixure.create("bitch", 1000);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        Product product = productRepository.save(ProductFixure.create("후라드이 치킨", 1000));
        product.setPrice(new BigDecimal(2000));

        Product chageProduct = productService.changePrice(product.getId(), product);

        assertThat(chageProduct.getPrice()).isEqualTo(new BigDecimal(2000));
    }

    @DisplayName("가격 변경시, 상품 가격은 0보다 작으면 에러가 발생한다.")
    @Test
    void changePriceZeroPriceException() {
        final Product product = productRepository.save(ProductFixure.create("후라드이 치킨", 1000));
        product.setPrice(new BigDecimal(-1000));

        assertThatThrownBy(() -> productService.changePrice(product.getId(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가격 변동시, 상품의 총합이 메뉴의 가격보다 작을경우, 메뉴을 비노출 시킨다.")
    @Test
    void changePriceMenuPriceCompareToTotalPrice() {
        final Product product = productRepository.save(ProductFixure.create("후라드이 치킨", 1000));
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
        Menu menu = menuRepository.save(MenuFixture.create(menuGroup, product));

        product.setPrice(new BigDecimal(500));
        productService.changePrice(product.getId(), product);

        boolean displayed = menuRepository.findById(menu.getId()).get().isDisplayed();
        assertThat(displayed).isFalse();
    }

}
