package kitchenpos.menu

import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.RestAssured.get
import io.restassured.http.ContentType
import kitchenpos.domain.Menu
import kitchenpos.menu.MenuHelper.Companion.메뉴_이름으로_메뉴_조회
import kitchenpos.menugroup.MenuGroupHelper.Companion.메뉴그룹_이름으로_메뉴_그룹_조회
import kitchenpos.product.ProductHelper.Companion.상품이름으로_상품_조회
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import java.math.BigDecimal


class MenuStepDefinitions : CucumberTest() {
    private var menu = MenuFixture.fixture()

    @Before
    fun setUp() {
        menu = MenuFixture.fixture()
    }

    @ParameterType("활성화|비활성화")
    fun menuDisplayed(displayed: String): Boolean {
        return displayed == "활성화"
    }

    @Given("메뉴 이름은 {string}이고 가격은 {int}원이다")
    fun 메뉴생성_입력(name: String, price: Int) {
        menu = MenuFixture.fixture(name = name, price = price, displayed = true)
    }

    @Given("메뉴의 메뉴그룹은 {string}이다")
    fun 메뉴그룹추가(name: String) {
        val menuGroup = 메뉴그룹_이름으로_메뉴_그룹_조회(name)
        menu.menuGroupId = menuGroup.id
    }

    @Given("메뉴의 상품으로 {string} {int}개 추가")
    fun 상품추가(name: String, quantity: Long) {
        val product = 상품이름으로_상품_조회(name)
        if (menu.menuProducts == null) {
            menu.menuProducts = mutableListOf()
        }
        menu.menuProducts.add(
            MenuProductFixture.fixture(
                seq = menu.menuProducts.size.toLong() + 1,
                product = product,
                quantity = quantity
            )
        )
    }

    @When("메뉴 이름이 {string}인 메뉴의 가격을 {int}원으로 수정한다")
    fun 메뉴_가격_수정(name: String, price: Int) {
        val findMenu = 메뉴_이름으로_메뉴_조회(name)
        findMenu.price = price.toBigDecimal()
        RestAssured
            .given().body(findMenu).contentType(ContentType.JSON)
            .`when`().put("/api/menus/${findMenu.id}/price")
    }

    @When("메뉴 이름이 {string}인 메뉴를 {menuDisplayed}한다")
    fun 메뉴_상태_수정(name: String, displayed: Boolean) {
        val findMenu = 메뉴_이름으로_메뉴_조회(name)
        if (displayed) {
            RestAssured
                .given()
                .`when`().put("/api/menus/${findMenu.id}/display")
            return
        }
        RestAssured
            .given()
            .`when`().put("/api/menus/${findMenu.id}/hide")
    }


    @When("메뉴 생성 요청")
    fun 메뉴생성요청() {
        RestAssured
            .given().body(menu).contentType(ContentType.JSON)
            .`when`().post("/api/menus")
    }

    @Then("메뉴 생성 성공")
    fun 메뉴생성성공() {
        val menus = get("/api/menus")
            .then().extract().`as`(Array<Menu>::class.java)
        assertThat(menus).hasSize(1)
    }

    @Then("메뉴 생성 실패")
    fun 메뉴생성실패() {
        val menus = get("/api/menus")
            .then().extract().`as`(Array<Menu>::class.java)
        assertThat(menus).hasSize(0)
    }

    @Then("메뉴 이름 {string}로 조회하면 가격은 {int}원이다")
    fun 메뉴조회_확인(name: String, price: Int) {
        val findMenu = 메뉴_이름으로_메뉴_조회(name)
        assertThat(findMenu.name).isEqualTo(name)
        assertThat(findMenu.price)
            .usingComparator(BigDecimal::compareTo)
            .isEqualTo(price.toBigDecimal())
    }

    @Then("메뉴 이름 {string}로 조회하면 {menuDisplayed} 상태이다")
    fun 메뉴조회_확인(name: String, displayed: Boolean) {
        val findMenu = 메뉴_이름으로_메뉴_조회(name)
        assertThat(findMenu.name).isEqualTo(name)
        assertThat(findMenu.isDisplayed).isEqualTo(displayed)
    }
}
