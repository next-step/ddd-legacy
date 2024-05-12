package kitchenpos.application

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
            val request = Order().apply {
                this.type = null
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val request = Order().apply {
                this.type = OrderType.DELIVERY
                this.orderLineItems = null
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴 정보가 비어있다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val request = Order().apply {
                this.type = OrderType.DELIVERY
                this.orderLineItems = emptyList()
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴의 메뉴 개수와 실제 주문 메뉴의 개수가 다르다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test4() {
            // given
            val request = Order().apply {
                this.type = OrderType.DELIVERY
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                })
            }

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
            val request = Order().apply {
                this.type = OrderType.DELIVERY
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = -1
                })
            }

            every { menuRepository.findAllByIdIn(any()) } returns listOf(Menu())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 포장일 때, 주문 메뉴의 요청 개수가 음수보다 작으면, IllegalStateException 예외 처리를 한다.")
        @Test
        fun test6() {
            // given
            val request = Order().apply {
                this.type = OrderType.TAKEOUT
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = -1
                })
            }

            every { menuRepository.findAllByIdIn(any()) } returns listOf(Menu())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 메뉴의 메뉴 id가 존재하지 않은 메뉴라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test7() {
            // given
            val request = Order().apply {
                this.type = OrderType.EAT_IN
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                })
            }

            every { menuRepository.findAllByIdIn(any()) } returns listOf(Menu())
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
            val request = Order().apply {
                this.type = OrderType.EAT_IN
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                })
            }

            val menu = Menu().apply {
                this.isDisplayed = false
            }

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
            val request = Order().apply {
                this.type = OrderType.EAT_IN
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.TWO
            }

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
            val request = Order().apply {
                this.type = OrderType.DELIVERY
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
                this.deliveryAddress = null
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.ONE
            }

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
            val request = Order().apply {
                this.type = OrderType.DELIVERY
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
                this.deliveryAddress = "주소"
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.ONE
            }

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderRepository.save(any()) } returns request.apply {
                this.status = OrderStatus.WAITING
                this.id = UUID.randomUUID()
                this.orderDateTime = LocalDateTime.now()
                this.orderLineItems = request.orderLineItems
            }

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
            val request = Order().apply {
                this.type = OrderType.EAT_IN
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
                this.deliveryAddress = null
                this.orderTableId = null
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.ONE
            }

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
            val request = Order().apply {
                this.type = OrderType.EAT_IN
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
                this.deliveryAddress = null
                this.orderTableId = null
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.ONE
            }

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderTableRepository.findById(any()) } returns Optional.of(OrderTable().apply {
                this.isOccupied = false
            })

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderService.create(request)
            }
        }

        @DisplayName("주문 유형이 매장 내 식사일 때, 정상 요청이라면 주문이 생성된다.")
        @Test
        fun test14() {
            // given
            val request = Order().apply {
                this.type = OrderType.EAT_IN
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.ONE
            }

            val orderTable = OrderTable().apply {
                this.isOccupied = true
            }

            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)
            every { orderRepository.save(any()) } returns request.apply {
                this.status = OrderStatus.WAITING
                this.id = UUID.randomUUID()
                this.orderDateTime = LocalDateTime.now()
                this.orderLineItems = request.orderLineItems
                this.orderTable = orderTable
            }

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
            result.orderTable.isOccupied shouldBe orderTable.isOccupied
        }

        @DisplayName("주문 유형이 포장일 때, 정상 요청이라면 주문이 생성된다.")
        @Test
        fun test15() {
            // given
            val request = Order().apply {
                this.type = OrderType.TAKEOUT
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menuId = UUID.randomUUID()
                    this.quantity = 1
                    this.price = BigDecimal.ONE
                })
            }

            val menu = Menu().apply {
                this.isDisplayed = true
                this.price = BigDecimal.ONE
            }


            every { menuRepository.findAllByIdIn(any()) } returns listOf(menu)
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderRepository.save(any()) } returns request.apply {
                this.status = OrderStatus.WAITING
                this.id = UUID.randomUUID()
                this.orderDateTime = LocalDateTime.now()
                this.orderLineItems = request.orderLineItems
            }

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
            val order = Order().apply {
                this.status = orderStatus
            }

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
            val order = Order().apply {
                this.status = OrderStatus.WAITING
                this.type = OrderType.DELIVERY
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menu = Menu().apply {
                        this.price = BigDecimal.ONE
                    }
                    this.quantity = 1
                })
            }

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
            val order = Order().apply {
                this.status = OrderStatus.WAITING
                this.type = type
                this.orderLineItems = listOf(OrderLineItem().apply {
                    this.menu = Menu().apply {
                        this.price = BigDecimal.ONE
                    }
                    this.quantity = 1
                })
            }

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
            val order = Order().apply {
                this.status = status
            }

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
            val order = Order().apply {
                this.status = OrderStatus.ACCEPTED
            }

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
            val order = Order().apply {
                this.type = type
            }

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
            val order = Order().apply {
                this.type = OrderType.DELIVERY
                this.status = status
            }

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
            val order = Order().apply {
                this.type = OrderType.DELIVERY
                this.status = OrderStatus.SERVED
            }

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
            val order = Order().apply {
                this.status = status
            }

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
            val order = Order().apply {
                this.status = OrderStatus.DELIVERING
            }

            every { orderRepository.findById(any()) } returns Optional.of(order)

            // when
            val result = orderService.completeDelivery(orderId)

            // then
            result.status shouldBe OrderStatus.DELIVERED
        }
    }
}
