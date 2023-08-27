package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.objectmother.MenuGroupMaker;
import kitchenpos.objectmother.MenuMaker;
import kitchenpos.objectmother.MenuProductMaker;
import kitchenpos.objectmother.ProductMaker;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private MenuService menuService;

    private MenuGroup 메뉴그룹;
    private Product 상품_1;
    private Product 상품_2;
    private MenuProduct 메뉴상품_1;
    private MenuProduct 메뉴상품_2;
    private MenuProduct 수량음수상품;

    @BeforeEach
    void setUp() {
        메뉴그룹 = menuGroupRepository.save(MenuGroupMaker.make("메뉴그룹"));
        상품_1 = productRepository.save(ProductMaker.make("상품1", 1500L));
        상품_2 = productRepository.save(ProductMaker.make("상품2", 3000L));
        메뉴상품_1 = MenuProductMaker.make(상품_1, 2);
        메뉴상품_2 = MenuProductMaker.make(상품_2, 5);
        수량음수상품 = MenuProductMaker.make(상품_2, -3);
    }

    @DisplayName("메뉴생성시 요청한 데이터로 메뉴가 생성되야 한다.")
    @Test
    void 메뉴생성() {
        // given
        Menu menu = MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when
        Menu saveMenu = menuService.create(menu);

        // then
        assertThat(saveMenu.getName()).isEqualTo(menu.getName());
        assertThat(saveMenu.getPrice()).isEqualTo(menu.getPrice());
        assertThat(saveMenu.isDisplayed()).isTrue();
        assertThat(saveMenu.getMenuGroup())
                .extracting(MenuGroup::getName)
                .isEqualTo(메뉴그룹.getName());
        assertThat(saveMenu.getMenuProducts())
                .hasSize(2)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    @DisplayName("메뉴생성시 메뉴가격이 음수일경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_가격_음수() {
        // given
        Menu menu = MenuMaker.make("메뉴", -15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시 메뉴상품에 수량이 0보다 작을경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_메뉴상품수량_음수() {
        // given
        Menu menu = MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 수량음수상품);

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시 메뉴이름에 욕설이 포함된경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_메뉴이름_욕설포함() {
        // given
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);
        Menu menu = MenuMaker.make("Fuck메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴생성시 메뉴이름에 욕설이 포함된경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_메뉴가격_메뉴상품총가격_초과() {
        // given
        Menu menu = MenuMaker.make("메뉴", 20000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가격 변경시 변경된 메뉴가격이 조회되야 한다.")
    @Test
    void 가격변경() {
        // given
        Menu menu = menuService.create(
                MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2)
        );

        // when
        Menu priceMenu = menuService.changePrice(menu.getId(),
                MenuMaker.make("메뉴", 3000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // then
        assertThat(priceMenu.getPrice()).isEqualTo(new BigDecimal(3000L));
    }

    @DisplayName("메뉴가격 변경시 메뉴가격이 메뉴상품총가격보다 클 경우 에러를 던진다.")
    @Test
    void 가격변경실패_메뉴가격_메뉴상품총가격_초과() {
        // given
        Menu menu = menuService.create(
                MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2)
        );

        // when then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(),
                MenuMaker.make("메뉴", 5000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴노출 시 메뉴에 노출여부 상태가 노출로 변경된다.")
    @Test
    void 메뉴노출() {
        // given
        Menu menu = menuService.create(
                MenuMaker.make("메뉴", 3000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2)
        );

        // when
        Menu displayMenu = menuService.display(menu.getId());

        // then
        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴노출 시 메뉴가격이 메뉴상품총가격을 넘을경우 에러를 던진다.")
    @Test
    void 메뉴노출실패_메뉴가격_메뉴상품총가격_초과() {
        // given
        Menu menu = menuService.create(
                MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2)
        );

        // when then
        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴비노출 시 메뉴에 노출여부 상태가 비노출로 변경된다.")
    @Test
    void 메뉴비노출() {
        // given
        Menu menu = menuService.create(
                MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2)
        );

        // when
        Menu hideMenu = menuService.hide(menu.getId());

        // then
        assertThat(hideMenu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴전체조회시 지금까지 등록된 메뉴가 전체조회되야 한다.")
    @Test
    void 메뉴전체조회() {
        // given
        menuService.create(MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
        menuService.create(MenuMaker.make("메뉴2", 12000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertThat(menus)
                .hasSize(2)
                .extracting(Menu::getName, Menu::getPrice, Menu::isDisplayed)
                .containsExactlyInAnyOrder(
                        Tuple.tuple("메뉴", new BigDecimal(15000L), true),
                        Tuple.tuple("메뉴2", new BigDecimal(12000L), true)
                );

        assertThat(menus)
                .extracting(Menu::getMenuGroup)
                .extracting(MenuGroup::getName)
                .containsExactly(메뉴그룹.getName(), 메뉴그룹.getName());

        assertThat(menus)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice()),
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

}