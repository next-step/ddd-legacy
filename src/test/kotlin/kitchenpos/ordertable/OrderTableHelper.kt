package kitchenpos.ordertable

import io.restassured.RestAssured
import io.restassured.response.Response
import kitchenpos.domain.OrderTable
import java.util.*

class OrderTableHelper {
    companion object {
        fun 가게테이블이름으로_가게테이블_조회(name: String): OrderTable {
            return RestAssured
                .`when`().get("/api/order-tables")
                .then().extract().jsonPath().getList("", OrderTable::class.java)
                .find { it.name == name }!!
        }

        fun 가게테이블_생성(orderTable: OrderTable): Response {
            return RestAssured
                .given().body(orderTable).contentType("application/json")
                .`when`().post("/api/order-tables")
        }

        fun 가게테이블_생성_ID추출(orderTable: OrderTable): UUID {
            return 가게테이블_생성(orderTable).then().log().all().extract().jsonPath().getUUID("id")
        }

        fun 가게테이블_점유(orderTableId: UUID): Response {
            return RestAssured
                .put("/api/order-tables/$orderTableId/sit")
        }
    }
}
