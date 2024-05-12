package kitchenpos.acceptance.menu

import domain.MenuFixtures.makeMenuOne
import domain.MenuGroupFixtures.makeMenuGroupOne
import domain.ProductFixtures.makeProductOne
import io.kotest.matchers.shouldBe
import kitchenpos.acceptance.CommonAcceptanceTest
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.ProductRepository
import org.springframework.boot.test.web.server.LocalServerPort

class MenuAcceptanceTest(
    @LocalServerPort
    override val port: Int,
    private val productRepository: ProductRepository,
    private val menuGroupRepository: MenuGroupRepository,
    private val menuRepository: MenuRepository,
) : CommonAcceptanceTest() {
    init {
        given("가게 사장님이 로그인한 뒤에") {
            `when`("메뉴를 생성하면") {
                then("메뉴가 생성된다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())

                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                    "name": "후라이드 치킨",
                                    "price": 16000,
                                    "menuGroupId": "df8d7e4f-4283-4c91-9e43-d9e2dcb6f182",
                                    "menuProducts": [
                                        {
                                            "productId": "f699d757-1ea1-46fc-9952-d92d29057687",
                                            "quantity": 1
                                        }
                                    ]
                                }
                                """.trimIndent(),
                            )
                            .post("/api/menus")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("name") shouldBe "후라이드 치킨"
                    jsonPathEvaluator.getInt("price") shouldBe 16000
                    response.statusCode shouldBe 201
                }
            }

            `when`("메뉴의 가격을 변경하면") {
                then("가격이 변경된다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())

                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                    "price": 15000
                                }
                                """.trimIndent(),
                            )
                            .put("/api/menus/9625e053-28d8-49e6-9b43-bef6f9af4c3b/price")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getInt("price") shouldBe 15000
                    response.statusCode shouldBe 200
                }

                then("메뉴의 가격이 메뉴 상품들의 가격과 개수보다 작으면 가격을 변경할 수 없다.") {
                    productRepository.save(makeProductOne())
                    menuGroupRepository.save(makeMenuGroupOne())
                    menuRepository.save(makeMenuOne())

                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                    "price": 17000
                                }
                                """.trimIndent(),
                            )
                            .put("/api/menus/9625e053-28d8-49e6-9b43-bef6f9af4c3b/price")
                            .then()
                            .log().everything()
                            .extract().response()

                    response.statusCode shouldBe 500
                }
            }
        }
    }
}
