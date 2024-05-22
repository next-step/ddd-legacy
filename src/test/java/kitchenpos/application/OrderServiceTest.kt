package kitchenpos.application

import kitchenpos.domain.*
import kitchenpos.infra.KitchenridersClient
import kitchenpos.utils.generateUUIDFrom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal

@SpringBootTest
@Sql("classpath:db/data.sql")
class OrderServiceTest {
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Autowired
    private lateinit var orderTableRepository: OrderTableRepository

    @MockBean
    private lateinit var kitchenridersClient: KitchenridersClient

    @Autowired
    private lateinit var orderTableService: OrderTableService

    @Autowired
    private lateinit var sut: OrderService

    companion object {
        private val EXISTING_ORDER_TABLE_ID = generateUUIDFrom("8d71004329b6420e8452233f5a035520")
        private val EXISTING_MENU_ID = generateUUIDFrom("b9c670b04ef5409083496868df1c7d62")
        private val NON_DISPLAYED_MENU_ID = generateUUIDFrom("33e558df7d934622b50efcc4282cd184")
    }

    @DisplayName("주문 생성 (공통)")
    @Nested
    inner class CreateOrderCommonCase {
        @DisplayName("주문자는 주문시 주문유형을 선택해야 한다.")
        @Test
        fun case_1() {
            // given
            val request = Order()

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("주문자는 주문시 메뉴를 선택해야 한다. (메뉴 선택 x)")
        @Test
        fun case_2() {
            // given
            val request = Order()
            request.type = OrderType.EAT_IN

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("'매장식사' 가 아닌 경우, 주문 상품 수량은 음수일 수 없다.")
        @Test
        fun case_3() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = -1
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("주문한 메뉴의 가격과 실제 메뉴의 가격이 같아야 한다.")
        @Test
        fun case_4() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(16_000) // should be 17_000
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴가 `노출` 상태이어야 주문 가능하다.")
        @Test
        fun case_5() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = NON_DISPLAYED_MENU_ID
                    this.quantity = 1
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            // then
            assertThrows<IllegalStateException> { sut.create(request) }
        }

        @DisplayName("최초 주문시 `대기` 상태로 등록된다.")
        @Test
        fun case_6() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            val createdOrder = sut.create(request)

            // then
            assertThat(createdOrder.status).isEqualTo(
                OrderStatus.WAITING,
            )
        }
    }

    @DisplayName("매장식사 유형으로 주문이 가능하다.")
    @Nested
    inner class CreateInEatOrder {
        @DisplayName("주문전에 테이블에 앉아서 주문해야 한다.")
        @Test
        fun case_1() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.EAT_IN
                    this.orderLineItems = listOf(orderLineItem)
                    this.orderTableId = EXISTING_ORDER_TABLE_ID
                }

            // when
            // then
            assertThrows<IllegalStateException> { sut.create(request) }
        }

        @DisplayName("메뉴가 접수되면 `접수` 상태로 변경된다.")
        @Test
        fun case_2() {
            // given
            val occupiedOrderTable = orderTableService.sit(EXISTING_ORDER_TABLE_ID)

            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.EAT_IN
                    this.orderLineItems = listOf(orderLineItem)
                    this.orderTableId = occupiedOrderTable.id
                }

            val waitingOrder = sut.create(request)

            // when
            val acceptedOrder = sut.accept(waitingOrder.id)

            // then
            assertThat(acceptedOrder.status).isEqualTo(OrderStatus.ACCEPTED)
        }

        @DisplayName("메뉴가 준비되면 `서빙완료` 상태로 변경된다.")
        @Test
        fun case_3() {
            // given
            val occupiedOrderTable = orderTableService.sit(EXISTING_ORDER_TABLE_ID)

            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.EAT_IN
                    this.orderLineItems = listOf(orderLineItem)
                    this.orderTableId = occupiedOrderTable.id
                }

            val waitingOrder = sut.create(request)
            val acceptedOrder = sut.accept(waitingOrder.id)

            // when
            val servedOrder = sut.serve(acceptedOrder.id)

            // then
            assertThat(servedOrder.status).isEqualTo(OrderStatus.SERVED)
        }

        @DisplayName("손님이 메뉴를 가져가면 사장님은 주문을 `완료` 상태로 변경된다.")
        @Test
        fun case_4() {
            // given
            val occupiedOrderTable = orderTableService.sit(EXISTING_ORDER_TABLE_ID)

            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.EAT_IN
                    this.orderLineItems = listOf(orderLineItem)
                    this.orderTableId = occupiedOrderTable.id
                }

            val waitingOrder = sut.create(request)
            val acceptedOrder = sut.accept(waitingOrder.id)
            val servedOrder = sut.serve(acceptedOrder.id)

            // when
            val completedOrder = sut.complete(servedOrder.id)

            // then
            assertThat(completedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        }
    }

    @DisplayName("배송 유형으로 주문이 가능하다.")
    @Nested
    inner class CreateDeliveryOrder {
        @DisplayName("메뉴, 수량, 배송지 정보를 입력해야 한다. (배송지 입력 x)")
        @Test
        fun case_1() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("`라이더 요청` 이 성공되면 `접수` 상태로 변경된다.")
        @Test
        fun case_2() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.deliveryAddress = "nextstep 사무실"
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            val createdOrder = sut.create(request)

            // then
            assertThat(createdOrder.status).isEqualTo(OrderStatus.WAITING)
        }

        @DisplayName("`라이더 요청` 이 성공되면 `접수` 상태로 변경된다.")
        @Test
        fun case_3() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.deliveryAddress = "nextstep 사무실"
                    this.orderLineItems = listOf(orderLineItem)
                }

            given(kitchenridersClient.requestDelivery(any(), any(), any()))
                .willAnswer { println("*** 라이더 호출 성공 ***") }

            val createdOrder = sut.create(request)

            // when
            val acceptedOrder = sut.accept(createdOrder.id)

            // then
            assertThat(acceptedOrder.status).isEqualTo(OrderStatus.ACCEPTED)
        }

        @DisplayName("서빙이 완료되면 `서빙완료` 상태로 변경된다.")
        @Test
        fun case_4() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.deliveryAddress = "nextstep 사무실"
                    this.orderLineItems = listOf(orderLineItem)
                }

            given(kitchenridersClient.requestDelivery(any(), any(), any()))
                .willAnswer { println("*** 라이더 호출 성공 ***") }

            val createdOrder = sut.create(request)
            val acceptedOrder = sut.accept(createdOrder.id)

            // when
            val servedOrder = sut.serve(acceptedOrder.id)

            // then
            assertThat(servedOrder.status).isEqualTo(OrderStatus.SERVED)
        }

        @DisplayName("배송이 시작되면 `배송중` 상태로 변경된다.")
        @Test
        fun case_5() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.deliveryAddress = "nextstep 사무실"
                    this.orderLineItems = listOf(orderLineItem)
                }

            given(kitchenridersClient.requestDelivery(any(), any(), any()))
                .willAnswer { println("*** 라이더 호출 성공 ***") }

            val createdOrder = sut.create(request)
            val acceptedOrder = sut.accept(createdOrder.id)
            val servedOrder = sut.serve(acceptedOrder.id)

            // when
            val deliveryStartedOrder = sut.startDelivery(servedOrder.id)

            // then
            assertThat(deliveryStartedOrder.status).isEqualTo(OrderStatus.DELIVERING)
        }

        @DisplayName("배송이 완료되면 `배송완료` 상태로 변경된다.")
        @Test
        fun case_6() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.deliveryAddress = "nextstep 사무실"
                    this.orderLineItems = listOf(orderLineItem)
                }

            given(kitchenridersClient.requestDelivery(any(), any(), any()))
                .willAnswer { println("*** 라이더 호출 성공 ***") }

            val createdOrder = sut.create(request)
            val acceptedOrder = sut.accept(createdOrder.id)
            val servedOrder = sut.serve(acceptedOrder.id)
            val deliveryStartedOrder = sut.startDelivery(servedOrder.id)

            // when

            val deliveryCompletedOrder = sut.completeDelivery(deliveryStartedOrder.id)

            // then
            assertThat(deliveryCompletedOrder.status).isEqualTo(OrderStatus.DELIVERED)
        }

        @DisplayName("사장님은 `배송완료` 상태를 확인하면 `완료` 상태로 변경 가능하다.")
        @Test
        fun case_7() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.DELIVERY
                    this.deliveryAddress = "nextstep 사무실"
                    this.orderLineItems = listOf(orderLineItem)
                }

            given(kitchenridersClient.requestDelivery(any(), any(), any()))
                .willAnswer { println("*** 라이더 호출 성공 ***") }

            val createdOrder = sut.create(request)
            val acceptedOrder = sut.accept(createdOrder.id)
            val servedOrder = sut.serve(acceptedOrder.id)
            val deliveryStartedOrder = sut.startDelivery(servedOrder.id)
            val deliveryCompletedOrder = sut.completeDelivery(deliveryStartedOrder.id)

            // when
            val completedOrder = sut.complete(deliveryCompletedOrder.id)

            // then
            assertThat(completedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        }
    }

    @DisplayName("테이크아웃 유형으로 주문이 가능하다.")
    @Nested
    inner class CreateTakeoutOrder {
        @DisplayName("메뉴, 수량 정보를 입력해야 한다.")
        @Test
        fun case_1() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            // when
            val createdOrder = sut.create(request)

            // then
            assertThat(createdOrder.id).isNotNull()
        }

        @DisplayName("메뉴가 접수되면 `접수` 상태로 변경된다.")
        @Test
        fun case_2() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            val createdOrder = sut.create(request)

            // when
            val acceptedOrder = sut.accept(createdOrder.id)

            // then
            assertThat(acceptedOrder.status).isEqualTo(OrderStatus.ACCEPTED)
        }

        @DisplayName("메뉴가 준비되면 `서빙완료` 상태로 변경된다.")
        @Test
        fun case_3() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            val createdOrder = sut.create(request)
            val acceptedOrder = sut.accept(createdOrder.id)

            // when
            val servedOrder = sut.serve(acceptedOrder.id)

            // then
            assertThat(servedOrder.status).isEqualTo(OrderStatus.SERVED)
        }

        @DisplayName("손님이 메뉴를 가져가면 사장님은 주문을 `완료` 상태로 변경 가능하다.")
        @Test
        fun case_4() {
            // given
            val orderLineItem =
                OrderLineItem().apply {
                    this.menuId = EXISTING_MENU_ID
                    this.quantity = 1
                    this.price = BigDecimal.valueOf(17_000)
                }

            val request =
                Order().apply {
                    this.type = OrderType.TAKEOUT
                    this.orderLineItems = listOf(orderLineItem)
                }

            val createdOrder = sut.create(request)
            val acceptedOrder = sut.accept(createdOrder.id)
            val servedOrder = sut.serve(acceptedOrder.id)

            // when
            val completedOrder = sut.complete(servedOrder.id)

            // then
            assertThat(completedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        }
    }

    @DisplayName("생성된 주문 목록을 조회할 수 있다.")
    @Test
    fun case_1() {
        // when
        val orders = sut.findAll()

        // then
        assertThat(orders.size).isEqualTo(3)
    }
}
