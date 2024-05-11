package kitchenpos.acceptance.product

import io.kotest.matchers.shouldBe
import kitchenpos.acceptance.CommonAcceptanceTest
import org.springframework.boot.test.web.server.LocalServerPort

class ProductAcceptanceTest(
    @LocalServerPort
    override var port: Int,
) : CommonAcceptanceTest() {
    init {
        given("가게 사장님이 로그인한 뒤에") {
            `when`("상품을 생성하면") {
                then("상품이 생성된다.") {
                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                    "name": "후라이드 치킨",
                                    "price": 16000,
                                }
                                """.trimIndent(),
                            )
                            .post("/api/products")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("name") shouldBe "후라이드 치킨"
                    jsonPathEvaluator.getInt("price") shouldBe 16000
                    response.statusCode shouldBe 201
                }
            }
        }
    }
}
