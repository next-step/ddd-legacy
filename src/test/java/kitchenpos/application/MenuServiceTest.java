package kitchenpos.application;

import kitchenpos.config.MenuTestContextConfiguration;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.helper.MenuGroupTestHelper.메뉴카테고리_생성;
import static kitchenpos.helper.MenuProductTestHelper.음식메뉴_생성;
import static kitchenpos.helper.MenuTestHelper.메뉴_생성;
import static kitchenpos.helper.ProductTestHelper.음식_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@Import(MenuTestContextConfiguration.class)
class MenuServiceTest {
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private ProductRepository productRepository;
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
        추천메뉴 = 메뉴카테고리_생성("추천메뉴");
        마라탕 = 음식_생성("마라탕", BigDecimal.valueOf(10000));
        미니꿔바로우 = 음식_생성("미니꿔바로우", BigDecimal.valueOf(8000));
        콜라 = 음식_생성("콜라", BigDecimal.valueOf(3000));

        마라탕메뉴 = 음식메뉴_생성(마라탕, 1);
        미니꿔바로우메뉴 = 음식메뉴_생성(미니꿔바로우, 1);
        콜라메뉴 = 음식메뉴_생성(콜라, 1);

        마라세트 = 메뉴_생성(추천메뉴, "마라세트", BigDecimal.valueOf(16000), Arrays.asList(마라탕메뉴, 미니꿔바로우메뉴));

        메뉴들.add(마라세트);

        Mockito.when(menuGroupRepository.findById(추천메뉴.getId()))
                .thenReturn(Optional.of(추천메뉴));

        Mockito.when(productRepository.findById(마라탕.getId()))
                .thenReturn(Optional.of(마라탕));

        Mockito.when(productRepository.findById(미니꿔바로우.getId()))
                .thenReturn(Optional.of(미니꿔바로우));

        Mockito.when(productRepository.findById(콜라.getId()))
                .thenReturn(Optional.of(콜라));

        Mockito.when(productRepository.findAllByIdIn(Arrays.asList(마라탕.getId(), 콜라.getId())))
                .thenReturn(Arrays.asList(마라탕, 콜라));

        Mockito.when(productRepository.findAllByIdIn(Arrays.asList(마라탕.getId(), 미니꿔바로우.getId())))
                .thenReturn(Arrays.asList(마라탕, 미니꿔바로우));

        Mockito.when(menuRepository.findById(마라세트.getId()))
                .thenReturn(Optional.of(마라세트));

        Mockito.when(menuRepository.findAll())
                .thenReturn(메뉴들);
    }

    @DisplayName("특정 메뉴카테고리에 음식메뉴를 조합한 신규 메뉴를 추가한다.")
    @Test
    void createMenu(){
        //given
        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(11000), Arrays.asList(마라탕메뉴, 콜라메뉴));

        Mockito.when(menuRepository.save(any()))
                .thenReturn(requestMenu);

        //when
        Menu createMenu = menuService.create(requestMenu);

        //then
        assertThat(createMenu.getName()).isSameAs(requestMenu.getName());
    }

    @DisplayName("메뉴에 가격이 없는 경우 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createMenuOfNoPrice(){
        //given
        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", null, Arrays.asList(마라탕메뉴, 콜라메뉴));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("메뉴에 가격이 음수인 경우 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createMenuOfMinusPrice(){
        //given
        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(-1000), Arrays.asList(마라탕메뉴, 콜라메뉴));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("메뉴에 등록되지 않은 음식메뉴를 넣을 경우 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createMenuOfNoProductMenu(){
        //given
        MenuProduct 없는음식메뉴 = 음식메뉴_생성(new Product(), 1);
        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(10000), Arrays.asList(없는음식메뉴));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("등록하려는 메뉴 조합에서의 단일 음식 수량이 음수인 경우 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createMenuOfNoQurantityProductMenu(){
        //given
        콜라메뉴.setQuantity(-1);

        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(10000), Arrays.asList(마라탕메뉴, 콜라메뉴));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("등록되지 않은 음식으로 메뉴조합을 만들어 등록하려는 경우 NoSuchElementException 예외가 발생한다.")
    @Test
    void createMenuOfNoProduct(){
        //given
        Product 없는음식 = new Product();
        MenuProduct 없는음식메뉴 = 음식메뉴_생성(없는음식, 1);

        Mockito.when(productRepository.findAllByIdIn(any()))
                .thenReturn(Arrays.asList(마라탕, 없는음식));

        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", BigDecimal.valueOf(10000), Arrays.asList(마라탕메뉴, 없는음식메뉴));

        //when && then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("등록하려는 메뉴 조합에서의 가격이 단일 음식들의 총금액보다 비쌀 경우 IllegalArgumentException 예외가 발생한다.")
    @Test
    void createMenuExpensiveMoreThenSumPriceOfMenuProducts(){
        //given
        BigDecimal price = 마라탕메뉴.getProduct().getPrice().add(콜라메뉴.getProduct().getPrice());
        Menu requestMenu = 메뉴_생성(추천메뉴, "나홀로세트", price.add(BigDecimal.valueOf(1000)), Arrays.asList(마라탕메뉴, 콜라메뉴));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("등록하려는 메뉴 이름에 비속어가 있는 경우 IllegalArgumentException 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "바보메뉴"})
    void createMenuNameEmptyOfIncluedProfanity(String name){
        //given
        String paramName = (name.equals("") ? null : name);
        Menu requestMenu = 메뉴_생성(추천메뉴, paramName, BigDecimal.valueOf(11000), Arrays.asList(마라탕메뉴, 콜라메뉴));

        //when && then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(requestMenu));
    }

    @DisplayName("특정 메뉴의 가격을 변경한다.")
    @Test
    void changePrice(){
        //given && when
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(17000));
        Menu changeMenu = menuService.changePrice(마라세트.getId(), request);

        //then
        assertThat(changeMenu.getPrice()).isSameAs(request.getPrice());
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

    @DisplayName("모든 메뉴 리스트를 조회한다.")
    @Test
    void findAll(){
        //given && when
        List<Menu> menus = menuService.findAll();

        //then
        assertThat(menus.size()).isSameAs(메뉴들.size());
    }
}