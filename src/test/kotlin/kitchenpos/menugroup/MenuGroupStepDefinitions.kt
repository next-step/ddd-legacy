package kitchenpos.menugroup

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import kitchenpos.domain.MenuGroup
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before

class MenuGroupStepDefinitions : CucumberTest() {
    private var menuGroup: MenuGroup = MenuGroupFixture.fixture()

    @Before
    fun setUp() {
        menuGroup = MenuGroupFixture.fixture()
    }

    @Given("메뉴 그룹 이름은 {string}")
    fun 메뉴그룹이름은String이다(name: String?) {
        menuGroup = MenuGroupFixture.fixture(name = name)
    }

    @When("메뉴 그룹 생성 요청")
    fun 메뉴그룹생성요청() {
        RestAssured
            .given().body(menuGroup).contentType(ContentType.JSON)
            .`when`().post("/api/menu-groups")
    }

    @Then("메뉴 그룹 생성 성공")
    fun 메뉴그룹생성성공() {
        val menuGroups = RestAssured
            .get("/api/menu-groups")
            .then().extract().`as`(Array<MenuGroup>::class.java)
        assertThat(menuGroups).hasSize(1)
    }

    @Then("메뉴 그룹 생성 실패")
    fun 메뉴그룹생성실패() {
        val menuGroups = RestAssured
            .get("/api/menu-groups")
            .then().extract().`as`(Array<MenuGroup>::class.java)
        assertThat(menuGroups).hasSize(0)
    }
}
