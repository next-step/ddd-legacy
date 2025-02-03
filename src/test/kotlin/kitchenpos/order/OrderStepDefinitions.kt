package kitchenpos.order

import io.cucumber.java.ParameterType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import kitchenpos.domain.Order
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderType
import kitchenpos.menu.MenuHelper.Companion.메뉴_이름으로_메뉴_조회
import kitchenpos.utils.CucumberTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before


class OrderStepDefinitions : CucumberTest() {
    private var order: Order = OrderFixture.fixture()

    @Before
    fun setUp() {
        order = OrderFixture.fixture()
    }

    @ParameterType("홀주문|배달주문|포장주문")
    fun orderType(type: String): OrderType {
        when (type) {
            "홀주문" -> {
                return OrderType.EAT_IN
            }

            "배달주문" -> {
                return OrderType.DELIVERY
            }

            "포장주문" -> {
                return OrderType.TAKEOUT
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    @ParameterType("주문대기|주문접수|주문준비완료|배달중|배달완료|완료")
    fun orderStatus(status: String): OrderStatus {
        when (status) {
            "주문대기" -> {
                return OrderStatus.WAITING
            }

            "주문접수" -> {
                return OrderStatus.ACCEPTED
            }

            "주문준비완료" -> {
                return OrderStatus.SERVED
            }

            "배달중" -> {
                return OrderStatus.DELIVERING
            }

            "배달완료" -> {
                return OrderStatus.DELIVERED
            }

            "완료" -> {
                return OrderStatus.COMPLETED
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    @Given("{orderType} 으로 주문을 입력한다")
    fun 주문유형_선택(type: OrderType) {
        order.type = type
    }

    @Given("주문에 메뉴 {string}와 수량 {int}개를 입력한다")
    fun 주문_메뉴_추가(menuName: String, quantity: Long) {
        val menu = 메뉴_이름으로_메뉴_조회(menuName)
        val orderLineItem = OrderLineItemFixture.fixture(order = order, menu = menu, quantity = quantity)
        order.orderLineItems.add(orderLineItem)
    }

    @Given("주문에 배달주소를 입력한다")
    fun 주문_배달주소_입력() {
        order.deliveryAddress = "서울시 강남구"
    }

    @When("주문을 생성한다")
    fun 주문을_생성한다() {
        try {
            order = RestAssured
                .given().body(order).contentType("application/json")
                .`when`().post("/api/orders")
                .then().extract().`as`(Order::class.java)
        } catch (e: Exception) {
            order = OrderFixture.fixture()
        }
    }

    @Then("생성한 주문의 타입은 {orderType}이고 상태는 {orderStatus}이다")
    fun 주문_타입_상태_확인(type: OrderType, status: OrderStatus) {
        val createdOrder = OrderHelper.주문ID로_주문_조회(order.id)
        assertThat(createdOrder.type).isEqualTo(type)
        assertThat(createdOrder.status).isEqualTo(status)
    }

    @Then("주문 생성 실패")
    fun 주문_생성_실패() {
        val orders = RestAssured
            .get("/api/orders")
            .then().extract().`as`(Array<Order>::class.java)
        assertThat(orders).hasSize(0)
    }
}
