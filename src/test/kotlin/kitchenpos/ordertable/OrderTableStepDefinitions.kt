package kitchenpos.ordertable

import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import kitchenpos.domain.OrderTable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before

class OrderTableStepDefinitions {
    private var orderTable: OrderTable = OrderTableFixture.fixture()

    @Before
    fun setUp() {
        orderTable = OrderTableFixture.fixture()
    }

    @ParameterType("점유|비점유")
    fun occupied(occupied: String): Boolean {
        return occupied == "점유"
    }

    @ParameterType(".*")
    fun inputName(name: String?): String? {
        return if (name == "null") null else name
    }

    @Given("가게 테이블이 존재한다  이름 : {string} 손님수 : {int}명")
    fun 가게테이블_존재(name: String, numberOfGuests: Int) {
        orderTable = OrderTableFixture.fixture(name = name, numberOfGuests = numberOfGuests)
        orderTable = RestAssured
            .given().body(orderTable).contentType("application/json")
            .`when`().post("/api/order-tables")
            .then().extract().`as`(OrderTable::class.java)
    }

    @Given("가게 테이블 이름을 {inputName}로 입력한다")
    fun 가게테이블_이름_입력(name: String?) {
        orderTable.name = name
    }

    @When("가게 테이블을 생성요청")
    fun 가게테이블_생성_요청() {
        RestAssured
            .given().body(orderTable).contentType("application/json")
            .`when`().post("/api/order-tables")
    }

    @When("가게 테이블 점유")
    fun 가게테이블_점유() {
        RestAssured
            .put("/api/order-tables/${orderTable.id}/sit")
    }

    @When("가게 테이블 비점유")
    fun 가게테이블_비점유() {
        RestAssured
            .put("/api/order-tables/${orderTable.id}/clear")
    }

    @When("가게 테이블의 손님수를 {int}명으로 변경")
    fun 가게테이블_손님수_변경(numberOfGuests: Int) {
        orderTable.numberOfGuests = numberOfGuests
        RestAssured
            .given().body(orderTable).contentType("application/json")
            .`when`().put("/api/order-tables/${orderTable.id}/number-of-guests")
    }

    @Then("가게 테이블이 생성성공")
    fun 가게테이블_생성됨() {
        val orderTables = RestAssured
            .get("/api/order-tables")
            .then().extract().`as`(Array<OrderTable>::class.java)
        assertThat(orderTables).hasSize(1)
    }

    @Then("가게 테이블 생성실패")
    fun 가게테이블_생성실패() {
        val orderTables = RestAssured
            .get("/api/order-tables")
            .then().extract().`as`(Array<OrderTable>::class.java)
        assertThat(orderTables).hasSize(0)
    }

    @Then("현재 가게 테이블 상태  이름 : {string} 손님수 : {int}명 점유여부 : {occupied}")
    fun 현재_가게테이블_상태(name: String, numberOfGuests: Int, occupied: Boolean) {
        val orderTables = RestAssured
            .get("/api/order-tables")
            .then().extract().`as`(Array<OrderTable>::class.java)
        assertThat(orderTables).hasSize(1)
        assertThat(orderTables[0].name).isEqualTo(name)
        assertThat(orderTables[0].numberOfGuests).isEqualTo(numberOfGuests)
        assertThat(orderTables[0].isOccupied).isEqualTo(occupied)
    }
}

