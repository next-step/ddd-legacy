package kitchenpos.acceptance

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.junit5.MockKExtension
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = ["kitchenpos.*"])
@TestPropertySource("classpath:application.properties")
@ExtendWith(MockKExtension::class)
abstract class CommonAcceptanceTest : BehaviorSpec() {
    abstract val port: Int

    fun commonRequestSpec(): RequestSpecification =
        RestAssured.given()
            .log().all()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
}
