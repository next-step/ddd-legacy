package kitchenpos.order

import io.restassured.RestAssured
import kitchenpos.domain.Order
import java.util.*

class OrderHelper {
    companion object {
        fun 주문ID로_주문_조회(id: UUID): Order {
            return RestAssured
                .get("/api/orders")
                .then().log().all().extract().`as`(Array<Order>::class.java)
                .find { it.id == id }!!
        }
    }
}
