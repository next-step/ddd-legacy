package kitchenpos.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.domain.*
import kitchenpos.infra.KitchenridersClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import random.*
import java.math.BigDecimal
import java.util.*

class OrderServiceTest {
    private val orderRepository: OrderRepository = mockk()
    private val menuRepository: MenuRepository = mockk()
    private val orderTableRepository: OrderTableRepository = mockk()
    private val kichenridersClient: KitchenridersClient = mockk(relaxed = true)
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        orderService = OrderService(orderRepository, menuRepository, orderTableRepository, kichenridersClient)
    }

    @Nested
    inner class `주문 생성` {

        @Test
        fun `주문 타입은 필수값이다 아닌 경우 예외 발생`() {
            // given
            val order = randomOrder(type = null)
            // when then
            assertThrows<IllegalArgumentException> {
                orderService.create(order)
            }
        }

        @Test
        fun `메뉴 주문은 필수값이며, 최소 1개 이상이어야 한다 아닌 경우 예외 발생`() {
            // given
            val order = randomOrder(orderLineItems = emptyList())
            // when then
            assertThrows<IllegalArgumentException> {
                orderService.create(order)
            }
        }

        @Test
        fun `메뉴 주문의 메뉴가 존재해야 한다`() {
            // given
            val order = randomOrder(orderLineItems = listOf(randomOrderLineItem()))
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns emptyList()

            // when then
            assertThrows<IllegalArgumentException> {
                orderService.create(order)
            }
        }

        @ParameterizedTest
        @EnumSource(value = OrderType::class, mode = EnumSource.Mode.EXCLUDE, names = ["EAT_IN"])
        fun `메뉴 주문 수량은 포장 또는 배달 주문의 경우 0보다 커야 한다`(type: OrderType) {
            val order = randomOrder(orderLineItems = listOf(randomOrderLineItem(quantity = -1)), type = type)
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns listOf(
                randomMenu()
            )

            // when then
            assertThrows<IllegalArgumentException> {
                orderService.create(order)
            }
        }

        @Test
        fun `메뉴 주문의 메뉴는 노출된 메뉴여야 한다`() {
            // given
            val order = randomOrder(orderLineItems = listOf(randomOrderLineItem()))
            val menu = randomMenu(isDisplayed = false)
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns listOf(
                menu
            )
            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when then
            assertThrows<IllegalStateException> {
                orderService.create(order)
            }
        }

        @Test
        fun `메뉴 주문 가격은 메뉴 가격과 동일해야 한다`() {
            // given
            val order = randomOrder(orderLineItems = listOf(randomOrderLineItem(price = BigDecimal.valueOf(100))))
            val menu = randomMenu(isDisplayed = true, price = BigDecimal.valueOf(110))
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns listOf(
                menu
            )
            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when then
            assertThrows<IllegalArgumentException> {
                orderService.create(order)
            }
        }

        @Test
        fun `주문 생성 시 주문 상태는 대기여야 한다`() {
            // given
            val order = randomOrder(
                orderLineItems = listOf(randomOrderLineItem(price = BigDecimal.valueOf(100))),
                type = OrderType.TAKEOUT
            )
            val menu = randomMenu(isDisplayed = true, price = BigDecimal.valueOf(100))
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns listOf(
                menu
            )
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderRepository.save(any()) } returns randomOrder(status = OrderStatus.WAITING)
            // when then
            orderService.create(order).apply {
                this.status shouldBe OrderStatus.WAITING
            }
        }

        @Test
        fun `주문 타입이 배달인 경우, 배달 주소는 필수값이다 아닐 경우 예외 발생`() {
            // given
            val order = randomOrder(
                orderLineItems = listOf(randomOrderLineItem(price = BigDecimal.valueOf(100))),
                type = OrderType.DELIVERY,
                deliveryAddress = null
            )
            val menu = randomMenu(isDisplayed = true, price = BigDecimal.valueOf(100))
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns listOf(
                menu
            )
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            // when then
            assertThrows<IllegalArgumentException> {
                orderService.create(order)
            }
        }

        @Test
        fun `주문 타입이 먹고가기인 경우, 주문 테이블이 존재해야 하며 비어 있어야 한다`() {
            // given
            val order = randomOrder(
                orderLineItems = listOf(randomOrderLineItem(price = BigDecimal.valueOf(100))),
                type = OrderType.EAT_IN
            )
            val menu = randomMenu(isDisplayed = true, price = BigDecimal.valueOf(100))
            every { menuRepository.findAllByIdIn(order.orderLineItems.map(OrderLineItem::getMenuId)) } returns listOf(
                menu
            )
            every { menuRepository.findById(any()) } returns Optional.of(menu)
            every { orderTableRepository.findById(any()) } returns Optional.of(randomOrderTable(occupied = false))
            // when then
            assertThrows<IllegalStateException> {
                orderService.create(order)
            }
        }
    }

    @Nested
    inner class `주문 접수` {

        @Test
        fun `주문이 존재해야 한다 아닌 경우 예외 발생`() {
            val orderId = randomOrderId()
            every { orderRepository.findById(orderId) } returns Optional.empty()
            assertThrows<NoSuchElementException> {
                orderService.accept(orderId)
            }
        }

        @ParameterizedTest
        @EnumSource(value = OrderStatus::class, mode = EnumSource.Mode.EXCLUDE, names = ["WAITING"])
        fun `현재 주문 상태가 대기여야 한다`(orderStatus: OrderStatus) {
            val order = randomOrder(status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)
            assertThrows<IllegalStateException> {
                orderService.accept(order.id)
            }
        }

        @Test
        fun `주문 타입이 배달인 경우, 배달 접수 처리를 해야 한다`() {
            // given
            val order = randomOrder(status = OrderStatus.WAITING, type = OrderType.DELIVERY)
            every { orderRepository.findById(order.id) } returns Optional.of(order)
            // when
            orderService.accept(order.id)
            // then
            verify { kichenridersClient.requestDelivery(order.id, any(), order.deliveryAddress) }
        }

        @Test
        fun `정상 접수 상태 반환`() {
            // given
            val order = randomOrder(status = OrderStatus.WAITING)
            every { orderRepository.findById(order.id) } returns Optional.of(order)
            // when then
            orderService.accept(order.id).run {
                this.status shouldBe OrderStatus.ACCEPTED
            }
        }
    }

    @Nested
    inner class `주문 서빙` {

        @Test
        fun `주문이 존재하지 않으면 예외 발생`() {
            val orderId = randomOrderId()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            assertThrows<NoSuchElementException> {
                orderService.serve(orderId)
            }
        }

        @Test
        fun `주문이 접수상태가 아닌 경우 예외 발생`() {
            val order = randomOrder(status = OrderStatus.SERVED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            assertThrows<IllegalStateException> {
                orderService.serve(order.id)
            }
        }

        @Test
        fun `정상 서빙 상태 반환`() {
            val order = randomOrder(status = OrderStatus.ACCEPTED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            orderService.serve(order.id).status shouldBe OrderStatus.SERVED
        }
    }

    @Nested
    inner class `주문 배달` {
        @Test
        fun `주문이 존재하지 않으면 예외 발생`() {
            val orderId = randomOrderId()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            assertThrows<NoSuchElementException> {
                orderService.startDelivery(orderId)
            }
        }

        @Test
        fun `주문 타입이 배달이 아닌 경우 예외 발생`() {
            val order = randomOrder(type = OrderType.EAT_IN)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            assertThrows<IllegalStateException> {
                orderService.startDelivery(order.id)
            }
        }

        @Test
        fun `주문 상태가 서빙이 아닌 경우 예외 발생`() {
            val order = randomOrder(type = OrderType.DELIVERY, status = OrderStatus.WAITING)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            assertThrows<IllegalStateException> {
                orderService.startDelivery(order.id)
            }
        }

        @Test
        fun `정상 배달중 상태 반환`() {
            val order = randomOrder(type = OrderType.DELIVERY, status = OrderStatus.SERVED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            orderService.startDelivery(order.id).status shouldBe OrderStatus.DELIVERING
        }
    }

    @Nested
    inner class `주문 배달 완료` {
        @Test
        fun `주문이 존재하지 않으면 예외 발생`() {
            val orderId = randomOrderId()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            assertThrows<NoSuchElementException> {
                orderService.completeDelivery(orderId)
            }
        }

        @Test
        fun `주문 상태가 배달중이 아닌 경우 예외 발생`() {
            val order = randomOrder(type = OrderType.DELIVERY, status = OrderStatus.WAITING)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            assertThrows<IllegalStateException> {
                orderService.completeDelivery(order.id)
            }
        }

        @Test
        fun `정상 배달 완료 상태 반환`() {
            val order = randomOrder(type = OrderType.DELIVERY, status = OrderStatus.DELIVERING)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            orderService.completeDelivery(order.id).status shouldBe OrderStatus.DELIVERED
        }
    }

    @Nested
    inner class `주문 최종 완료` {
        @Test
        fun `주문이 존재하지 않을 경우 예외 발생`() {
            // given
            val orderId = randomOrderId()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                orderService.complete(orderId)
            }
        }

        @Test
        fun `주문 타입이 배달인 경우, 현재 주문 상태가 배달 완료가 아닐 경우 예외 발생`() {
            // given
            val order = randomOrder(type = OrderType.DELIVERY, status = OrderStatus.DELIVERING)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when then
            assertThrows<IllegalStateException> {
                orderService.complete(order.id)
            }
        }

        @Test
        fun `주문 타입이 포장 또는 먹고가기인 경우, 현재 주문 상태가 서빙여야 한다`() {
            val order = randomOrder(type = OrderType.TAKEOUT, status = OrderStatus.SERVED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when then
            orderService.complete(order.id).status shouldBe OrderStatus.COMPLETED
        }

        @Test
        fun `주문 타입이 먹고가기인 경우, 주문 테이블을 비워야 한다`() {
            // given
            val order =
                randomOrder(type = OrderType.EAT_IN, status = OrderStatus.SERVED, orderTable = randomOrderTable())
            every { orderRepository.findById(order.id) } returns Optional.of(order)
            every {
                orderRepository.existsByOrderTableAndStatusNot(
                    order.orderTable,
                    OrderStatus.COMPLETED
                )
            } returns false
            // when then
            orderService.complete(order.id).status shouldBe OrderStatus.COMPLETED
        }
    }

    @Test
    fun `주문 조회`() {
        val orders = listOf(randomOrder(), randomOrder())
        every { orderRepository.findAll() } returns orders
        orderService.findAll() shouldBe orders
    }
}
