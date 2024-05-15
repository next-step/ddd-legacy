package kitchenpos.application

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import kitchenpos.domain.*
import kitchenpos.infra.KitchenridersClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
internal class OrderServiceTest {
    @MockK
    private lateinit var orderRepository: OrderRepository

    @MockK
    private lateinit var menuRepository: MenuRepository

    @MockK
    private lateinit var orderTableRepository: OrderTableRepository

    @MockK
    private lateinit var kitchenridersClient: KitchenridersClient

    @InjectMockKs
    private lateinit var orderService: OrderService

    @Nested
    inner class `주문 생성 테스트` {
        @DisplayName("타입 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val request = createOrderRequest(
                type = null
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val request = createOrderRequest(
                type = OrderType.DELIVERY,
                orderLineItems = null
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴 정보가 비어있다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val request = createOrderRequest(
                type = OrderType.DELIVERY,
                orderLineItems = emptyList()
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴의 메뉴 개수와 실제 주문 메뉴의 개수가 다르다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test4() {
            // given
            val request = createOrderRequest(
                type = OrderType.DELIVERY,
                orderLineItems = listOf(createOrderLineItem())
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(Menu(), Menu())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 배달일 때, 주문 메뉴의 요청 개수가 음수보다 작으면, IllegalStateException 예외 처리를 한다.")
        @Test
        fun test5() {
            // given
            val request = createOrderRequest(
                type = OrderType.DELIVERY,
                orderLineItems = listOf(createOrderLineItem(quantity = -1))
            )

            val menu = createMenu()

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 포장일 때, 주문 메뉴의 요청 개수가 음수보다 작으면, IllegalStateException 예외 처리를 한다.")
        @Test
        fun test6() {
            // given
            val request = createOrderRequest(
                type = OrderType.TAKEOUT,
                orderLineItems = listOf(createOrderLineItem(quantity = -1))
            )

            val menu = createMenu()

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴의 메뉴 id가 존재하지 않은 메뉴라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test7() {
            // given
            val request = createOrderRequest(
                type = OrderType.EAT_IN,
            )

            val menu = createMenu()

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.create(request)
            }
        }

        @DisplayName("메뉴가 노출되지 않는 상태라면, IllegalStateException 예외 처리를 한다.")
        @Test
        fun test8() {
            // given
            val request = createOrderRequest(
                type = OrderType.EAT_IN,
            )

            val menu = createMenu(
                isDisplayed = false,
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.create(request)
            }
        }

        @DisplayName("메뉴의 가격이 요청한 주문 메뉴의 가격과 같지 않다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test9() {
            // given
            val request = createOrderRequest(
                orderLineItems = listOf(
                    createOrderLineItem(
                        quantity = 1,
                        price = BigDecimal.ONE,
                    )
                ),
            )

            val menu = createMenu(
                price = BigDecimal.TWO
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 배달 타입일 때, 주소 정보가 없다면, IllegalArgumentException 처리를 한다.")
        @Test
        fun test10() {
            // given
            val request = createOrderRequest(
                type = OrderType.DELIVERY,
                deliveryAddress = null
            )

            val menu = createMenu(
                price = BigDecimal.ONE
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 배달 타입일 때, 정상 요청이라면 주문이 생성된다.")
        @Test
        fun test11() {
            // given
            val orderTable = createOrderTable(isOccupied = false)

            val request = createOrderRequest(
                type = OrderType.DELIVERY,
                orderLineItems = listOf(
                    createOrderLineItem(
                        quantity = 1,
                        price = BigDecimal.ONE,
                    )
                ),
                orderTable = orderTable,
                deliveryAddress = "주소"
            )

            val menu = createMenu(
                price = BigDecimal.ONE
            )

            val 찾아온_주문 = createOrder(
                status = OrderStatus.WAITING,
                type = request.type,
                orderTable = orderTable,
                orderLineItems = request.orderLineItems,
                deliveryAddress = request.deliveryAddress
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderRepository.save(any()) } returns 찾아온_주문

            // when
            val result = orderService.create(request)

            // then
            result.type shouldBe request.type
            result.status shouldBe OrderStatus.WAITING
            result.id shouldNotBe null
            result.orderDateTime shouldNotBe null
            result.orderLineItems shouldNotBe null
            result.orderLineItems[0].menuId shouldBe request.orderLineItems[0].menuId
            result.orderLineItems[0].quantity shouldBe request.orderLineItems[0].quantity
            result.orderLineItems[0].price shouldBe request.orderLineItems[0].price
            result.deliveryAddress shouldBe request.deliveryAddress
        }

        @DisplayName("주문 유형이 매장 내 식사일 때, 테이블 id가 존재하지 않는다면, NoSuchElementException 처리를 한다.")
        @Test
        fun test12() {
            // given
            val request = createOrderRequest(
                type = OrderType.EAT_IN,
                orderLineItems = listOf(
                    createOrderLineItem(
                        quantity = 1,
                        price = BigDecimal.ONE,
                    )
                ),
            )

            val menu = createMenu(
                price = BigDecimal.ONE
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderTableRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 매장 내 식사일 때, 해당 테이블이 점유 상태가 아니라면, IllegalStateException 처리를 한다.")
        @Test
        fun test13() {
            // given
            val orderTable = createOrderTable(isOccupied = false)

            val request = createOrderRequest(
                type = OrderType.EAT_IN,
                orderLineItems = listOf(
                    createOrderLineItem(
                        quantity = 1,
                        price = BigDecimal.ONE,
                    )
                ),
                orderTable = orderTable
            )

            val menu = createMenu(
                price = BigDecimal.ONE
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 매장 내 식사일 때, 정상 요청이라면 주문이 생성된다.")
        @Test
        fun test14() {
            // given
            val orderTable = createOrderTable(isOccupied = true)

            val request = createOrderRequest(
                type = OrderType.EAT_IN,
                orderLineItems = listOf(
                    createOrderLineItem(
                        quantity = 1,
                        price = BigDecimal.ONE,
                    )
                ),
                orderTable = orderTable
            )

            val menu = createMenu(
                price = BigDecimal.ONE
            )

            val 찾아온_주문 = createOrder(
                type = request.type,
                status = OrderStatus.WAITING,
                orderTable = request.orderTable,
                orderLineItems = request.orderLineItems,
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)
            every { orderRepository.save(any()) } returns 찾아온_주문

            // when
            val result = orderService.create(request)

            // then
            assertSoftly {
                result.type shouldBe request.type
                result.status shouldBe OrderStatus.WAITING
                result.id shouldNotBe null
                result.orderDateTime shouldNotBe null
                result.orderLineItems shouldNotBe null
                result.orderLineItems[0].menuId shouldBe request.orderLineItems[0].menuId
                result.orderLineItems[0].quantity shouldBe request.orderLineItems[0].quantity
                result.orderLineItems[0].price shouldBe request.orderLineItems[0].price
                result.orderTable.isOccupied shouldBe orderTable.isOccupied
            }
        }

        @DisplayName("주문 유형이 포장일 때, 정상 요청이라면 주문이 생성된다.")
        @Test
        fun test15() {
            // given
            val menu = createMenu(
                price = BigDecimal.ONE
            )

            val request = createOrderRequest(
                type = OrderType.TAKEOUT,
                orderLineItems = listOf(
                    createOrderLineItem(
                        quantity = 1,
                        price = BigDecimal.ONE,
                    )
                )
            )

            val 찾아온_주문 = createOrder(
                status = OrderStatus.WAITING,
                orderTable = request.orderTable,
                orderLineItems = request.orderLineItems,
            )

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderRepository.save(any()) } returns 찾아온_주문

            // when
            val result = orderService.create(request)

            // then
            assertSoftly {
                result.type shouldBe request.type
                result.status shouldBe OrderStatus.WAITING
                result.id shouldNotBe null
                result.orderDateTime shouldNotBe null
                result.orderLineItems shouldNotBe null
                result.orderLineItems[0].menuId shouldBe request.orderLineItems[0].menuId
                result.orderLineItems[0].quantity shouldBe request.orderLineItems[0].quantity
                result.orderLineItems[0].price shouldBe request.orderLineItems[0].price
            }
        }
    }

    @Nested
    inner class `주문 수락 테스트` {
        @DisplayName("존재하지 않는 주문 id 라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderId = UUID.randomUUID()

            every { orderRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.accept(orderId)
            }
        }

        @DisplayName("주문 상태가 대기 상태가 아니라면, IllegalStateException 예외 처리를 한다.")
        @ParameterizedTest
        @CsvSource("ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED")
        fun test2(orderStatus: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = orderStatus,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.accept(orderId)
            }
        }

        @DisplayName("주문 유형이 배달일 때, 키친 라이더스에게 배달 요청을 하고, 배달을 수락 한다.")
        @Test
        fun test3() {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = OrderStatus.WAITING,
                type = OrderType.DELIVERY,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)
            justRun { kitchenridersClient.requestDelivery(any(), any(), any()) }

            // when
            val result = orderService.accept(orderId)

            // then
            result.status shouldBe OrderStatus.ACCEPTED
            verify { kitchenridersClient.requestDelivery(any(), any(), any()) }
        }

        @DisplayName("주문 유형이 포장이나 매장 내 식사 일 때, 배달을 수락 한다.")
        @ParameterizedTest
        @CsvSource("TAKEOUT", "EAT_IN")
        fun test4(type: OrderType) {
            // given
            val orderId = UUID.randomUUID()
            val order = createOrder(
                id = orderId,
                status = OrderStatus.WAITING,
                type = type,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when
            val result = orderService.accept(orderId)

            // then
            result.status shouldBe OrderStatus.ACCEPTED
        }
    }

    @Nested
    inner class `주문 서빙 테스트` {
        @DisplayName("존재하지 않는 주문 id 라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderId = UUID.randomUUID()

            every { orderRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.serve(orderId)
            }
        }

        @DisplayName("주문 상태가 수락 상태가 아니라면, IllegalStateException 예외 처리를 한다.")
        @ParameterizedTest
        @CsvSource("WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED")
        fun test2(status: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = status,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.serve(orderId)
            }
        }

        @DisplayName("정상 요청이라면, 주문을 서빙 상태로 만든다.")
        @Test
        fun test3() {
            // given
            val orderId = UUID.randomUUID()
            val order = createOrder(
                id = orderId,
                status = OrderStatus.ACCEPTED,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when
            val result = orderService.serve(orderId)

            // then
            result.status shouldBe OrderStatus.SERVED
        }
    }

    @Nested
    inner class `배달 시작 테스트` {
        @DisplayName("존재하지 않는 주문 id 라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderId = UUID.randomUUID()

            every { orderRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.startDelivery(orderId)
            }
        }

        @DisplayName("주문 유형이 배달이 아니라면, IllegalStateException 예외 처리를 한다.")
        @ParameterizedTest
        @CsvSource("TAKEOUT", "EAT_IN")
        fun test2(type: OrderType) {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                type = type,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.startDelivery(orderId)
            }
        }

        @DisplayName("주문 상태가 서빙 상태가 아니라면, IllegalStateException 예외 처리를 한다.")
        @ParameterizedTest
        @CsvSource("WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED")
        fun test3(status: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()
            val order = createOrder(
                id = orderId,
                status = status,
                type = OrderType.DELIVERY,
                orderLineItems = listOf(
                    createOrderLineItem(
                        menu = createMenu(price = BigDecimal.ONE),
                        quantity = 1,
                    )
                ),
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.startDelivery(orderId)
            }
        }

        @DisplayName("요청이 정상이라면, 배달 시작 상태로 변경한다.")
        @Test
        fun test4() {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = OrderStatus.SERVED,
                type = OrderType.DELIVERY,
                orderLineItems = listOf(
                    createOrderLineItem(
                        menu = createMenu(price = BigDecimal.ONE),
                        quantity = 1,
                    )
                ),
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when
            val result = orderService.startDelivery(orderId)

            // then
            result.status shouldBe OrderStatus.DELIVERING
        }
    }

    @Nested
    inner class `배달 완료 테스트` {
        @DisplayName("존재하지 않는 주문 id 라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderId = UUID.randomUUID()

            every { orderRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.completeDelivery(orderId)
            }
        }

        @DisplayName("주문 상태가 배달 중이 아니라면, IllegalStateException 예외 처리를 한다.")
        @ParameterizedTest
        @CsvSource("WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED")
        fun test2(status: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = status,
                type = OrderType.DELIVERY,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.completeDelivery(orderId)
            }
        }

        @DisplayName("정상 요청이라면, 주문 상태를 배달 완료로 변경한다.")
        @Test
        fun test3() {
            // given
            val orderId = UUID.randomUUID()
            val order = createOrder(
                id = orderId,
                status = OrderStatus.DELIVERING,
                type = OrderType.DELIVERY,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when
            val result = orderService.completeDelivery(orderId)

            // then
            result.status shouldBe OrderStatus.DELIVERED
        }
    }

    @Nested
    inner class `주문 완료 테스트` {
        @DisplayName("존재하지 않는 주문 id 라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderId = UUID.randomUUID()

            every { orderRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderService.complete(orderId)
            }
        }

        @DisplayName("주문 유형이 배달인데, 주문 상태가 배달 완료 상태가 아니라면, IllegalStateException 예외 처리")
        @ParameterizedTest
        @CsvSource("WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED")
        fun test2(status: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = status,
                type = OrderType.DELIVERY,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.complete(orderId)
            }
        }

        @DisplayName("주문 유형이 포장 또는 매장 내 식사인데, 주문 상태가 서빙 상태가 아니라면, IllegalStateException 예외 처리를 한다.")
        @ParameterizedTest
        @CsvSource(
            "TAKEOUT, WAITING",
            "TAKEOUT, ACCEPTED",
            "TAKEOUT, COMPLETED",
            "EAT_IN, WAITING",
            "EAT_IN, ACCEPTED",
            "EAT_IN, COMPLETED"
        )
        fun test3(type: OrderType, status: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()
            val order = createOrder(
                id = orderId,
                status = status,
                type = type,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.complete(orderId)
            }
        }

        @DisplayName("주문 유형이 매장 내 식사이고, 해당 테이블을 사용하는 곳이 없다면, 주문 완료 처리가 되면서, 테이블도 치운다.")
        @Test
        fun test4() {
            // given
            val orderId = UUID.randomUUID()
            val order = createOrder(
                id = orderId,
                status = OrderStatus.SERVED,
                type = OrderType.EAT_IN,
                orderTable = createOrderTable(
                    numberOfGuests = 4, isOccupied = true
                ),
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)
            every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns false

            // when
            val result = orderService.complete(orderId)

            // then
            assertSoftly {
                result.status shouldBe OrderStatus.COMPLETED
                result.orderTable.isOccupied shouldBe false
                result.orderTable.numberOfGuests shouldBe 0
            }
        }

        @DisplayName("주문 유형이 배달 또는 포장이고, 정상 요청이면, 주문 완료 처리가 된다.")
        @ParameterizedTest
        @CsvSource("DELIVERY, DELIVERED", "TAKEOUT, SERVED")
        fun test5(type: OrderType, status: OrderStatus) {
            // given
            val orderId = UUID.randomUUID()

            val order = createOrder(
                id = orderId,
                status = status,
                type = type,
            )

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when
            val result = orderService.complete(orderId)

            // then
            result.status shouldBe OrderStatus.COMPLETED
        }
    }

    private fun createOrderRequest(
        id: UUID = UUID.randomUUID(),
        status: OrderStatus = OrderStatus.WAITING,
        type: OrderType? = OrderType.TAKEOUT,
        orderTable: OrderTable = createOrderTable(),
        orderLineItems: List<OrderLineItem>? = listOf(createOrderLineItem()),
        deliveryAddress: String? = null,
    ) = createOrder(
        id = id,
        status = status,
        type = type,
        orderTable = orderTable,
        orderLineItems = orderLineItems,
        deliveryAddress = deliveryAddress,
    )

    private fun createOrder(
        id: UUID = UUID.randomUUID(),
        status: OrderStatus = OrderStatus.WAITING,
        type: OrderType? = OrderType.TAKEOUT,
        orderTable: OrderTable = createOrderTable(),
        orderLineItems: List<OrderLineItem>? = listOf(createOrderLineItem()),
        orderDateTime: LocalDateTime = LocalDateTime.now(),
        deliveryAddress: String? = null,
    ) = Order().apply {
        this.id = id
        this.type = type
        this.status = status
        this.orderTable = orderTable
        this.orderLineItems = orderLineItems
        this.orderDateTime = orderDateTime
        this.deliveryAddress = deliveryAddress
    }

    private fun createOrderTable(
        id: UUID = UUID.randomUUID(),
        name: String = "테이블명",
        numberOfGuests: Int = 4,
        isOccupied: Boolean = true,
    ) = OrderTable().apply {
        this.id = id
        this.name = name
        this.numberOfGuests = numberOfGuests
        this.isOccupied = isOccupied
    }

    private fun createOrderLineItem(
        menu: Menu = createMenu(),
        quantity: Long = 1,
        seq: Long = 1,
        price: BigDecimal = BigDecimal.valueOf(1000L),
    ) = OrderLineItem().apply {
        this.menuId = menu.id
        this.menu = menu
        this.quantity = quantity
        this.seq = seq
        this.price = price
    }

    private fun createMenu(
        id: UUID = UUID.randomUUID(),
        price: BigDecimal = BigDecimal.valueOf(1000L),
        isDisplayed: Boolean = true,
    ) = Menu().apply {
        this.id = id
        this.price = price
        this.isDisplayed = isDisplayed
    }
}
