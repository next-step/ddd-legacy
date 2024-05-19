package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.application.MenuFixture.createMenu;
import static kitchenpos.fixture.application.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.application.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.application.ProductFixture.changeProduct;
import static kitchenpos.fixture.application.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private Product 강정치킨;
    private Product 후라이드치킨;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        강정치킨 = createProduct("강정치킨", BigDecimal.valueOf(12_000));
        후라이드치킨 = createProduct("후라이드치킨", BigDecimal.valueOf(11_000));
    }

    @DisplayName("상품 등록")
    @Nested
    class ProductCreate {

        @ParameterizedTest
        @DisplayName("상품을 등록한다")
        @ValueSource(strings = {"", " ", "강정치킨"})
        void create(String name) {
            // given
            Product product = createProduct(name, BigDecimal.valueOf(18_000));
            given(productRepository.save(any())).willReturn(product);

            // when
            Product createdProduct = productService.create(product);

            // then
            assertThat(createdProduct.getId()).isEqualByComparingTo(product.getId());
        }

        @ParameterizedTest
        @DisplayName("상품의 이름은 비속어가 될 수 없다")
        @ValueSource(strings = {"비속어1", "비속어2"})
        void createProductNameCannotBeProfanity(String name) {
            Product product = createProduct(name);

            when(purgomalumClient.containsProfanity(name)).thenReturn(true);

            assertThatThrownBy(() -> {
                productService.create(product);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("상품의 이름은 null 이면 예외가 발생한다")
        @NullSource
        void createProductNameIsNotNull(String name) {
            Product product = createProduct(name);

            assertThatThrownBy(() -> {
                productService.create(product);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품의 가격은 0원 이상이어야 한다")
        void createProductPriceIsGreaterThanZero() {
            // given
            Product product = createProduct(BigDecimal.ZERO);
            given(productRepository.save(any())).willReturn(product);

            // when
            Product createdProduct = productService.create(product);

            // then
            assertThat(createdProduct.getId()).isEqualByComparingTo(product.getId());
        }

        @ParameterizedTest
        @DisplayName("상품의 가격은 null 이면 예외가 발생한다")
        @NullSource
        void createProductPriceIsNotNull(BigDecimal price) {
            Product product = createProduct(price);

            assertThatThrownBy(() -> {
                productService.create(product);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class ProductModify {

        @Test
        @DisplayName("상품의 가격을 수정한다")
        void changePrice() {
            when(productRepository.findById(any())).thenReturn(Optional.of(강정치킨));

            Product request = changeProduct(BigDecimal.valueOf(10_000));
            Product result = productService.changePrice(강정치킨.getId(), request);

            assertThat(result.getPrice()).isEqualTo(request.getPrice());
        }

        @ParameterizedTest
        @DisplayName("변경하려는 상품의 가격이 null 이면 예외가 발생한다")
        @NullSource
        void changePriceIsNull(BigDecimal price) {
            Product request = changeProduct(price);

            assertThatThrownBy(() -> {
                productService.changePrice(강정치킨.getId(), request);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("변경하려는 상품의 가격이 0보다 작으면 예외가 발생한다")
        @ValueSource(ints = {-1, -20_000})
        void changePriceIsLowerThanZero(int price) {
            Product request = changeProduct(BigDecimal.valueOf(price));

            assertThatThrownBy(() -> {
                productService.changePrice(강정치킨.getId(), request);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴의 가격이 메뉴에 등록되어 있는 상품의 합(가격 * 수량)보다 크다면 메뉴는 전시되지 않는다")
        void changeProductPriceThenNotDisplayed() {
            // given
            BigDecimal 변경하려는_가격 = BigDecimal.valueOf(8_000);
            Product 강정치킨 = createProduct("강정치킨", BigDecimal.valueOf(12_000));
            List<Menu> menus = createMenus(강정치킨);

            when(productRepository.findById(any())).thenReturn(Optional.of(강정치킨));
            when(menuRepository.findAllByProductId(강정치킨.getId())).thenReturn(menus);
            Product request = createProduct(변경하려는_가격);

            // when
            productService.changePrice(강정치킨.getId(), request);

            // then
            menus.forEach(menu -> assertThat(menu.isDisplayed()).isFalse());
        }
    }

    @Test
    @DisplayName("목록 - 상품의 목록을 볼 수 있다.")
    void products() {
        List<Product> products = List.of(강정치킨, 후라이드치킨);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertThat(result).containsExactly(강정치킨, 후라이드치킨);
    }

    private List<Menu> createMenus(Product product) {
        MenuGroup menuGroup = createMenuGroup("1+1 메뉴");
        MenuProduct menuProduct = createMenuProduct(product, 2);
        Menu menu = createMenu("강정치킨 + 강정치킨", menuGroup, List.of(menuProduct), BigDecimal.valueOf(18_000));
        return List.of(menu);
    }
}
