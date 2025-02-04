package kitchenops.application

import io.kotest.datatest.withData
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.application.OrderService
import kitchenpos.domain.*
import kitchenpos.infra.KitchenridersClient
import spec.BaseUnitSpec
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.NoSuchElementException

internal class OrderServiceTest : BaseUnitSpec({

    val orderRepository = mockk<OrderRepository>()
    val menuRepository = mockk<MenuRepository>()
    val orderTableRepository = mockk<OrderTableRepository>()
    val kitchenridersClient = mockk<KitchenridersClient>()

    val sut = OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient)

    context("식당은 주문을 받을 수 있다") {
        test("주문의 종류를 지정하지 않으면 주문을 받을 수 없다") {
            // given
            val request = createOrder(type = null)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("주문할 메뉴를 지정하지 않으면 주문을 받을 수 없다") {
            // given
            val request = createOrder(type = OrderType.EAT_IN, orderLineItems = null)

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("주문할 메뉴가 비어있으면 주문을 받을 수 없다") {
            // given
            val request = createOrder(type = OrderType.EAT_IN, orderLineItems = emptyList())

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("식당에 없는 메뉴는 주문할 수 없다") {
            // given
            val menuId = UUID.randomUUID()
            every { menuRepository.findAllByIdIn(listOf(menuId)) } returns emptyList()

            val request = createOrder(
                type = OrderType.EAT_IN,
                orderLineItems = listOf(OrderLineItem().apply { this.menuId = menuId })
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        val notEatInTypes = OrderType.entries.filterNot { it == OrderType.EAT_IN }
        withData(nameFn = { "매장 내 식사가 아니라면($it) 음식의 갯수가 0 미만이면 주문할 수 없다" }, notEatInTypes) { orderType ->
            // given
            val menu = createMenu()
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = -1)

            val request = createOrder(type = orderType, orderLineItems = listOf(orderLineItem))

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("숨겨진 메뉴는 주문할 수 없다") {
            // given
            val menu = createMenu(displayed = false)
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1)

            val request = createOrder(type = OrderType.EAT_IN, orderLineItems = listOf(orderLineItem))

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("메뉴의 가격과 주문한 메뉴의 가격이 동일하지 않으면 주문할 수 없다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("16000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("12000"))

            val request = createOrder(type = OrderType.EAT_IN, orderLineItems = listOf(orderLineItem))

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("배달일경우 배달 주소를 지정하지 않으면 주문할 수 없다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("16000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("16000"))

            val request = createOrder(
                type = OrderType.DELIVERY,
                orderLineItems = listOf(orderLineItem),
                deliveryAddress = null
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("배달일경우 배달 주소가 비어있으면 주문을 받을 수 없다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("16000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("16000"))

            val request = createOrder(
                type = OrderType.DELIVERY,
                orderLineItems = listOf(orderLineItem),
                deliveryAddress = "",
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("매장 내 식사일경우 주문 테이블이 존재하지 않으면 주문을 받을 수 없다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("10000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderTableId = UUID.randomUUID()
            every { orderTableRepository.findById(orderTableId) } returns Optional.empty()

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("10000"))

            val request = createOrder(
                type = OrderType.EAT_IN,
                orderTable = createOrderTable(id = orderTableId),
                orderLineItems = listOf(orderLineItem),
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        // TODO: 주문 테이블이 비어야 먹을 수 있는데 isOccupied == true일경우 오류로 하고 직접 sit으로 바꿔야 하지 않을까 싶다
        // TODO: 주문 테이블에 먼저 sit 호출하고 create를 호출한다고 하면 맞는데 sit 호출과 create 사이에 시간이 걸리면 동시성에 문제생길 것 같은데...
        test("매장 내 식사일경우 주문 테이블이 비어있지 않으면 주문을 받을 수 없다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("10000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderTable = createOrderTable(occupied = false)
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("10000"))

            val request = createOrder(
                type = OrderType.EAT_IN,
                orderTable = orderTable,
                orderLineItems = listOf(orderLineItem),
            )

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("배달 주문을 받을 수 있다") {
            // given
            val menu1 = createMenu(id = UUID.randomUUID(), displayed = true, price = BigDecimal("10000"))
            val menu2 = createMenu(id = UUID.randomUUID(), displayed = true, price = BigDecimal("4000"))
            every { menuRepository.findAllByIdIn(listOf(menu1.id, menu2.id)) } returns listOf(menu1, menu2)
            every { menuRepository.findById(menu1.id) } returns Optional.of(menu1)
            every { menuRepository.findById(menu2.id) } returns Optional.of(menu2)

            val orderLineItem1 = createOrderLineItem(menu = menu1, quantity = 1, price = BigDecimal("10000"))
            val orderLineItem2 = createOrderLineItem(menu = menu2, quantity = 2, price = BigDecimal("4000"))

            val request = createOrder(
                type = OrderType.DELIVERY,
                deliveryAddress = "address",
                orderLineItems = listOf(orderLineItem1, orderLineItem2),
            )

            every {
                orderRepository.save(match {
                    it.type == OrderType.DELIVERY && it.status == OrderStatus.WAITING && it.orderLineItems.size == 2 && it.deliveryAddress == "address" && it.orderTable == null
                })
            } returns createOrder(
                type = request.type,
                status = OrderStatus.WAITING,
                orderLineItems = request.orderLineItems,
                deliveryAddress = request.deliveryAddress,
                orderTable = null,
            )

            // when
            val actual = sut.create(request)

            // then
            actual should {
                it.type shouldBe OrderType.DELIVERY
                it.status shouldBe OrderStatus.WAITING
                it.orderLineItems shouldBe listOf(orderLineItem1, orderLineItem2)
                it.deliveryAddress shouldBe "address"
                it.orderTable shouldBe null
            }

            verify(exactly = 1) { orderRepository.save(any()) }
        }

        test("포장 주문을 받을 수 있다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("1000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("1000"))

            val request = createOrder(type = OrderType.TAKEOUT, orderLineItems = listOf(orderLineItem))

            every {
                orderRepository.save(match {
                    it.type == OrderType.TAKEOUT && it.status == OrderStatus.WAITING && it.orderLineItems.size == 1
                })
            } returns createOrder(
                type = request.type,
                status = OrderStatus.WAITING,
                orderLineItems = request.orderLineItems,
                deliveryAddress = null,
                orderTable = null,
            )

            // when
            val actual = sut.create(request)

            // then
            actual should {
                it.type shouldBe OrderType.TAKEOUT
                it.status shouldBe OrderStatus.WAITING
                it.orderLineItems shouldBe listOf(orderLineItem)
                it.deliveryAddress shouldBe null
                it.orderTable shouldBe null
            }

            verify(exactly = 1) { orderRepository.save(any()) }
        }

        test("매장 내 식사 주문을 받을 수 있다") {
            // given
            val menu = createMenu(displayed = true, price = BigDecimal("3000"))
            every { menuRepository.findAllByIdIn(listOf(menu.id)) } returns listOf(menu)
            every { menuRepository.findById(menu.id) } returns Optional.of(menu)

            val orderTable = createOrderTable(occupied = true)
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            val orderLineItem = createOrderLineItem(menu = menu, quantity = 1, price = BigDecimal("3000"))

            val request = createOrder(
                type = OrderType.EAT_IN,
                orderLineItems = listOf(orderLineItem),
                orderTable = orderTable,
            )

            every {
                orderRepository.save(match {
                    it.type == OrderType.EAT_IN && it.status == OrderStatus.WAITING && it.orderLineItems.size == 1 && it.orderTable == orderTable
                })
            } returns createOrder(
                type = request.type,
                status = OrderStatus.WAITING,
                orderLineItems = request.orderLineItems,
                deliveryAddress = null,
                orderTable = orderTable,
            )

            // when
            val actual = sut.create(request)

            // then
            actual should {
                it.type shouldBe OrderType.EAT_IN
                it.status shouldBe OrderStatus.WAITING
                it.orderLineItems shouldBe listOf(orderLineItem)
                it.deliveryAddress shouldBe null
                it.orderTable shouldBe orderTable
            }

            verify(exactly = 1) { orderRepository.save(any()) }
        }
    }

    context("주문을 전체 조회할 수 있다") {
        test("전체 조회한다") {
            // given
            val order1 = createOrder()
            val order2 = createOrder()
            every { orderRepository.findAll() } returns listOf(order1, order2)

            // when
            val actual = sut.findAll()

            // then
            actual shouldBe listOf(order1, order2)
        }
    }

    context("주문을 수락한다") {
        test("존재하지 않는 주문은 수락할 수 없다") {
            // given
            val orderId = UUID.randomUUID()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.accept(orderId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        val notWaitingStatus = OrderStatus.entries.filterNot { it == OrderStatus.WAITING }
        withData(nameFn = { "주문 대기 상태가 아닐경우($it) 수락할 수 없다" }, notWaitingStatus) { orderStatus ->
            // given
            val order = createOrder(status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.accept(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("배달 주문일경우 배달 서비스에 배달을 요청한다") {
            // given
            val menu = createMenu(price = BigDecimal("1000"))
            val orderLineItem = createOrderLineItem(menu = menu, quantity = 2)
            val order = createOrder(
                id = UUID.randomUUID(),
                status = OrderStatus.WAITING,
                type = OrderType.DELIVERY,
                orderLineItems = listOf(orderLineItem),
                deliveryAddress = "address",
            )
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            justRun { kitchenridersClient.requestDelivery(order.id, BigDecimal("2000"), "address") }

            // when
            val actual = sut.accept(order.id)

            // then
            actual.status shouldBe OrderStatus.ACCEPTED
            verify(exactly = 1) { kitchenridersClient.requestDelivery(any(), any(), any()) }
        }

        val notDeliveryType = OrderType.entries.filterNot { it == OrderType.DELIVERY }
        withData(nameFn = { "배달 주문이 아닐경우($it) 주문을 수락한다" }, notDeliveryType) { orderType ->
            // given
            val order = createOrder(status = OrderStatus.WAITING, type = orderType)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = sut.accept(order.id)

            // then
            actual.status shouldBe OrderStatus.ACCEPTED
        }
    }

    context("주문을 서빙한다") {
        test("존재하지 않는 주문은 서빙할 수 없다") {
            // given
            val orderId = UUID.randomUUID()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.serve(orderId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        val notAcceptedStatusList = OrderStatus.entries.filterNot { it == OrderStatus.ACCEPTED }
        withData(nameFn = { "수락 상태가 아닌 주문은($it) 서빙할 수 없다" }, notAcceptedStatusList) { orderStatus ->
            // given
            val order = createOrder(status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.serve(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("요리가 모두 준비되면 서빙한다") {
            // given
            val order = createOrder(status = OrderStatus.ACCEPTED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = sut.serve(order.id)

            // then
            actual.status shouldBe OrderStatus.SERVED
        }
    }

    context("배달을 시작한다") {
        test("존재하지 않는 주문은 배달을 시작할 수 없다") {
            // given
            val orderId = UUID.randomUUID()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.startDelivery(orderId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        val notDeliveryTypeList = OrderType.entries.filterNot { it == OrderType.DELIVERY }
        withData(nameFn = { "배달 주문이 아니면($it) 배달을 시작할 수 없다" }, notDeliveryTypeList) { orderType ->
            // given
            val order = createOrder(type = orderType, status = OrderStatus.SERVED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.startDelivery(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        val notServedStatusList = OrderStatus.entries.filterNot { it == OrderStatus.SERVED }
        withData(nameFn = { "서빙 상태가 아니라면($it) 배달을 시작할 수 없다" }, notServedStatusList) { orderStatus ->
            // given
            val order = createOrder(type = OrderType.DELIVERY, status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.startDelivery(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("주문이 서빙된 상태면 배달을 시작한다") {
            // given
            val order = createOrder(type = OrderType.DELIVERY, status = OrderStatus.SERVED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = sut.startDelivery(order.id)

            // then
            actual.status shouldBe OrderStatus.DELIVERING
        }
    }

    context("배달을 완료한다") {
        test("존재하지 않는 주문은 배달을 완료할 수 없다") {
            // given
            val orderId = UUID.randomUUID()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.completeDelivery(orderId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        val notDeliveringStatusList = OrderStatus.entries.filterNot { it == OrderStatus.DELIVERING }
        withData(nameFn = { "배달 중 상태가 아니라면($it) 배달을 완료할 수 없다" }, notDeliveringStatusList) { orderStatus ->
            // given
            val order = createOrder(status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.completeDelivery(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("라이더가 배달을 완료하면 배달을 완료 처리한다") {
            // given
            val order = createOrder(status = OrderStatus.DELIVERING)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = sut.completeDelivery(order.id)

            // then
            actual.status shouldBe OrderStatus.DELIVERED
        }
    }

    context("주문을 완료 처리한다") {
        test("존재하지 않는 주문은 완료 처리할 수 없다") {
            // given
            val orderId = UUID.randomUUID()
            every { orderRepository.findById(orderId) } returns Optional.empty()

            // when
            val actual = runCatching { sut.complete(orderId) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        val notDeliveredStatusList = OrderStatus.entries.filterNot { it == OrderStatus.DELIVERED }
        withData(nameFn = { "배달 주문은 배달 완료 상태가 아니라면($it) 완료 처리할 수 없다" }, notDeliveredStatusList) { orderStatus ->
            // given
            val order = createOrder(type = OrderType.DELIVERY, status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.complete(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        val notServedStatusList = OrderStatus.entries.filterNot { it == OrderStatus.SERVED }
        withData(nameFn = { "포장 주문은 서빙 상태가 아니라면($it) 완료 처리할 수 없다" }, notServedStatusList) { orderStatus ->
            // given
            val order = createOrder(type = OrderType.TAKEOUT, status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.complete(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        withData(nameFn = { "매장 내 식사 주문은 서빙 상태가 아니라면($it) 완료 처리할 수 없다" }, notServedStatusList) { orderStatus ->
            // given
            val order = createOrder(type = OrderType.EAT_IN, status = orderStatus)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = runCatching { sut.complete(order.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("배달 주문은 배달 완료 상태면 주문을 완료 처리한다") {
            // given
            val order = createOrder(type = OrderType.DELIVERY, status = OrderStatus.DELIVERED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = sut.complete(order.id)

            // then
            actual.status shouldBe OrderStatus.COMPLETED
        }

        test("포장 주문은 서빙 상태면 주문을 완료 처리한다") {
            // given
            val order = createOrder(type = OrderType.TAKEOUT, status = OrderStatus.SERVED)
            every { orderRepository.findById(order.id) } returns Optional.of(order)

            // when
            val actual = sut.complete(order.id)

            // then
            actual.status shouldBe OrderStatus.COMPLETED
        }

        // TODO: orderStatus를 먼저 copmpleted로 변경하고 complete인 테이블이 있으면 정리하는데, status를 가장 마지막에 수정하는 코드로 바꾸면 테스트 수정 필요함
        test("매장 내 식사 주문은 서빙 상태면 주문을 완료 처리한다") {
            // given
            val orderTable = createOrderTable(id = UUID.randomUUID())
            val order = createOrder(type = OrderType.EAT_IN, status = OrderStatus.SERVED, orderTable = orderTable)
            every { orderRepository.findById(order.id) } returns Optional.of(order)
            every { orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED) } returns true

            // when
            val actual = sut.complete(order.id)

            // then
            actual.status shouldBe OrderStatus.COMPLETED
        }

        test("매장 내 식사 주문은 완료 상태가 아니었다면 주문 테이블을 정리한다") {
            // given
            val orderTable = createOrderTable(id = UUID.randomUUID(), occupied = true, numberOfGuests = 3)
            val order = createOrder(type = OrderType.EAT_IN, status = OrderStatus.SERVED, orderTable = orderTable)
            every { orderRepository.findById(order.id) } returns Optional.of(order)
            every { orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED) } returns false

            // when
            val actual = sut.complete(order.id)

            // then
            actual.status shouldBe OrderStatus.COMPLETED
            actual.orderTable.isOccupied shouldBe false
            actual.orderTable.numberOfGuests shouldBe 0
        }
    }
}) {
    companion object {
        fun createOrder(
            id: UUID? = null,
            type: OrderType? = null,
            status: OrderStatus? = null,
            orderDateTime: LocalDateTime? = null,
            orderLineItems: List<OrderLineItem>? = null,
            deliveryAddress: String? = null,
            orderTable: OrderTable? = null,
        ) = Order().apply {
            this.id = id
            this.type = type
            this.status = status
            this.orderDateTime = orderDateTime
            this.orderLineItems = orderLineItems
            this.deliveryAddress = deliveryAddress
            this.orderTable = orderTable
            this.orderTableId = orderTable?.id
        }

        fun createMenu(
            id: UUID? = null,
            price: BigDecimal? = null,
            displayed: Boolean = true,
        ) = Menu().apply {
            this.id = id
            this.price = price
            this.isDisplayed = displayed
        }

        fun createOrderLineItem(
            menu: Menu? = null,
            quantity: Long = 0,
            price: BigDecimal? = null,
        ) = OrderLineItem().apply {
            this.menuId = menu?.id
            this.menu = menu
            this.quantity = quantity
            this.price = price
        }

        fun createOrderTable(
            id: UUID? = null,
            occupied: Boolean = false,
            numberOfGuests: Int = 0,
        ) = OrderTable().apply {
            this.id = id
            this.isOccupied = occupied
            this.numberOfGuests = numberOfGuests
        }
    }
}
