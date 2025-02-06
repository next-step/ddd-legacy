package kitchenpos.product

import io.restassured.RestAssured
import kitchenpos.domain.Product


class ProductHelper {
    companion object {
        fun 상품이름으로_상품_조회(name: String): Product {
            return RestAssured
                .get("/api/products")
                .then().log().all().extract().`as`(Array<Product>::class.java)
                .find { it.name == name }!!
        }
    }
}
