package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kitchenpos.domain.Order
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus.DELIVERED
import kitchenpos.domain.OrderStatus.DELIVERING
import kitchenpos.domain.OrderStatus.SERVED
import kitchenpos.domain.OrderStatus.WAITING
import kitchenpos.domain.OrderType.DELIVERY
import kitchenpos.domain.OrderType.TAKEOUT
import kitchenpos.infra.KitchenridersClient
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeOrderRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.OrderFixtures

class OrderServiceCompleteDeliveryTest : ShouldSpec({
    lateinit var savedOrder: Order
    lateinit var orderRepository: OrderRepository
    lateinit var service: OrderService

    beforeTest {
        orderRepository = FakeOrderRepository()

        service = OrderService(
            orderRepository,
            FakeMenuRepository(),
            FakeOrderTableRepository(),
            KitchenridersClient()
        )

        savedOrder = orderRepository.save(
            OrderFixtures.createOrder(
                type = DELIVERY,
                status = DELIVERING,
                menus = listOf()
            )
        )
    }

    context("주문 배달 완료") {
        should("성공") {
            // given
            val orderId = savedOrder.id

            // when
            val order = service.completeDelivery(orderId)

            // then
            order.status shouldBe DELIVERED
        }

        should("실패 - 주문이 배달 중 상태가 아닌 경우") {
            // given
            val invalidOrder = orderRepository.save(
                OrderFixtures.createOrder(
                    type = DELIVERY,
                    status = WAITING,
                    menus = listOf()
                )
            )
            val orderId = invalidOrder.id

            // when
            val exception = shouldThrowAny {
                service.completeDelivery(orderId)
            }

            // then
            exception.shouldBeTypeOf<IllegalStateException>()
        }
    }
})
