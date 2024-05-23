package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus.ACCEPTED
import kitchenpos.domain.OrderStatus.COMPLETED
import kitchenpos.domain.OrderStatus.DELIVERED
import kitchenpos.domain.OrderStatus.DELIVERING
import kitchenpos.domain.OrderStatus.SERVED
import kitchenpos.domain.OrderTableRepository
import kitchenpos.domain.OrderType.DELIVERY
import kitchenpos.domain.OrderType.EAT_IN
import kitchenpos.domain.OrderType.TAKEOUT
import kitchenpos.infra.KitchenridersClient
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeOrderRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.OrderFixtures
import kitchenpos.testsupport.OrderTableFixtures

class OrderServiceCompleteTest : ShouldSpec({
    lateinit var orderTableRepository: OrderTableRepository
    lateinit var orderRepository: OrderRepository
    lateinit var service: OrderService

    beforeTest {
        orderTableRepository = FakeOrderTableRepository()
        orderRepository = FakeOrderRepository()

        service = OrderService(
            orderRepository,
            FakeMenuRepository(),
            orderTableRepository,
            KitchenridersClient()
        )
    }

    context("주문 완료") {
        context("배달 주문") {
            should("성공") {
                // given
                val deliveredOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = DELIVERY,
                        status = DELIVERED,
                        menus = listOf()
                    )
                )

                // when
                val order = service.complete(deliveredOrder.id)

                // then
                order.status shouldBe COMPLETED
            }

            should("실패 - 주문이 배달 완료 상태가 아닌 경우") {
                // given
                val deliveredOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = DELIVERY,
                        status = DELIVERING,
                        menus = listOf()
                    )
                )

                // when
                val exception = shouldThrowAny {
                    service.complete(deliveredOrder.id)
                }

                // then
                exception.shouldBeTypeOf<IllegalStateException>()
            }
        }

        context("포장 주문") {
            should("성공") {
                // given
                val servedOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = TAKEOUT,
                        status = SERVED,
                        menus = listOf()
                    )
                )

                // when
                val order = service.complete(servedOrder.id)

                // then
                order.status shouldBe COMPLETED
            }

            should("실패 - 주문 전달 상태가 아닌 경우") {
                // given
                val acceptedOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = TAKEOUT,
                        status = ACCEPTED,
                        menus = listOf()
                    )
                )

                // when
                val exception = shouldThrowAny {
                    service.complete(acceptedOrder.id)
                }

                // then
                exception.shouldBeTypeOf<IllegalStateException>()
            }
        }

        context("매장 주문") {
            should("성공") {
                // given
                val occupiedOrderTable = orderTableRepository.save(
                    OrderTableFixtures.createOrderTable()
                        .apply { isOccupied = true }
                )
                val acceptedOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = EAT_IN,
                        status = SERVED,
                        menus = listOf(),
                        orderTable = occupiedOrderTable
                    )
                )

                // when
                val order = service.complete(acceptedOrder.id)

                // then
                order.status shouldBe COMPLETED
            }

            should("실패 - 주문 전달 상태가 아닌 경우") {
                // given
                val acceptedOrder = orderRepository.save(
                    OrderFixtures.createOrder(
                        type = EAT_IN,
                        status = ACCEPTED,
                        menus = listOf(),
                        orderTable = orderTableRepository.save(
                            OrderTableFixtures.createOrderTable()
                        )
                    )
                )

                // when
                val exception = shouldThrowAny {
                    service.complete(acceptedOrder.id)
                }

                // then
                exception.shouldBeTypeOf<IllegalStateException>()
            }
        }
    }
})
