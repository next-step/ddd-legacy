package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;
    private OrderTable orderTable;
    private MenuGroup menuGroup;
    private Product product;
    private MenuProduct menuProduct;
    private Menu menu;
    @BeforeEach
    void setUp() {
        orderTable = createOrderTable("매장테이블", true, 2);
        menuGroup = createMenuGroup("메뉴그룹1");
        product = createProduct("상품1", new BigDecimal("1000"));
        menuProduct = createMenuProduct(product, 1);
        menu = createMenu("메뉴1", new BigDecimal("1500"), menuGroup, true, List.of(menuProduct));
    }

    @Test
    void 상품은_등록할_수_있다() {
        //given
        Product product = createProduct("상품1", new BigDecimal("1000"));

        given(purgomalumClient.containsProfanity(any()))
                .willReturn(false);
        given(productRepository.save(any()))
                .willReturn(product);

        //when
        Product result = productService.create(product);

        //then
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
    }

    @ParameterizedTest
    @NullSource
    void 상품의_이름은_비어있을_수_없다(String productName) {
        //given
        Product product = createProduct(productName, new BigDecimal("1000"));

        //when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품의_이름은_욕설을_포함할_수_없다() {
        //given
        Product product = createProduct("상품", new BigDecimal("1000"));

        given(purgomalumClient.containsProfanity(any()))
                .willReturn(true);

        //when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품의_가격은_0원미만일_수_없다() {
        //given
        Product product = createProduct("상품", new BigDecimal("-1"));

        //when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품의_가격을_변경할_수_있다() {
        //given
        Product request = createProduct("상품1", new BigDecimal("2000"));
        Product product = createProduct("상품1", new BigDecimal("1000"));

        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(any()))
                .willReturn(List.of(menu));

        //when
        Product result = productService.changePrice(product.getId(), request);

        //then
        assertThat(result.getPrice())
                .isEqualTo(request.getPrice());
    }

    @Test
    void 가격을_수정하는_상품을_가진_메뉴의_가격이_수정_후보다_수정_전이_크다면_메뉴를_노출하지_않는다() {
        //given
        Product request = createProduct("상품1", new BigDecimal("20"));
        Product product = createProduct("상품1", new BigDecimal("200"));

        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(any()))
                .willReturn(List.of(menu));

        //when
        productService.changePrice(product.getId(), request);

        //then
        assertThat(menu.isDisplayed()).isEqualTo(false);
    }

    @Test
    void 모든_상품을_조회할_수_있다() {
        //given
        Product product1 = createProduct("상품1", new BigDecimal("200"));
        Product product2 = createProduct("상품2", new BigDecimal("200"));

        List<Product> products = List.of(product1, product2);

        given(productRepository.findAll())
                .willReturn(products);

        //when
        List<Product> result = productService.findAll();

        //then
        assertThat(result.size()).isEqualTo(products.size());
    }
}