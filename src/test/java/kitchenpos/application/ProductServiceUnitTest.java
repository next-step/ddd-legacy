package kitchenpos.application;

import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    MenuRepository menuRepository;

    @Mock
    PurgomalumClient purgomalumClient;

    @InjectMocks
    ProductService productService;

    @DisplayName("상품 추가")
    @Test
    void create_product_success() {
        //given
        Product newProduct = new Product();
        newProduct.setId(UUID.randomUUID());
        newProduct.setName("간장 치킨");
        newProduct.setPrice(BigDecimal.valueOf(10000L));

        doReturn(newProduct).when(productRepository).save(any(Product.class));
        doReturn(false).when(purgomalumClient).containsProfanity(newProduct.getName());

        //when
        Product savedProduct = productService.create(newProduct);

        //then
        assertThat(savedProduct.getId()).isEqualTo(newProduct.getId());
        assertThat(savedProduct.getName()).isEqualTo(newProduct.getName());
        assertThat(savedProduct.getPrice()).isEqualTo(newProduct.getPrice());
        assertThat(savedProduct).isEqualTo(newProduct);
    }

    @DisplayName("상품 추가 실패 - 가격 미지정 or 음수")
    @ParameterizedTest
    @MethodSource("paramsForTestPrice")
    void create_fail_invalid_price(BigDecimal price) {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setPrice(price);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(newProduct));
    }

    @DisplayName("상품 추가 실패 - 상품명 미지정")
    @Test
    void create_fail_empty_name() {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setName(null);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(newProduct));
    }

    @DisplayName("상품 추가 실패 - 비속어 포함")
    @Test
    void create_fail_profanity_name() {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setName("wop");

        doReturn(true).when(purgomalumClient).containsProfanity(newProduct.getName());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> productService.create(newProduct));
    }

    @DisplayName("상품 가격 수정")
    @Test
    void change_price_success() {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setPrice(BigDecimal.valueOf(20000));

        doReturn(Optional.of(newProduct)).when(productRepository).findById(newProduct.getId());
        List<Menu> menus = new LinkedList<>();
        doReturn(menus).when(menuRepository).findAllByProductId(newProduct.getId());

        //when
        productService.changePrice(newProduct.getId(), newProduct);
        Product findProduct = productRepository.findById(newProduct.getId()).get();

        //then
        assertAll(
                () -> assertNotNull(findProduct),
                () -> assertThat(findProduct).isEqualTo(newProduct),
                () -> assertThat(findProduct.getPrice()).isEqualTo(newProduct.getPrice())
        );
    }

    @DisplayName("상품 가격 수정 실패 - 존재하지 않는 상품")
    @Test
    void change_price_fail_not_exist_product() {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setPrice(BigDecimal.valueOf(20000));
        UUID newProductId = newProduct.getId();

        doReturn(Optional.empty()).when(productRepository).findById(newProductId);

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> productService.changePrice(newProductId, newProduct));
    }

    @DisplayName("상품 가격 수정 실패 - 가격 미지정")
    @Test
    void change_price_fail_null_price() {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setPrice(null);
        UUID newProductId = newProduct.getId();

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(newProductId, newProduct));
    }

    @DisplayName("상품 가격 수정 실패 - 가격이 음수")
    @Test
    void change_price_fail_negative_price() {
        //given
        Product newProduct = ProductFixture.generateProduct();
        newProduct.setPrice(BigDecimal.valueOf(-100000));
        UUID newProductId = newProduct.getId();

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(newProductId, newProduct));
    }

    @DisplayName("상품 가격 수성 시, 메뉴 숨김 처리 - 상품의 총합 값보다 비싸진 메뉴가 있다면 메뉴를 숨김 처리한다.")
    @Test
    void hide_menu() {
        //given
        MenuGroup menuGroup = MenuFixture.generateMenuGroup();
        Product product = ProductFixture.generateProduct();
        Product product01 = ProductFixture.generateProduct("양념 치킨", 11000L);
        Product product02 = ProductFixture.generateProduct("후라이드 치킨", 11000L);
        List<MenuProduct> menuProducts = MenuFixture.generateMenuProducts(product, product01, product02);
        Menu menu = MenuFixture.generateMenu(menuGroup, menuProducts);

        BigDecimal totalPrice = menu.totalPriceOfProducts();
        menu.setPrice(totalPrice.add(BigDecimal.valueOf(10000)));

        doReturn(Optional.of(product)).when(productRepository).findById(product.getId());
        List<Menu> menus = Arrays.asList(menu);
        doReturn(menus).when(menuRepository).findAllByProductId(product.getId());
        //when
        productService.changePrice(product.getId(), product);

        //then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("상품 가격 수성 시, 메뉴 숨김 처리하지 않음 - 상품의 총합 값보다 비싸진 메뉴가 없다.")
    @Test
    void do_not_hide_menu() {
        //given
        MenuGroup menuGroup = MenuFixture.generateMenuGroup();
        Product product = ProductFixture.generateProduct();
        Product product01 = ProductFixture.generateProduct("양념 치킨", 11000L);
        Product product02 = ProductFixture.generateProduct("후라이드 치킨", 11000L);
        List<MenuProduct> menuProducts = MenuFixture.generateMenuProducts(product, product01, product02);
        Menu menu = MenuFixture.generateMenu(menuGroup, menuProducts);
        menu.setPrice(BigDecimal.valueOf(10));

        doReturn(Optional.of(product)).when(productRepository).findById(product.getId());
        List<Menu> menus = Arrays.asList(menu);
        doReturn(menus).when(menuRepository).findAllByProductId(product.getId());

        //when
        productService.changePrice(product.getId(), product);

        //then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("모든 상품 조회")
    @Test
    void findAll() {
        //given
        Product product01 = ProductFixture.generateProduct();
        Product product02 = ProductFixture.generateProduct();
        Product product03 = ProductFixture.generateProduct();

        List<Product> products = Arrays.asList(product01, product02, product03);
        doReturn(products).when(productRepository).findAll();

        //when
        List<Product> findProducts = productService.findAll();

        //then
        assertAll(
                () -> assertThat(findProducts).hasSize(3),
                () -> assertThat(findProducts.get(0)).isEqualTo(product01),
                () -> assertThat(findProducts.get(1)).isEqualTo(product02),
                () -> assertThat(findProducts.get(2)).isEqualTo(product03)
        );
    }

    private static Stream<Arguments> paramsForTestPrice() {
        return Stream.of(
                Arguments.of(null, IllegalArgumentException.class),
                Arguments.of(BigDecimal.valueOf(-1000), IllegalArgumentException.class)
        );
    }
}
