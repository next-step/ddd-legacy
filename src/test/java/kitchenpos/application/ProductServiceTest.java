package kitchenpos.application;

import kitchenpos.FixtureData;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends FixtureData {

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
        fixtureProducts();
    }

    @DisplayName("상품 생성")
    @Test
    void createProduct() {
        Product product = products.get(0);
        given(productRepository.save(any())).willReturn(product);

        Product createProduct = productService.create(product);

        assertThat(createProduct).isEqualTo(product);
    }

    @DisplayName("상품 가격 없을 시 예외")
    @ParameterizedTest
    @NullSource
    void negativePrice(BigDecimal nullPrice) {
        Product product = products.get(0);
        product.setPrice(nullPrice);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(new Product()));
    }

    @DisplayName("상품명 없을 시 예외 확인")
    @ParameterizedTest
    @NullSource
    void negativeProductName(String name) {
        Product product = products.get(0);
        product.setName(name);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(product));
    }

    @DisplayName("상품명 한글자 이상")
    @ParameterizedTest
    @ValueSource(strings = {"죽", "피자", "햄버거"})
    void minimumProductNameLength(String name) {
        // given
        Product product = products.get(0);
        product.setName(name);

        given(productRepository.save(any())).willReturn(product);

        // when
        Product createProduct = productService.create(product);

        // then
        assertThat(createProduct).isEqualTo(product);
    }

    @DisplayName("상품 가격은 0 미만 불가")
    @ParameterizedTest
    @ValueSource(ints = {-1})
    void negativeMinusPrice(int price) {
        Product product = products.get(0);
        product.setPrice(BigDecimal.valueOf(price));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(new Product()));
    }

    @DisplayName("상품 가격은 0 이상")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void minimumProductPriceRange (int price) {
        // given
        Product product = products.get(0);
        product.setPrice(BigDecimal.valueOf(price));

        given(productRepository.save(any())).willReturn(product);

        // when
        Product createProduct = productService.create(product);

        // then
        assertThat(createProduct).isEqualTo(product);
    }

    @DisplayName("상품명 중복 허용")
    @Test
    void duplicateProductName() {
        // given
        Product product = products.get(0);
        Product product2 = products.get(0);

        given(productRepository.save(any())).willReturn(product).willReturn(product2);

        // when
        Product createProduct = productService.create(product);
        Product createProduct2 = productService.create(product2);

        // then
        assertThat(createProduct.getName()).isEqualTo(createProduct2.getName());
    }

    @DisplayName("상품 가격 변경")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void changeProductPrice(int price) {
        // given
        BigDecimal changePrice = new BigDecimal(price);

        Product product = new Product();
        product.setPrice(changePrice);

        given(productRepository.findById(FIRST_ID)).willReturn(Optional.of(products.get(0)));

        // when
        Product changeProduct = productService.changePrice(FIRST_ID, product);

        // then
        assertThat(changeProduct.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("상품 가격 변경 시 0 미만 예외")
    @ParameterizedTest
    @ValueSource(ints = {-1})
    void negativeMinimumChangeProductPrice(int price) {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(price));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.changePrice(FIRST_ID, product));
    }

    @DisplayName("상품 가격 변경 후 메뉴가격이 높으면 숨김")
    @Test
    void menuHideToProductPrice() {
        // given
        List<Menu> menuList = new ArrayList<>();
        Menu menu = menus.get(0);
        menu.setDisplayed(MENU_SHOW);

        MenuProduct menuProduct = menu.getMenuProducts().get(0);
        Product product = menuProduct.getProduct();
        product.setPrice(BigDecimal.valueOf(10));

        UUID producId = product.getId();

        menuList.add(menu);

        given(productRepository.findById(producId)).willReturn(Optional.of(product));
        given(menuRepository.findAllByProductId(producId)).willReturn(menuList);

        // when
        productService.changePrice(producId, product);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("상품 내역 확인")
    @Test
    void findAll() {
        // given
        given(productRepository.findAll()).willReturn(products);

        // when
        List<Product> findAll = productService.findAll();

        verify(productRepository).findAll();
        verify(productRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(products.containsAll(findAll)).isTrue(),
                () -> assertThat(products.size()).isEqualTo(findAll.size())
        );
    }
}