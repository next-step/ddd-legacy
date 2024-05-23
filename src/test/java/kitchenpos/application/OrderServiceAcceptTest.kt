package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.clearMocks
import io.mockk.spyk
import io.mockk.verify
import kitchenpos.domain.Menu
import kitchenpos.domain.Order
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus.ACCEPTED
import kitchenpos.domain.OrderStatus.WAITING
import kitchenpos.domain.OrderType.DELIVERY
import kitchenpos.domain.OrderType.TAKEOUT
import kitchenpos.infra.KitchenridersClient
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeOrderRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.MenuFixtures
import kitchenpos.testsupport.OrderFixtures
import kitchenpos.testsupport.ProductFixtures

class OrderServiceAcceptTest : ShouldSpec({
    lateinit var savedOrder: Order
    lateinit var savedDisplayedMenu: Menu
    lateinit var orderRepository: OrderRepository
    val kitchenridersClient = spyk(KitchenridersClient())
    lateinit var service: OrderService

    beforeTest {
        clearMocks(kitchenridersClient)

        orderRepository = FakeOrderRepository()
        val menuRepository = FakeMenuRepository()
        val orderTableRepository = FakeOrderTableRepository()

        service = OrderService(
            orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        )

        savedDisplayedMenu = menuRepository.save(
            MenuFixtures.createMenu(
                product = ProductFixtures.createProduct(),
                name = "test-displayed-menu-name",
                isDisplayed = true
            )
        )

        savedOrder = orderRepository.save(
            OrderFixtures.createOrder(
                type = TAKEOUT,
                status = WAITING,
                menus = listOf(savedDisplayedMenu)
            )
        )
    }

    context("주문 접수") {
        context("배달 주문") {
            should("성공 - 배달 주문이 접수되는 경우") {
                // given
                val deliveryOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = DELIVERY,
                        status = WAITING,
                        menus = listOf(savedDisplayedMenu),
                        deliveryAddress = "test-delivery-address"
                    )
                )
                val orderId = deliveryOrder.id

                // when
                val order = service.accept(orderId)

                // then
                order.status shouldBe ACCEPTED
                verify(exactly = 1) {
                    kitchenridersClient.requestDelivery(any(), any(), any())
                }
            }
        }

        should("성공") {
            // given
            val orderId = savedOrder.id

            // when
            val order = service.accept(orderId)

            // then
            order.status shouldBe ACCEPTED
            verify(exactly = 0) {
                kitchenridersClient.requestDelivery(any(), any(), any())
            }
        }

        should("실패 - 대기중 상태가 아닌 경우") {
            // given
            val acceptedOrder = orderRepository.save(
                OrderFixtures.createOrder(
                    type = TAKEOUT,
                    status = ACCEPTED,
                    menus = listOf(savedDisplayedMenu),
                    deliveryAddress = "test-delivery-address"
                )
            )
            val orderId = acceptedOrder.id

            // when
            val exception = shouldThrowAny {
                service.accept(orderId)
            }

            // then
            exception.shouldBeTypeOf<IllegalStateException>()
            verify(exactly = 0) {
                kitchenridersClient.requestDelivery(any(), any(), any())
            }
        }
    }
})
