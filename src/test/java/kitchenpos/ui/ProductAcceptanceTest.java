package kitchenpos.ui;

import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.setup.MenuGroupSetup;
import kitchenpos.setup.MenuSetup;
import kitchenpos.setup.ProductSetup;
import kitchenpos.util.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static kitchenpos.fixture.MenuFixture.generateMenu;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroup;
import static kitchenpos.fixture.ProductFixture.generateNewProductWithName;
import static kitchenpos.fixture.ProductFixture.generateProduct;
import static kitchenpos.fixture.ProductFixture.generateProductWithPrice;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.equalTo;


class ProductAcceptanceTest extends AcceptanceTest {
    @Autowired
    private MenuGroupSetup menuGroupSetup;

    @Autowired
    private MenuSetup menuSetup;

    @Autowired
    private ProductSetup productSetup;

    @DisplayName("상품을 등록한다")
    @Test
    void createProduct() {
        // given
        final Product product = generateProduct();

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .assertThat()
                .body("name", equalTo(product.getName()))
                .body("price", equalTo(product.getPrice().intValue()))
        ;
    }

    @DisplayName("상품 이름을 반드시 지정해야 한다")
    @Test
    void nullProductName() {
        // given
        final Product product = generateNewProductWithName(null);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("상품 이름에 욕설이 들어가면 안된다")
    @Test
    void profaneName() {
        // given
        final Product product = generateNewProductWithName("shit");

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("상품 가격은 0원 이상이어야 한다")
    @Test
    void negativePrice() {
        // given
        final Product product = generateProductWithPrice(BigDecimal.valueOf(-1));

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(product))
                .when()
                .post(getPath())
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("상품 가격을 변경한다")
    @Test
    void changePrice() {
        // given
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final BigDecimal newPrice = BigDecimal.valueOf(9_000);
        product.setPrice(newPrice);

        // expected
        final String path = getPath() + "/" + product.getId().toString() + "/price";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(product))
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

    @DisplayName("상품 가격을 변경할 때, 상품이 포함된 메뉴의 가격이 메뉴에 포함된 상품들의 총가격(단가 * 수량)보다 크다면 " +
            "해당 메뉴를 노출시키지 않는다.")
    @Test
    void changePriceAndMenuDisplayed() {
        // given
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final Product product = productSetup.setupProduct(generateProductWithPrice(BigDecimal.valueOf(10_000)));
        final int quantity = 1;
        final Menu menu = menuSetup.setupMenu(generateMenu(product, quantity, menuGroup, BigDecimal.valueOf(9_000), true));
        assert menu.isDisplayed();

        final BigDecimal newPrice = BigDecimal.valueOf(8_000);
        product.setPrice(newPrice);

        // when
        final String path = getPath() + "/" + product.getId().toString() + "/price";
        Response result = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(product))
                .when()
                .put(path);

        // then
        assertSoftly(softAssertions -> {
            result.then()
                    .log()
                    .all()
                    .statusCode(HttpStatus.OK.value())
                    .assertThat()
                    .body("price", equalTo(newPrice.intValue()));

            softAssertions.assertThat(menuSetup.loadMenu(menu.getId()).isDisplayed()).isFalse();
        });
    }

    @DisplayName("상품 목록을 조회한다")
    @Test
    void getAllProducts() {
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
        return "/api/products";
    }
}