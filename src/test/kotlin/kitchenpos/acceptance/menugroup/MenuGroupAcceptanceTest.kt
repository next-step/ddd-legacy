package kitchenpos.acceptance.menugroup

import io.kotest.matchers.shouldBe
import kitchenpos.acceptance.CommonAcceptanceTest
import org.springframework.boot.test.web.server.LocalServerPort

class MenuGroupAcceptanceTest(
    @LocalServerPort
    override val port: Int,
) : CommonAcceptanceTest() {
    init {
        given("가게 사장님이 로그인한 뒤에") {
            `when`("메뉴 그룹을 생성하면") {
                then("메뉴 그룹이 생성된다.") {
                    val response =
                        commonRequestSpec()
                            .given()
                            .body(
                                """
                                {
                                    "name": "테스트 메뉴 그룹"
                                }
                                """.trimIndent(),
                            )
                            .post("/api/menu-groups")
                            .then()
                            .log().everything()
                            .extract().response()

                    val jsonPathEvaluator = response.jsonPath()

                    jsonPathEvaluator.getString("name") shouldBe "테스트 메뉴 그룹"
                    response.statusCode shouldBe 201
                }
            }
        }
    }
}
