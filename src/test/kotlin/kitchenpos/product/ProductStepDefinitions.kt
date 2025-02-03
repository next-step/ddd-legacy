package kitchenpos.product

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import kitchenpos.domain.Product
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat


class ProductStepDefinitions : CucumberTest() {
    private var product: Product = ProductFixture.fixture()

    @Before
    fun setUp() {
        product = ProductFixture.fixture()
    }

    @Given("상품 이름은 {productName} 이고 상품 가격은 {int}원")
    fun 상품이름은string이고상품가격은int원(name: String?, price: Int) {
        product = ProductFixture.fixture(name = name, price = price.toBigDecimal())
    }

    @When("상품 생성 요청")
    fun 상품생성요청() {
        RestAssured
            .given().body(product).contentType("application/json")
            .`when`().post("/api/products")
    }

    @Then("상품 생성 성공")
    fun 상품생성성공() {
        val products = RestAssured
            .get("/api/products")
            .then().extract().`as`(Array<Product>::class.java)
        assertThat(products).hasSize(1)
    }

    @Then("상품 생성 실패")
    fun 상품생성실패() {
        val products = RestAssured
            .get("/api/products")
            .then().extract().`as`(Array<Product>::class.java)
        assertThat(products).hasSize(0)
    }
}
