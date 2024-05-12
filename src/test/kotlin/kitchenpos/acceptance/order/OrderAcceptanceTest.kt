package kitchenpos.acceptance.order

import domain.MenuFixtures.makeMenuOne
import domain.MenuGroupFixtures.makeMenuGroupOne
import domain.OrderFixtures.makeOrderOne
import domain.ProductFixtures.makeProductOne
import io.kotest.matchers.shouldBe
import kitchenpos.acceptance.CommonAcceptanceTest
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
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
    private val orderRepository: OrderRepository,
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

        given("가게 사장님이 로그인한 뒤에") {
            `when`("주문을 수락하면") {
                then("주문이 수락되고, 배달 시스템에 요청을 보낸다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())
                    orderRepository.save(
                        makeOrderOne().apply {
                            status = OrderStatus.WAITING
                            orderTable = null
                        },
                    )

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/accept")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("orderLineItems[0].menu.id") shouldBe "9625e053-28d8-49e6-9b43-bef6f9af4c3b"
                    jsonPathEvaluator.getInt("orderLineItems[0].quantity") shouldBe 1
                    jsonPathEvaluator.getFloat("orderLineItems[0].menu.price") shouldBe 16000.00f
                    jsonPathEvaluator.getString("deliveryAddress") shouldBe "서울시 강남구"
                    jsonPathEvaluator.getString("status") shouldBe "ACCEPTED"

                    response.statusCode shouldBe 200
                }
            }
        }

        given("음식이 준비되어") {
            `when`("전달을 완료하면") {
                then("주문의 상태를 제공됨으로 전환한다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())
                    orderRepository.save(
                        makeOrderOne().apply {
                            status = OrderStatus.ACCEPTED
                            orderTable = null
                        },
                    )

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/serve")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("orderLineItems[0].menu.id") shouldBe "9625e053-28d8-49e6-9b43-bef6f9af4c3b"
                    jsonPathEvaluator.getInt("orderLineItems[0].quantity") shouldBe 1
                    jsonPathEvaluator.getFloat("orderLineItems[0].menu.price") shouldBe 16000.00f
                    jsonPathEvaluator.getString("deliveryAddress") shouldBe "서울시 강남구"
                    jsonPathEvaluator.getString("status") shouldBe "SERVED"

                    response.statusCode shouldBe 200
                }
            }
        }

        given("음식 제공을 완료하였을때") {
            `when`("배달 시작을 요청하면") {
                then("주문의 상태를 배달중으로 전환한다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())
                    orderRepository.save(
                        makeOrderOne().apply {
                            status = OrderStatus.SERVED
                            orderTable = null
                        },
                    )

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/start-delivery")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("orderLineItems[0].menu.id") shouldBe "9625e053-28d8-49e6-9b43-bef6f9af4c3b"
                    jsonPathEvaluator.getInt("orderLineItems[0].quantity") shouldBe 1
                    jsonPathEvaluator.getFloat("orderLineItems[0].menu.price") shouldBe 16000.00f
                    jsonPathEvaluator.getString("deliveryAddress") shouldBe "서울시 강남구"
                    jsonPathEvaluator.getString("status") shouldBe "DELIVERING"

                    response.statusCode shouldBe 200
                }
            }
        }

        given("배달이 완료됐을 때") {
            `when`("배달 완료를 요청하면") {
                then("주문의 상태를 배달 완료로 전환한다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())
                    orderRepository.save(
                        makeOrderOne().apply {
                            status = OrderStatus.DELIVERING
                            orderTable = null
                        },
                    )

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/complete-delivery")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("orderLineItems[0].menu.id") shouldBe "9625e053-28d8-49e6-9b43-bef6f9af4c3b"
                    jsonPathEvaluator.getInt("orderLineItems[0].quantity") shouldBe 1
                    jsonPathEvaluator.getFloat("orderLineItems[0].menu.price") shouldBe 16000.00f
                    jsonPathEvaluator.getString("deliveryAddress") shouldBe "서울시 강남구"
                    jsonPathEvaluator.getString("status") shouldBe "DELIVERED"

                    response.statusCode shouldBe 200
                }
            }
        }

        given("고객이 배달 확정을 누르거나, 정해진 시간이 지났을 때") {
            `when`("주문 완료를 요청하면") {
                then("주문의 상태를 주문 완료로 전환한다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())
                    orderRepository.save(
                        makeOrderOne().apply {
                            status = OrderStatus.DELIVERED
                            orderTable = null
                        },
                    )

                    val response =
                        commonRequestSpec()
                            .given()
                            .put("/api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/complete")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("orderLineItems[0].menu.id") shouldBe "9625e053-28d8-49e6-9b43-bef6f9af4c3b"
                    jsonPathEvaluator.getInt("orderLineItems[0].quantity") shouldBe 1
                    jsonPathEvaluator.getFloat("orderLineItems[0].menu.price") shouldBe 16000.00f
                    jsonPathEvaluator.getString("deliveryAddress") shouldBe "서울시 강남구"
                    jsonPathEvaluator.getString("status") shouldBe "COMPLETED"

                    response.statusCode shouldBe 200
                }
            }
        }
    }
}
