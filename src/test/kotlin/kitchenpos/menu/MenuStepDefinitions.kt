package kitchenpos.menu

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.RestAssured.get
import io.restassured.http.ContentType
import kitchenpos.domain.Menu
import kitchenpos.menugroup.MenuGroupHelper.Companion.메뉴그룹_이름으로_메뉴_그룹_조회
import kitchenpos.product.ProductHelper.Companion.상품이름으로_상품_조회
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before


class MenuStepDefinitions : CucumberTest() {
    private var menu = MenuFixture.fixture()

    @Before
    fun setUp() {
        menu = MenuFixture.fixture()
    }

    @Given("메뉴 이름은 {string}이고 가격은 {int}원이다")
    fun 메뉴생성(name: String, price: Int) {
        menu = MenuFixture.fixture(name = name, price = price)
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
}
