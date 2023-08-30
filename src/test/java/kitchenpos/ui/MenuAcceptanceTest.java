package kitchenpos.ui;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.setup.MenuGroupSetup;
import kitchenpos.setup.MenuSetup;
import kitchenpos.setup.ProductSetup;
import kitchenpos.util.AcceptanceTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static kitchenpos.fixture.MenuFixture.generateMenu;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroup;
import static kitchenpos.fixture.ProductFixture.generateProduct;
import static kitchenpos.fixture.ProductFixture.generateProductWithPrice;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;


class MenuAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ProductSetup productSetup;

    @Autowired
    private MenuGroupSetup menuGroupSetup;

    @Autowired
    private MenuSetup menuSetup;

    @DisplayName("메뉴를 생성한다")
    @Test
    void createMenuGroup() {
        // given
        final Product product = productSetup.setupProduct(generateProduct());
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = generateMenu(product, quantity, menuGroup);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body((menu))
                .when()
                .post(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value())
                .assertThat()
                .body("name", equalTo(menu.getName()))
                .body("price", equalTo(menu.getPrice().intValue()))
                .body("menuGroup.name", equalTo(menuGroup.getName()))
                .body("displayed", equalTo(menu.isDisplayed()))
                .body("menuProducts[0].product.name", equalTo(product.getName()))
                .body("menuProducts[0].product.price", equalTo(product.getPrice().floatValue()))
                .body("menuProducts[0].quantity", equalTo(quantity))
        ;
    }

    @DisplayName("메뉴에 포함될 상품의 수량은 0개 이상이어야 한다")
    @Test
    void negativeQuantity() {
        // given
        final Product product = productSetup.setupProduct(generateProduct());
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = -1;
        final Menu menu = generateMenu(product, quantity, menuGroup);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body((menu))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("메뉴 가격은 0원 이상이다.")
    @Test
    void negativeMenuPrice() {
        // given
        final Product product = productSetup.setupProduct(generateProduct());
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 0;
        final Menu menu = generateMenu(product, quantity, menuGroup);
        menu.setPrice(BigDecimal.valueOf(-1));

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body((menu))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("메뉴 가격은 메뉴에 포함된 상품들의 총가격(단가 * 수량) 보다 작아야 한다")
    @Test
    void invalidMenuPrice() {
        // given
        final Product product = productSetup.setupProduct(generateProduct());
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = generateMenu(product, quantity, menuGroup);
        menu.setPrice(product.getPrice().add(BigDecimal.valueOf(1)));

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body((menu))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("메뉴 그룹을 반드시 명시한다")
    @Test
    void menuGroupMustExist() {
        // given
        final Product product = productSetup.setupProduct(generateProduct());
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = generateMenu(product, quantity, menuGroup);
        menu.setMenuGroup(null);
        menu.setMenuGroupId(null);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(menu))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("메뉴의 가격을 바꿀 수 있다")
    @Test
    void changeMenuPrice() {
        // given
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = menuSetup.setupMenu(generateMenu(product, quantity, menuGroup));

        final BigDecimal newPrice = product.getPrice().subtract(BigDecimal.valueOf(100));
        menu.setPrice(newPrice);

        // expected
        final String path = getPath() +
                "/" +
                menu.getId().toString() +
                "/price";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(menu))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .body("price", equalTo(newPrice.intValue()))
        ;
    }

    @Disabled
    @DisplayName("변경된 메뉴 가격이 메뉴에 포함된 상품들의 총가격(단가 * 수량) 보다 작으면 노출하지 않는다")
    @Test
    void invisibleMenuAfterChangePriceOfMenu() {
        // given
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = menuSetup.setupMenu(generateMenu(product, quantity, menuGroup));

        assert menu.isDisplayed();

        final BigDecimal newPrice = product.getPrice().add(BigDecimal.valueOf(100));
        menu.setPrice(newPrice);

        // expected
        final String path = getPath() +
                "/" +
                menu.getId().toString() +
                "/price";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(menu))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .body("price", equalTo(newPrice.intValue()))
                .body("displayed", equalTo(false));
        ;
    }

    @DisplayName("메뉴를 노출한다")
    @Test
    void changeDisplayed() {
        // given
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = menuSetup.setupMenu(generateMenu(product, quantity, menuGroup, BigDecimal.valueOf(9_000), false));

        // expected
        final String path = getPath() +
                "/" +
                menu.getId().toString() +
                "/display";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(menu))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .body("displayed", is(true))
        ;
    }

    @DisplayName("메뉴에 포함된 상품들의 총가격(단가 * 수량) 보다 메뉴 가격이 클 때는 메뉴를 노출할 수 없다")
    @Test
    void validChangeDisplayed() {
        // given
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = menuSetup.setupMenu(generateMenu(product, quantity, menuGroup, BigDecimal.valueOf(11_000), false));

        // expected
        final String path = getPath() +
                "/" +
                menu.getId().toString() +
                "/display";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(menu))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("메뉴를 숨긴다")
    @Test
    void hideMenu() {
        // given
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final int quantity = 1;
        final Menu menu = menuSetup.setupMenu(generateMenu(product, quantity, menuGroup, BigDecimal.valueOf(9_000), true));

        // expected
        final String path = getPath() +
                "/" +
                menu.getId().toString() +
                "/hide";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(menu))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .body("displayed", equalTo(false));
        ;
    }

    @DisplayName("메뉴 목록을 조회한다")
    @Test
    void getMenus() {
        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @Override
    protected String getPath() {
        return "/api/menus";
    }
}