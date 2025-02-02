package kitchenpos.product

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kitchenpos.application.ProductService
import kitchenpos.domain.Product
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.springframework.beans.factory.annotation.Autowired


class CreateProductDefs : CucumberTest() {
    @Autowired
    private lateinit var productService: ProductService

    private var product: Product = ProductFixture.fixture()

    @Given("상품 이름은 {productName} 이고 상품 가격은 {int}")
    fun 상품이름은string이고상품가격은int(name: String?, price: Int) {
        product = ProductFixture.fixture(name = name, price = price.toBigDecimal())
    }

    @Then("상품 생성 성공")
    fun 상품생성성공() {
        productService.create(product)
        assertThat(productService.findAll()).hasSize(1)
    }

    @Then("상품 생성 실패")
    fun 상품생성실패() {
        assertThatIllegalArgumentException()
            .isThrownBy { productService.create(product) }
    }
}
