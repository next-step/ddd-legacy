package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ProductServiceTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;
    private ProductService productService;
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        this.productRepository = new InMemoryProductRepository();
        this.menuRepository = new InMemoryMenuRepository();
        this.purgomalumClient = new FakePurgomalumClient(List.of("비속어", "욕설"));
        this.productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        this.menuGroupRepository = new InMemoryMenuGroupRepository();
    }

    @DisplayName("상품을 생성할 수 있다")
    @Nested
    class ProductCreator {

        @DisplayName("상품명과 가격을 입력하여 상품을 생성한다")
        @Test
        void createProduct() {
            Product request = ProductFixture.createProduct("치킨버거", new BigDecimal(7000));

            Product product = productService.create(request);

            assertThat(product)
                    .extracting(Product::getName, Product::getPrice)
                    .containsExactly("치킨버거", new BigDecimal(7000));
        }

        @DisplayName("상품명은 반드시 입력되어야 한다")
        @Test
        void notNullProductName() {
            Product request = ProductFixture.createProduct(null, new BigDecimal(7000));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("가격은 반드시 입력되어야 한다")
        @Test
        void notNullProductPrice() {
            Product request = ProductFixture.createProduct("치킨버거", null);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("상품명은 비속어가 있으면 등록할 수 없다")
        @ParameterizedTest
        @ValueSource(strings = {"비속어", "욕설이 포함된 상품명"})
        void validateProductName(String name) {
            Product request = ProductFixture.createProduct(name, new BigDecimal(7000));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("상품의 가격은 0원 이상이어야 한다")
        @Test
        void validatePriceOnCreate() {
            Product request = ProductFixture.createProduct("치킨버거", new BigDecimal(-1));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

    }

    @DisplayName("상품의 가격을 수정할 수 있다")
    @Nested
    class ProductPriceUpdater {

        @DisplayName("상품의 가격을 수정할 수 있다")
        @Test
        void modifyPrice() {
            Product product = productService.create(ProductFixture.createProduct(UUID.randomUUID(), "치킨버거", new BigDecimal(7000)));

            ReflectionTestUtils.setField(product, "price", new BigDecimal(8000));
            Product modifiedProduct = productService.changePrice(product.getId(), product);

            assertThat(modifiedProduct.getPrice()).isEqualTo(new BigDecimal(8000));
        }

        @DisplayName("상품의 가격은 0원 이상인 경우만 수정 가능하다")
        @Test
        void validatePriceOnModify() {
            Product product = productService.create(ProductFixture.createProduct(UUID.randomUUID(), "치킨버거", new BigDecimal(7000)));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.changePrice(product.getId(), ProductFixture.createProduct("치킨버거", new BigDecimal(-1))));
        }

        @DisplayName("메뉴의 가격이 메뉴에 속한 상품들의 가격 총 합보다 클 경우, 유효하지 않은 메뉴이므로 메뉴판에 전시할 수 없다")
        @Test
        void validatePriceBySetMenu() {
            //given
            Product chicken = productService.create(ProductFixture.createProduct("후라이드치킨", new BigDecimal(25000)));
            Product coke = productService.create(ProductFixture.createProduct("콜라", new BigDecimal(2500)));

            MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.createMenuGroup(UUID.randomUUID(), "세트메뉴"));

            MenuProduct chickenMenuProduct = MenuProductFixture.createMenuProduct(chicken, 1);
            MenuProduct cokeMenuProduct = MenuProductFixture.createMenuProduct(coke, 1);

            BigDecimal productSum = chicken.getPrice().add(coke.getPrice());
            Menu setMenu = menuRepository.save(MenuFixture.createMenu(
                    UUID.randomUUID(),
                    menuGroup,
                    menuGroup.getId(),
                    "후라이드치킨세트",
                    productSum,
                    true,
                    List.of(chickenMenuProduct, cokeMenuProduct)
            ));
            //when
            BigDecimal newPrice = chicken.getPrice().add(new BigDecimal(-1000));
            ReflectionTestUtils.setField(chicken, "price", newPrice);
            productService.changePrice(chicken.getId(), chicken);
            //when
            Menu resultMenu = menuRepository.findById(setMenu.getId()).get();
            assertThat(resultMenu.isDisplayed()).isFalse();
        }
    }

    //region [메뉴 조회]
    @DisplayName("모든 메뉴를 조회할 수 있다")
    @Test
    void findAll() {
        productService.create(ProductFixture.createProduct("치킨버거", new BigDecimal(7000)));
        productService.create(ProductFixture.createProduct("새우버거", new BigDecimal(8000)));

        List<Product> products = productService.findAll();

        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting(Product::getName, product -> product.getPrice().intValue())
                .contains(
                        Tuple.tuple("치킨버거", 7000),
                        Tuple.tuple("새우버거", 8000)
                );
    }
    //endregion*/

}
