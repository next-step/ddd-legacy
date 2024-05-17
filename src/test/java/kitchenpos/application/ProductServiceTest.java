package kitchenpos.application;

import kitchenpos.config.ServiceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenuWithId;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupWithId;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ServiceTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Nested
    class createTest {
        @DisplayName("메뉴를 생성할 수 있다.")
        @Test
        void createSuccessTest() {
            final Product product = createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000));

            final Product createdProduct = productService.create(product);

            assertThat(createdProduct.getId()).isNotNull();
        }

        @DisplayName("메뉴 가격이 존재하지 않은 경우에 예외가 발생한다.")
        @Test
        void createFailWhenPriceIsNullTest() {
            final Product product = createProductWithId("후라이드 치킨", null);

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 가격이 0 미만인 경우에 예외가 발생한다.")
        @Test
        void createFailWhenPriceIsLessThanZeroTest() {
            final Product product = createProductWithId("후라이드 치킨", BigDecimal.valueOf(-1));

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름이 존재하지 않은 경우에 예외가 발생한다.")
        @ParameterizedTest
        @NullSource
        void createFailWhenNameIsNullTest(final String name) {
            final Product product = createProductWithId(name, BigDecimal.valueOf(16000));

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 이름에 욕설이 포함된 경우에 예외가 발생한다.")
        @Test
        void createFailWhenNameContainsProfanityTest() {
            given(purgomalumClient.containsProfanity("시발 후라이드 치킨")).willReturn(true);
            final Product product = createProductWithId("시발 후라이드 치킨", BigDecimal.valueOf(16000));

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class changePriceTest {
        @DisplayName("상품의 가격을 변경할 수 있다.")
        @Test
        void changePriceSuccessTest() {
            Product product = createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000));
            product = productService.create(product);

            final Product changedProduct = productService.changePrice(product.getId(), createProductWithId("후라이드 치킨", BigDecimal.valueOf(17000)));

            assertThat(changedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(17000));
        }

        @DisplayName("상품 가격이 존재하지 않은 경우에 예외가 발생한다.")
        @Test
        void changePriceFailWhenPriceIsNullTest() {
            Product product = createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000));
            product = productService.create(product);

            final UUID productId = product.getId();
            product.setPrice(null);
            final Product changeProduct = product;

            assertThatThrownBy(() -> productService.changePrice(productId, changeProduct))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 가격이 0 미만인 경우에 예외가 발생한다.")
        @Test
        void changePriceFailWhenPriceIsLessThanZeroTest() {
            Product product = createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000));
            product = productService.create(product);

            final UUID productId = product.getId();
            product.setPrice(BigDecimal.valueOf(-16000));
            final Product changeProduct = product;

            assertThatThrownBy(() -> productService.changePrice(productId, changeProduct))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품이 존재하는 메뉴에서 가격 변경 시 메뉴의 상품이 더 높은 경우 메뉴의 displayed가 false로 변경된다.")
        @Test
        void changePriceFailWhenMenuPriceIsHigherThanProductPriceTest() {
            Product product = createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000));
            product = productService.create(product);

            final MenuProduct menuProduct = createMenuProduct(product, 1);
            MenuGroup menuGroup = createMenuGroupWithId("치킨 메뉴");
            menuGroup = menuGroupRepository.save(menuGroup);
            Menu menu = createMenuWithId(menuGroup, "후라이드 치킨 세트", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            product.setPrice(BigDecimal.valueOf(15000));

            assertThat(menu.isDisplayed()).isTrue();

            productService.changePrice(product.getId(), product);
            final Optional<Menu> findMenu = menuRepository.findById(menu.getId());

            assertThat(findMenu).isPresent();
            assertThat(findMenu.get().isDisplayed()).isFalse();
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("상품 목록을 조회한다.")
        @Test
        void findAllSuccessTest() {
            Product friedChickenProduct = createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000));
            Product seasonedChickenProduct = createProductWithId("양념 치킨", BigDecimal.valueOf(16000));

            friedChickenProduct = productService.create(friedChickenProduct);
            seasonedChickenProduct = productService.create(seasonedChickenProduct);

            final List<Product> products = productService.findAll();
            final List<UUID> productIds = products.stream()
                    .map(Product::getId)
                    .toList();

            assertThat(products).hasSize(2);
            assertThat(productIds).contains(friedChickenProduct.getId(), seasonedChickenProduct.getId());
        }
    }
}
