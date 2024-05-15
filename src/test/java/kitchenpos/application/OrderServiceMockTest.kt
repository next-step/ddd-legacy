package kitchenpos.application

import kitchenpos.domain.*
import kitchenpos.infra.KitchenridersClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import utils.spec.MenuSpec
import utils.spec.OrderSpec
import utils.spec.OrderTableSpec
import java.math.BigDecimal
import java.util.*

@DisplayName("주문 서비스")
@ExtendWith(MockitoExtension::class)
class OrderServiceMockTest {
    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var menuRepository: JpaMenuRepository

    @Mock
    private lateinit var orderTableRepository: OrderTableRepository

    @Mock
    private lateinit var kitchenridersClient: KitchenridersClient

    @InjectMocks
    private lateinit var orderService: OrderService

    private val menu = MenuSpec.of()
    private val hiddenMenu = MenuSpec.of(display = false)
    private val orderTable = OrderTableSpec.of(occupied = true, numberOfGuests = 2)
    private val notOccupiedOrderTable = OrderTableSpec.of(occupied = false, numberOfGuests = 0)

    @Nested
    @DisplayName("주문 생성")
    inner class `주문 생성` {
        @Test
        fun `정상적인 매장 주문 생성 성공`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.EAT_IN
            request.orderLineItems = listOf(orderLineItem)
            request.orderTableId = orderTable.id

            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            `when`(menuRepository.findById(menu.id))
                .thenReturn(Optional.of(menu))

            `when`(orderTableRepository.findById(orderTable.id))
                .thenReturn(Optional.of(orderTable))

            Assertions.assertThatCode { orderService.create(request) }
                .doesNotThrowAnyException()
        }

        @Test
        fun `주문 테이블이 미사용 중인 경우 실패`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.EAT_IN
            request.orderLineItems = listOf(orderLineItem)
            request.orderTableId = notOccupiedOrderTable.id

            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            `when`(menuRepository.findById(menu.id))
                .thenReturn(Optional.of(menu))

            `when`(orderTableRepository.findById(notOccupiedOrderTable.id))
                .thenReturn(Optional.of(notOccupiedOrderTable))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.create(request) }
        }

        @Test
        fun `주문 테이블 미지정 후 주문 요청 시 실패`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.EAT_IN
            request.orderLineItems = listOf(orderLineItem)


            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            `when`(menuRepository.findById(menu.id))
                .thenReturn(Optional.of(menu))

            Assertions.assertThatExceptionOfType(NoSuchElementException::class.java)
                .isThrownBy { orderService.create(request) }
        }

        @Test
        fun `주문 아이템이 없는 경우 실패`() {
            val request = Order()
            request.type = OrderType.EAT_IN
            request.orderLineItems = emptyList()
            request.orderTableId = orderTable.id

            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { orderService.create(request) }
        }

        @Test
        fun `메뉴 가격이 주문아이템의 요청 가격과 다를 경우 실패`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = BigDecimal.valueOf(19999)
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.EAT_IN
            request.orderLineItems = listOf(orderLineItem)
            request.orderTableId = orderTable.id


            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            `when`(menuRepository.findById(menu.id))
                .thenReturn(Optional.of(menu))

            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { orderService.create(request) }
        }

        @Test
        fun `정상적인 배송 주문 성공`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.DELIVERY
            request.orderLineItems = listOf(orderLineItem)
            request.deliveryAddress = "test address"


            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            `when`(menuRepository.findById(menu.id))
                .thenReturn(Optional.of(menu))

            Assertions.assertThatCode { orderService.create(request) }
                .doesNotThrowAnyException()
        }

        @Test
        fun `배달 주문의 배송지 없을 경우 실패`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.DELIVERY
            request.orderLineItems = listOf(orderLineItem)

            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            `when`(menuRepository.findById(menu.id))
                .thenReturn(Optional.of(menu))

            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { orderService.create(request) }
        }

        @Test
        fun `메뉴가 전시종료 상태인 경우 실패`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = hiddenMenu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = 3

            val request = Order()
            request.type = OrderType.DELIVERY
            request.orderLineItems = listOf(orderLineItem)

            `when`(menuRepository.findAllByIdIn(listOf(hiddenMenu.id)))
                .thenReturn(listOf(hiddenMenu))

            `when`(menuRepository.findById(hiddenMenu.id))
                .thenReturn(Optional.of(hiddenMenu))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.create(request) }
        }

        @Test
        fun `주문아이템의 수량이 0보다 작을 경우 실패`() {
            val orderLineItem = OrderLineItem()
            orderLineItem.menuId = menu.id
            orderLineItem.price = menu.menuProducts[0].product.price
            orderLineItem.quantity = -1

            val request = Order()
            request.type = OrderType.DELIVERY
            request.orderLineItems = listOf(orderLineItem)
            request.orderTableId = orderTable.id


            `when`(menuRepository.findAllByIdIn(listOf(menu.id)))
                .thenReturn(listOf(menu))

            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { orderService.create(request) }
        }
    }

    @Nested
    @DisplayName("주문 접수")
    inner class `주문 접수` {
        @Test
        fun `정상적인 배달 주문 접수`() {
            val order = OrderSpec.of()

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            val result = orderService.accept(order.id)

            Assertions.assertThat(result.status).isEqualTo(OrderStatus.ACCEPTED)

            verify(kitchenridersClient, atLeastOnce()).requestDelivery(eq(order.id), any(), eq(order.deliveryAddress))
        }

        @Test
        fun `배달 주문 상태가 WAITING이 아닐 경우 실패`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.SERVED,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.accept(order.id) }
        }
    }

    @Nested
    @DisplayName("주문 전달")
    inner class `주문 전달` {
        @Test
        fun `매장 주문 상태가 ACCEPTED가 아닐 경우 실패`() {
            val order = OrderSpec.of(
                type = OrderType.EAT_IN,
                status = OrderStatus.WAITING,
                orderTable = OrderTableSpec.of()
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.serve(order.id) }
        }
    }

    @Nested
    @DisplayName("주문 완료")
    inner class `주문 완료` {
        @Test
        fun `정상적인 매장 주문 성공`() {
            val order = OrderSpec.of(
                type = OrderType.EAT_IN,
                status = OrderStatus.SERVED,
                orderTable = OrderTableSpec.of(occupied = true, numberOfGuests = 3)
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))
            `when`(orderRepository.existsByOrderTableAndStatusNot(order.orderTable, OrderStatus.COMPLETED))
                .thenReturn(false)

            val result = orderService.complete(order.id)

            Assertions.assertThat(result.status).isEqualTo(OrderStatus.COMPLETED)
            Assertions.assertThat(result.orderTable.isOccupied).isFalse()
            Assertions.assertThat(result.orderTable.numberOfGuests).isZero()
        }

        @Test
        fun `매장 주문 상태가 SERVED가 아닐 경우 실패`() {
            val order = OrderSpec.of(
                type = OrderType.EAT_IN,
                status = OrderStatus.ACCEPTED,
                orderTable = OrderTableSpec.of()
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.complete(order.id) }
        }

        @Test
        fun `정상적인 배달 주문 성공`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.DELIVERED,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            val result = orderService.complete(order.id)
            Assertions.assertThat(result.status).isEqualTo(OrderStatus.COMPLETED)
        }

        @Test
        fun `배달 주문 상태 DELIVERED이 아닐 경우 실패`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.DELIVERING,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.complete(order.id) }
        }
    }

    @Nested
    @DisplayName("배송 시작")
    inner class `배송 시작` {
        @Test
        fun `정상적인 배송 시작 성공`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.SERVED,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            val result = orderService.startDelivery(order.id)
            Assertions.assertThat(result.status).isEqualTo(OrderStatus.DELIVERING)
        }

        @Test
        fun `배달 주문의 상태가 SERVED가 아닐 경우 실패`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.WAITING,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.startDelivery(order.id) }
        }
    }

    @Nested
    @DisplayName("배송 완료")
    inner class `배송 완료` {
        @Test
        fun `정상적인 배송 완료 성공`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.DELIVERING,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            val result = orderService.completeDelivery(order.id)
            Assertions.assertThat(result.status).isEqualTo(OrderStatus.DELIVERED)
        }

        @Test
        fun `주문의 상태가 DELIVERING이 아닐 경우 실패`() {
            val order = OrderSpec.of(
                type = OrderType.DELIVERY,
                status = OrderStatus.SERVED,
            )

            `when`(orderRepository.findById(order.id))
                .thenReturn(Optional.of(order))

            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderService.completeDelivery(order.id) }
        }
    }

}
