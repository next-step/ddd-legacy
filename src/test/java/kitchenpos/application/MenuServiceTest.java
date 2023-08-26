package kitchenpos.application;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.testHelper.SpringBootTestHelper;
import kitchenpos.testHelper.fake.PurgomalumClientFake;
import kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum;
import kitchenpos.testHelper.fixture.MenuFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest extends SpringBootTestHelper {

    @Autowired
    MenuService menuService;
    @Autowired
    ProductService productService;
    @Autowired
    MenuGroupService menuGroupService;
    @Autowired
    PurgomalumClientFake purgomalumClient;
    List<Product> products;
    MenuGroup menuGroup;

    @BeforeEach
    public void init() {
        super.init();
        initProduct();
        initMenuGroup();
    }

    private void initMenuGroup() {
        this.menuGroup = menuGroupService.create(new MenuGroup("메뉴그룹"));
    }

    private void initProduct() {
        purgomalumClient.setReturn(Purgomalum.NORMAL);
        List<Product> requests = List.of(
            new Product("P1", BigDecimal.valueOf(1000L)),
            new Product("P2", BigDecimal.valueOf(2000L)),
            new Product("P3", BigDecimal.valueOf(3000L))
        );
        for (Product request : requests) {
            productService.create(request);
        }

        this.products = productService.findAll();
    }

    @DisplayName("메뉴의 가격은 0원 이상이여야 한다")
    @ParameterizedTest
    @ValueSource(longs = {-1, -2, -3})
    void test1(long price) {
        //given
        Menu request = new Menu();
        request.setPrice(valueOf(price));

        //when && then
        assertThatThrownBy(
            () -> menuService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할수 있다")
    @Test
    void test2() {
        //given
        Menu menuRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 1L)
            .menuProduct(products.get(1), 2L)
            .name("menuName")
            .build();

        //when
        Menu result = menuService.create(menuRequest);

        //then
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("메뉴는 반드시 메뉴그룹이 존재해야 한다")
    @Test
    void test3() {
        //given
        UUID notSavedMenuGroupUuid = UUID.randomUUID();
        Menu menuRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(notSavedMenuGroupUuid)
            .menuProduct(products.get(0), 1L)
            .menuProduct(products.get(1), 2L)
            .name("menuName")
            .build();

        //when && then
        assertThatThrownBy(
            () -> menuService.create(menuRequest)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("없는 상품은 메뉴에 추가할수 없다")
    @Test
    void test4() {
        //given
        Product notSavedProduct = new Product("NOT_SAVED_PRODUCT", valueOf(1000));
        Menu menuRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 1L)
            .menuProduct(products.get(1), 2L)
            .menuProduct(notSavedProduct, 1L)
            .name("menuName")
            .build();

        //when && then
        assertThatThrownBy(
            () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 최소 하나 이상의 상품을 가지고 있어야 한다")
    @Test
    void test5() {
        //given
        Menu menuRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .name("menuName")
            .build();

        //when && then
        assertThatThrownBy(
            () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함된 상품은 각각 0개 이상 등록해야 한다")
    @Test
    void test6() {
        //given
        Menu menuRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), -1L)
            .name("menuName")
            .build();

        //when && then
        assertThatThrownBy(
            () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 포함된 상품들의 총가격(단가 * 수량)보다 클수 없다")
    @Test
    void test7() {
        //given
        Menu menuRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 1L)
            .menuProduct(products.get(1), 1L)
            .name("menuName")
            .addPrice(1L)
            .build();

        //when && then
        assertThatThrownBy(
            () -> menuService.create(menuRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격을 변경할수 있다")
    @Test
    void test8() {
        //given 0번 상품을 2개로 묶은 메뉴를 생성한다.
        Menu createRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 2L)
            .name("menuMame")
            .build();
        Menu savedMenu = menuService.create(createRequest);
        //given 0번 상품을 반값 행사를 할수 있게 Menu의 가격을 절반으로 변경한다.
        Menu updateRequest = MenuFixture.updateRequestBuilder()
            .price(savedMenu.getPrice().longValue() / 2)
            .build();

        //when
        Menu result = menuService.changePrice(savedMenu.getId(), updateRequest);

        //then
        assertThat(result.getPrice()).isEqualTo(updateRequest.getPrice());
    }

    @DisplayName("변경된 메뉴 가격 또한 포함된 상품들의 총가격(단가 * 수량)보다 클수 없다")
    @Test
    void test9() {
        //given 0번 상품을 2개로 묶은 메뉴를 생성한다.
        Menu createRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 2L)
            .name("menuMame")
            .build();
        Menu savedMenu = menuService.create(createRequest);
        //given 0번 상품을 반값 행사를 하려고 했으나 실수로 2배 가격을 설정해버렸다.
        Menu updateRequest = MenuFixture.updateRequestBuilder()
            .price(savedMenu.getPrice().longValue() * 2)
            .build();

        //when && then
        assertThatThrownBy(
            () -> menuService.changePrice(savedMenu.getId(), updateRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격이, 포함된 상품들의 총 가격(단가 * 수량)보다 적은 메뉴는 노출시킬수 있다")
    @Test
    void test10() {
        //given 0번 상품을 2개로 묶은 메뉴를 생성한다.
        Menu createRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 2L)
            .name("menuMame")
            .build();
        Menu savedMenu = menuService.create(createRequest);

        //when
        Menu result = menuService.display(savedMenu.getId());

        //then
        assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 숨길수 있다")
    @Test
    void test11() {
        //given 0번 상품을 2개로 묶은 메뉴를 생성한다.
        Menu createRequest = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 2L)
            .name("menuMame")
            .build();
        Menu savedMenu = menuService.create(createRequest);

        //when
        Menu result = menuService.hide(savedMenu.getId());

        //then
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴의 모든 정보를 조회할수 있다")
    @Test
    void test12() {
        //given
        //given 0번 상품을 2개로 묶은 메뉴를 생성한다.
        Menu createRequest1 = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(0), 1L)
            .name("menu1")
            .build();
        Menu createRequest2 = MenuFixture.createRequestBuilder()
            .menuGroupId(menuGroup.getId())
            .menuProduct(products.get(1), 1L)
            .name("menu2")
            .build();
        Menu savedMenu1 = menuService.create(createRequest1);
        Menu savedMenu2 = menuService.create(createRequest2);

        //when
        List<Menu> menus = menuService.findAll();

        //then
        assertThat(menus).extracting("id")
            .containsExactly(savedMenu1.getId(), savedMenu2.getId());
    }
}