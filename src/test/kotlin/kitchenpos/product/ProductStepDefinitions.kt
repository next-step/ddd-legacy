package kitchenpos.product

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import kitchenpos.domain.Product
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal


class ProductStepDefinitions : CucumberTest() {
    private var product: Product = ProductFixture.fixture()

    @Before
    fun setUp() {
        product = ProductFixture.fixture()
    }

    @Given("상품 이름은 {productName} 이고 상품 가격은 {int}원인 상품이 등록되어 있다")
    fun 상품생성(name: String?, price: Int) {
        product = ProductFixture.fixture(name = name, price = price.toBigDecimal())
        RestAssured
            .given().body(product).contentType("application/json")
            .`when`().post("/api/products")
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

    @When("상품 이름이 {productName}인 상품의 가격을 {int}원으로 수정한다")
    fun 상품이름은string이고상품가격은int원으로수정한다(name: String?, price: Int) {
        val product = 상품이름으로_상품_조회(name!!)
        product.price = price.toBigDecimal()
        RestAssured
            .given().body(product).contentType("application/json")
            .`when`().put("/api/products/${product.id}/price")
    }

    @Then("상품 이름은 {productName} 이고 상품 가격은 {int}원이다")
    fun 상품이름은string이고상품가격은int원이다(name: String?, price: Int) {
        val product = 상품이름으로_상품_조회(name!!)
        assertThat(product.name).isEqualTo(name)
        assertThat(product.price)
            .usingComparator(BigDecimal::compareTo)
            .isEqualTo(price.toBigDecimal())
    }

    @Then("상품 생성 실패")
    fun 상품생성실패() {
        val products = RestAssured
            .get("/api/products")
            .then().extract().`as`(Array<Product>::class.java)
        assertThat(products).hasSize(0)
    }

    private fun 상품이름으로_상품_조회(name: String): Product {
        return RestAssured
            .get("/api/products")
            .then().log().all().extract().`as`(Array<Product>::class.java)
            .find { it.name == name }!!
    }
}
