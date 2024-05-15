package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.config.MenuTestContextConfiguration;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Nested
@DisplayName("메뉴 테스트")
@Transactional
@Import(MenuTestContextConfiguration.class)
class MenuServiceTest extends SetupTest{
    @Autowired
    private MenuService menuService;

    private MenuGroup 추천메뉴;
    private MenuProduct 마라탕메뉴;
    private MenuProduct 미니꿔바로우메뉴;
    private MenuProduct 콜라메뉴;
    private Product 마라탕;
    private Product 미니꿔바로우;
    private Product 콜라;
    private Menu 마라세트;
    private List<Menu> 메뉴들 = new ArrayList<>();


    @BeforeEach
    void setUp() {
        super.setUp();

        추천메뉴 = MenuGroupTestHelper.메뉴카테고리_생성("추천메뉴");

        마라탕 = ProductTestHelper.음식_생성("마라탕", BigDecimal.valueOf(10000));
        미니꿔바로우 = ProductTestHelper.음식_생성("미니꿔바로우", BigDecimal.valueOf(8000));
        콜라 = ProductTestHelper.음식_생성("콜라", BigDecimal.valueOf(3000));

        마라탕메뉴 = MenuProductTestHelper.음식메뉴_생성(마라탕, 1);
        미니꿔바로우메뉴 = MenuProductTestHelper.음식메뉴_생성(미니꿔바로우, 1);
        콜라메뉴 = MenuProductTestHelper.음식메뉴_생성(콜라, 1);

        마라세트 = MenuTestHelper.메뉴_생성(추천메뉴, "마라세트", BigDecimal.valueOf(16000), Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴), true);

        메뉴들.add(마라세트);
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void createMenu(){
        //given
        Menu requestMenu = new Menu();
        requestMenu.setMenuGroup(추천메뉴);
        requestMenu.setMenuGroupId(추천메뉴.getId());
        requestMenu.setName("나홀로세트");
        requestMenu.setPrice(BigDecimal.valueOf(11000));
        requestMenu.setMenuProducts(Arrays.asList(마라탕메뉴, 콜라메뉴));
        requestMenu.setDisplayed(true);

        //when
        Menu createMenu = menuService.create(requestMenu);

        //then
        assertThat(createMenu.getName()).isSameAs(requestMenu.getName());
    }

    @Nested
    @DisplayName("등록하려는 메뉴가 ")
    class createMenuExceptionTestCase{
        @DisplayName("만약 가격이 없거나 음수인 경우 IllegalArgumentException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {0, -1000})
        void createMenuOfNoPrice(int price){
            //given
            Menu requestMenu = new Menu();
            requestMenu.setMenuGroup(추천메뉴);
            requestMenu.setMenuGroupId(추천메뉴.getId());
            requestMenu.setName("나홀로세트");
            requestMenu.setPrice((price==0 ? null : BigDecimal.valueOf(price)));
            requestMenu.setMenuProducts(Arrays.asList(마라탕메뉴, 콜라메뉴));
            requestMenu.setDisplayed(true);

            //when && then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("만약에 이름이 없거나 비속어를 포함하고있는 경우 IllegalArgumentException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"", "바보메뉴"})
        void createMenuNameEmptyOfIncluedProfanity(String name){
            //given
            String paramName = (name.equals("") ? null : name);

            Menu requestMenu = new Menu();
            requestMenu.setMenuGroup(추천메뉴);
            requestMenu.setMenuGroupId(추천메뉴.getId());
            requestMenu.setName(paramName);
            requestMenu.setPrice(BigDecimal.valueOf(10000));
            requestMenu.setMenuProducts(Arrays.asList(마라탕메뉴, 콜라메뉴));
            requestMenu.setDisplayed(true);

            //when && then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("만약에 등록되지 않은 음식을 포함하고있는 경우 IllegalArgumentException 예외가 발생한다.")
        @Test
        void createMenuOfNoProductMenu(){
            //given
            MenuProduct 없는음식메뉴 = MenuProductTestHelper.음식메뉴_생성(new Product(), 1);

            Menu requestMenu = new Menu();
            requestMenu.setMenuGroup(추천메뉴);
            requestMenu.setMenuGroupId(추천메뉴.getId());
            requestMenu.setName("나홀로세트");
            requestMenu.setPrice(BigDecimal.valueOf(10000));
            requestMenu.setMenuProducts(Arrays.asList(없는음식메뉴));
            requestMenu.setDisplayed(true);

            //when && then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("만약에 수량이 음수인 음식을 포함하고있는 경우 IllegalArgumentException 예외가 발생한다.")
        @Test
        void createMenuOfNoQurantityProductMenu(){
            //given
            MenuProduct 수량이_음수인_음식메뉴 = MenuProductTestHelper.음식메뉴_생성(ProductTestHelper.음식_생성("음식", BigDecimal.valueOf(1000)), -1);

            Menu requestMenu = new Menu();
            requestMenu.setMenuGroup(추천메뉴);
            requestMenu.setMenuGroupId(추천메뉴.getId());
            requestMenu.setName("나홀로세트");
            requestMenu.setPrice(BigDecimal.valueOf(10000));
            requestMenu.setMenuProducts(Arrays.asList(마라탕메뉴, 수량이_음수인_음식메뉴));
            requestMenu.setDisplayed(true);

            //when && then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(requestMenu));
        }

        @DisplayName("만약에 가격이 단일 음식들의 총 금액보다 비쌀 경우 IllegalArgumentException 예외가 발생한다.")
        @Test
        void createMenuExpensiveMoreThenSumPriceOfMenuProducts(){
            //given
            BigDecimal price = 마라탕메뉴.getProduct().getPrice().add(콜라메뉴.getProduct().getPrice());

            Menu requestMenu = new Menu();
            requestMenu.setMenuGroup(추천메뉴);
            requestMenu.setMenuGroupId(추천메뉴.getId());
            requestMenu.setName("나홀로세트");
            requestMenu.setPrice(price.add(BigDecimal.valueOf(1000)));
            requestMenu.setMenuProducts(Arrays.asList(마라탕메뉴, 콜라메뉴));
            requestMenu.setDisplayed(true);
            //when && then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.create(requestMenu));
        }
    }

    @DisplayName("메뉴의 가격을 변경한다.")
    @Test
    void changePriceOfMenu(){
        //given && when
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(17000));
        Menu changeMenu = menuService.changePrice(마라세트.getId(), request);

        //then
        assertThat(changeMenu.getPrice()).isSameAs(request.getPrice());
    }

    @Nested
    @DisplayName("가격을 변경하려는 메뉴가 ")
    class changePriceMenuExceptionTestCase {
        @DisplayName("만약에 입력한 가격이 없거나 음수인 경우 IllegalArgumentException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {0, -1000})
        void changeNoPriceOfMenu(Integer price){
            //given && when
            Menu request = new Menu();
            request.setPrice((price.equals(0) ? null : BigDecimal.valueOf(price)));

            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.changePrice(마라세트.getId(), request));
        }

        @DisplayName("만약에 미리 등록되어 있지 않는 경우 NoSuchElementException 예외가 발생한다.")
        @Test
        void changePriceOfNoMenu(){
            //given && when
            Menu 없는메뉴 = new Menu();
            없는메뉴.setId(UUID.randomUUID());

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(10000));

            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.changePrice(없는메뉴.getId(), request));
        }

        @DisplayName("만약에 입력한 가격이 단일 음식들 종 금액보다 비싼 경우 IllegalArgumentException 예외가 발생한다.")
        @Test
        void changePriceExpensiveMoreThenSumPriceOfMenuProducts(){
            //given && when
            BigDecimal totalPriceOfProducts = 마라세트.getMenuProducts().stream()
                    .map(MenuProduct::getProduct)
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Menu request = new Menu();
            request.setPrice(totalPriceOfProducts.add(BigDecimal.valueOf(1000)));

            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuService.changePrice(마라세트.getId(), request));
        }
    }

    @DisplayName("특정 메뉴의 노출 상태를 판매중으로 변경한다.")
    @Test
    void display(){
        //given && when
        Menu displayMenu = menuService.display(마라세트.getId());

        //then
        assertThat(displayMenu.isDisplayed()).isSameAs(true);
    }

    @DisplayName("특정 메뉴의 노출 상태를 판매중단으로 변경한다.")
    @Test
    void hide(){
        //given && when
        Menu hideMenu = menuService.hide(마라세트.getId());

        //then
        assertThat(hideMenu.isDisplayed()).isSameAs(false);
    }

    @Nested
    @DisplayName("노출 상태를 변경하려는 메뉴가 ")
    class changeDisplayMenuExceptionTestCase {
        @DisplayName("만약에 등록되어 있지 않는 메뉴인 경우 NoSuchElementException 예외가 발생한다. (판매중)")
        @Test
        void displayOfNoMenu(){
            //given && when
            Menu 없는메뉴 = new Menu();
            없는메뉴.setId(UUID.randomUUID());

            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.display(없는메뉴.getId()));
        }

        @DisplayName("만약에 등록되어 있지 않는 메뉴인 경우 NoSuchElementException 예외가 발생한다. (판매중단)")
        @Test
        void hideOfNoMenu(){
            //given && when
            Menu 없는메뉴 = new Menu();
            없는메뉴.setId(UUID.randomUUID());

            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> menuService.hide(없는메뉴.getId()));
        }

        @DisplayName("만약에 단일 음식 가격의 총 금액보다 비쌀 경우 IllegalArgumentException 예외가 발생한다.")
        @Test
        void displayMenuExpensiveMoreThenSumPriceOfMenuProducts(){
            //given && when
            List<MenuProduct> menuProducts = Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴);

            BigDecimal totalPriceOfProducts = menuProducts.stream()
                    .map(MenuProduct::getProduct)
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            Menu 단일음식총금액보다비싼메뉴 = MenuTestHelper.메뉴_생성(추천메뉴, "단일음식총금액보다비싼메뉴", totalPriceOfProducts.add(BigDecimal.valueOf(1000)), Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴), false);
            //then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> menuService.display(단일음식총금액보다비싼메뉴.getId()));
        }
    }

    @DisplayName("모든 메뉴 리스트를 조회한다.")
    @Test
    void findAll(){
        //given && when
        List<Menu> menus = menuService.findAll();

        //then
        assertThat(menus.size()).isSameAs(메뉴들.size());
    }
}
