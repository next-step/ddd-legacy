package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class ProductServiceTest extends BaseServiceTest {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    public ProductServiceTest(final ProductService productService, final ProductRepository productRepository, final MenuGroupRepository menuGroupRepository, final MenuRepository menuRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
    }

    @DisplayName("상품은 등록이 가능하다")
    @Test
    void test1() {
        final Product product = createProduct();
        given(purgomalumClient.containsProfanity(product.getName())).willReturn(false);

        final Product createdProduct = productService.create(product);

        final Product foundProduct = productRepository.findAll().get(0);

        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo(product.getName());
        assertThat(createdProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(foundProduct.getId()).isEqualTo(createdProduct.getId());
    }

    @DisplayName("상품의 이름은 비어있으면 안된다")
    @Test
    void test2() {
        final Product product = createProduct(null, BigDecimal.TEN);
        given(purgomalumClient.containsProfanity(product.getName())).willReturn(false);

        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품의 이름에 비속어가 포함되면 안된다")
    @Test
    void test3() {
        final Product product = createProduct("비속어", BigDecimal.TEN);

        when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

        assertThatIllegalArgumentException().isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품은 가격 수정이 가능하다")
    @Test
    void test4() {
        final Product product = productRepository.save(createProduct(UUID.randomUUID(), "상품", BigDecimal.TEN));
        final Product newPriceProduct = createProduct("상품", BigDecimal.ONE);

        final Product changedPriceProduct = productService.changePrice(product.getId(), newPriceProduct);

        assertThat(changedPriceProduct.getId()).isEqualTo(product.getId());
        assertThat(changedPriceProduct.getPrice()).isEqualTo(newPriceProduct.getPrice());
    }

    @DisplayName("상품 가격이 수정 되었을 때 해당 상품을 포함하여 판매중인 메뉴의 가격이 더 비싸질 경우 해당 메뉴는 미표출 한다.")
    @Test
    void test5() {
        final Product product = productRepository.save(createProduct(UUID.randomUUID(), "치킨", BigDecimal.TEN));
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID()));
        final MenuProduct menuProduct = createMenuProduct(product);
        final Menu cheapMenu = menuRepository.save(createMenu(UUID.randomUUID(), "치킨", new BigDecimal(0), menuGroup, true, List.of(menuProduct)));
        final Menu expensiveMenu = menuRepository.save(createMenu(UUID.randomUUID(), "치킨", new BigDecimal(100000000), menuGroup, true, List.of(menuProduct)));
        final Product newPriceProduct = createProduct("상품", BigDecimal.ONE);

        final Product changedPriceProduct = productService.changePrice(product.getId(), newPriceProduct);

        assertThat(changedPriceProduct.getId()).isEqualTo(product.getId());
        assertThat(changedPriceProduct.getPrice()).isEqualTo(newPriceProduct.getPrice());
        assertThat(cheapMenu.isDisplayed()).isTrue();
        assertThat(expensiveMenu.isDisplayed()).isFalse();
    }

    @DisplayName("상품은 전체 조회가 가능하다")
    @Test
    void test6() {
        final Product chicken = productRepository.save(createProduct(UUID.randomUUID(), "치킨", BigDecimal.ONE));
        final Product pizza = productRepository.save(createProduct(UUID.randomUUID(), "피자", BigDecimal.ZERO));

        final List<Product> products = productRepository.findAll();

        assertThat(products).containsExactly(chicken, pizza);
    }
}
