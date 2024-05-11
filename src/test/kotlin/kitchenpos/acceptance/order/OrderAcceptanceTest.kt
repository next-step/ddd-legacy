package kitchenpos.acceptance.order

import domain.MenuFixtures.makeMenuOne
import domain.MenuGroupFixtures.makeMenuGroupOne
import domain.ProductFixtures.makeProductOne
import io.kotest.matchers.shouldBe
import kitchenpos.acceptance.CommonAcceptanceTest
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.ProductRepository
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.transaction.annotation.Transactional

@Transactional
class OrderAcceptanceTest(
    @LocalServerPort
    override val port: Int,
    private val menuRepository: MenuRepository,
    private val menuGroupRepository: MenuGroupRepository,
    private val productRepository: ProductRepository,
) : CommonAcceptanceTest() {
    init {

        given("고객이 로그인한 뒤에") {
            `when`("주문하기 버튼을 누르고, 결제를 완료하면") {
                then("주문이 생성된다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())

                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                    "type": "DELIVERY",
                                    "orderLineItems": [
                                        {
                                            "menuId": "9625e053-28d8-49e6-9b43-bef6f9af4c3b",
                                            "quantity": 1,
                                            "price": 16000
                                        }
                                    ],
                                    "deliveryAddress": "서울시 강남구"
                                }
                                """.trimIndent(),
                            )
                            .post("/api/orders")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("orderLineItems[0].menu.id") shouldBe "9625e053-28d8-49e6-9b43-bef6f9af4c3b"
                    jsonPathEvaluator.getInt("orderLineItems[0].quantity") shouldBe 1
                    jsonPathEvaluator.getFloat("orderLineItems[0].menu.price") shouldBe 16000.00f
                    jsonPathEvaluator.getString("deliveryAddress") shouldBe "서울시 강남구"
                    jsonPathEvaluator.getString("status") shouldBe "WAITING"

                    response.statusCode shouldBe 201
                }
            }
        }
    }
}
