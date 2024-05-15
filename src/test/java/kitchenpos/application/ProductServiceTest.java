package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.config.ProductTestContextConfiguration;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.helper.MenuGroupTestHelper;
import kitchenpos.helper.MenuProductTestHelper;
import kitchenpos.helper.MenuTestHelper;
import kitchenpos.helper.ProductTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Transactional
@Import(ProductTestContextConfiguration.class)
class ProductServiceTest extends SetupTest{
    @Autowired
    private ProductService productService;
    private Product 미니꿔바로우;

    @BeforeEach
    void setUp() {
        super.setUp();

        MenuGroup 추천메뉴 = MenuGroupTestHelper.메뉴카테고리_생성("추천메뉴");

        미니꿔바로우 = ProductTestHelper.음식_생성("미니꿔바로우", BigDecimal.valueOf(8000));
        Product 마라탕 = ProductTestHelper.음식_생성("마라탕", BigDecimal.valueOf(10000));

        MenuProduct 마라탕메뉴 = MenuProductTestHelper.음식메뉴_생성(마라탕, 1);
        MenuProduct 미니꿔바로우메뉴 = MenuProductTestHelper.음식메뉴_생성(미니꿔바로우, 1);

        List<MenuProduct> menuProducts = Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴);

        BigDecimal totalPriceOfProducts = menuProducts.stream()
                .map(MenuProduct::getProduct)
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Menu 마라세트 = MenuTestHelper.메뉴_생성(추천메뉴, "마라세트", totalPriceOfProducts.subtract(BigDecimal.valueOf(1000)), menuProducts, true);
    }

    @DisplayName("음식을 등록한다.")
    @Test
    void createProcduct(){
        //given
        Product requestProduct = new Product();
        requestProduct.setName("마라탕");
        requestProduct.setPrice(BigDecimal.valueOf(10000));

        //when
        Product createProduct = productService.create(requestProduct);

        //then
        assertThat(createProduct.getName()).isSameAs(requestProduct.getName());
    }

    @DisplayName("이름이 없거나 비속어가 속한 이름의 음식을 등록할 경우 IllegalArgumentException 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "바보음식"})
    void createProcductOfEnptyNameOrIncludeProfanity(String name){
        //given
        Product requestProduct = new Product();
        requestProduct.setName((name.equals("") ? null : name));
        requestProduct.setPrice(BigDecimal.valueOf(10000));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(requestProduct));
    }

    @DisplayName("음식의 가격을 변경하다.")
    @Test
    void changePriceOfProcduct(){
        //given
        Product requestProduct = new Product();
        requestProduct.setPrice(BigDecimal.valueOf(9000));

        //when
        Product changeProduct = productService.changePrice(미니꿔바로우.getId(), requestProduct);

        //then
        assertThat(changeProduct.getPrice()).isSameAs(requestProduct.getPrice());
    }

    @Nested
    @DisplayName("가격을 변경하려는 음식이 ")
    class changePriceExceptionTestCase{
        @DisplayName("만약에 변경할 가격이 없거나 음수인 경우 IllegalArgumentException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {0, -1000})
        void changeNoPriceOrMinusPriceOfProcduct(int price){
            //given
            Product requestProduct = new Product();
            requestProduct.setPrice((price == 0 ? null : BigDecimal.valueOf(price)));

            //when && then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> productService.changePrice(미니꿔바로우.getId(), requestProduct));
        }

        @DisplayName("만약에 등록되어 있지 않는 경우 NoSuchElementException 예외가 발생한다.")
        @Test
        void changePriceOfNoProcduct(){
            //given
            Product requestProduct = new Product();
            requestProduct.setPrice(BigDecimal.valueOf(5000));

            //when && then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> productService.changePrice(UUID.randomUUID(), requestProduct));
        }
    }

    @DisplayName("음식의 가격 변경으로 기존에 등록된 메뉴의 가격이 단일 메뉴들의 총 금액보다 비싸진 경우 메뉴 노출 상태를 판매중단으로 바꾼다.")
    @Test
    void changePriceOfProcduct2(){
        //given
        Product requestProduct = new Product();
        requestProduct.setPrice(BigDecimal.valueOf(5000));

        //when
        Product changeProduct = productService.changePrice(미니꿔바로우.getId(), requestProduct);

        List<Menu> menus = MenuTestHelper.특정음식이_속한_메뉴들_조회(changeProduct.getId());
        menus.forEach(a -> {
            assertThat(a.isDisplayed()).isSameAs(false);
        });
    }
}
