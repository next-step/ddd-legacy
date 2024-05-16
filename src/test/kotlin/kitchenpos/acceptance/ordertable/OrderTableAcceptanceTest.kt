package kitchenpos.acceptance.ordertable

import domain.MenuFixtures.makeMenuOne
import domain.MenuGroupFixtures.makeMenuGroupOne
import domain.OrderFixtures.makeOrderTwo
import domain.OrderTableFixtures.makeOrderTableOne
import domain.ProductFixtures.makeProductOne
import io.kotest.matchers.shouldBe
import kitchenpos.acceptance.CommonAcceptanceTest
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderTableRepository
import kitchenpos.domain.ProductRepository
import org.springframework.boot.test.web.server.LocalServerPort

class OrderTableAcceptanceTest(
    @LocalServerPort
    override val port: Int,
    private val menuRepository: MenuRepository,
    private val menuGroupRepository: MenuGroupRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderTableRepository: OrderTableRepository,
) : CommonAcceptanceTest() {
    init {
        given("가게 사장님이 로그인한 뒤에") {
            `when`("주문 테이블을 생성하면") {
                then("주문 테이블이 생성된다.") {
                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                      "name": "9번"
                                }
                                """.trimIndent(),
                            )
                            .post("/api/order-tables")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getInt("numberOfGuests") shouldBe 0
                    response.statusCode shouldBe 201
                }
            }

            `when`("주문 테이블에 손님이 앉으면") {
                then("주문 테이블에 손님이 앉는다.") {
                    val orderTable = orderTableRepository.save(makeOrderTableOne())

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/order-tables/${orderTable.id}/sit")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getBoolean("occupied") shouldBe true
                    response.statusCode shouldBe 200
                }
            }

            `when`("주문 테이블을 비우면") {
                then("주문 테이블이 비워진다.") {
                    orderTableRepository.save(makeOrderTableOne())
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())
                    orderRepository.save(makeOrderTwo())

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/order-tables/6924640b-b0fc-4c86-84f9-b750eeba0205/clear")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getBoolean("occupied") shouldBe false
                    response.statusCode shouldBe 200
                }
            }
//
//            `when`("주문 테이블의 손님 수를 변경하면") {
//                then("주문 테이블의 손님 수가 변경된다.") {
//                    val orderTableId = createOrderTable()
//
//                    commonRequestSpec()
//                        .given()
//                        .put("/api/order-tables/$orderTableId/sit")
//                        .then()
//                        .log().everything()
//                        .extract().response()
//                }
//            }
        }
    }
}
