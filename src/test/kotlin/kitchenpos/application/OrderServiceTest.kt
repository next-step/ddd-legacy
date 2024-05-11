package kitchenpos.application

import domain.OrderFixtures.makeOrderOne
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Order
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTableRepository
import kitchenpos.domain.OrderType
import kitchenpos.infra.KitchenridersClient
import org.junit.jupiter.api.assertThrows
import java.util.*

class OrderServiceTest : DescribeSpec() {
    init {
        describe("OrderService 클래스의") {
            val orderRepository = mockk<OrderRepository>()
            val menuRepository = mockk<MenuRepository>()
            val orderTableRepository = mockk<OrderTableRepository>()
            val kitchenridersClient = mockk<KitchenridersClient>()

            val orderService =
                OrderService(
                    orderRepository,
                    menuRepository,
                    orderTableRepository,
                    kitchenridersClient,
                )

            describe("create 메서드는") {
                context("정상적인 주문이 주어졌을 때") {
                    val createOrderRequest = makeOrderOne()
                    it("주문을 생성한다") {
                        every { orderRepository.save(any<Order>()) } returns createOrderRequest
                        every { menuRepository.findAllByIdIn(any<List<UUID>>()) } returns
                            createOrderRequest.orderLineItems.map {
                                it.menu
                            }
                        every { menuRepository.findById(createOrderRequest.orderLineItems[0].menu.id) } returns
                            Optional.of(createOrderRequest.orderLineItems[0].menu)

                        val result = orderService.create(createOrderRequest)

                        result.type shouldBe createOrderRequest.type
                    }
                }

                context("주문 유형이 null일 때") {
                    val nullTypeRequest =
                        Order().apply {
                            this.id = UUID.randomUUID()
                            this.type = null
                        }
                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            orderService.create(nullTypeRequest)
                        }
                    }
                }

                context("주문 항목이 없거나 비어있을 때") {
                    val emptyItemsRequest =
                        Order().apply {
                            this.id = UUID.randomUUID()
                            this.type = OrderType.EAT_IN
                            this.orderLineItems = emptyList()
                        }

                    it("IllegalArgumentException을 던진다") {
                        assertThrows<IllegalArgumentException> {
                            orderService.create(emptyItemsRequest)
                        }
                    }
                }

                context("주문 타입이 매장 내 식사가 아닌 경우에 개수가 0보다 작으면") {
                    it("IllegalArgumentException을 던진다") {
                        val orderRequest =
                            makeOrderOne().apply {
                                this.orderLineItems[0].quantity = -1
                            }
                        every {
                            menuRepository.findAllByIdIn(orderRequest.orderLineItems.map { it.menu.id })
                        } returns orderRequest.orderLineItems.map { it.menu }

                        assertThrows<IllegalArgumentException> {
                            orderService.create(orderRequest)
                        }
                    }
                }

                context("미노출 메뉴가 있으면") {
                    val orderRequest = makeOrderOne()

                    every { orderRepository.save(any<Order>()) } returns orderRequest
                    every { menuRepository.findById(orderRequest.orderLineItems[0].menu.id) } returns
                        Optional.of(orderRequest.orderLineItems[0].menu)
                    every {
                        menuRepository.findAllByIdIn(orderRequest.orderLineItems.map { it.menu.id })
                    } returns orderRequest.orderLineItems.map { it.menu.apply { this.isDisplayed = false } }
                    it("IllegalStateException 던진다") {
                        assertThrows<IllegalStateException> {
                            orderService.create(orderRequest)
                        }
                    }
                }
            }

            describe("accept 메소드는") {
                context("대기 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    it("IllegalStateException를 발생시킨다") {
                        val order = makeOrderOne()
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        assertThrows<IllegalStateException> {
                            orderService.accept(order.id)
                        }
                    }
                }

                context("대기 상태이며 배달 타입의 주문이 주어지면") {
                    it("kitchenriders서비스에 배달 요청을 보내고, 주문의 상태를 수락으로 변경한다") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.WAITING
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)
                        every { kitchenridersClient.requestDelivery(any(), any(), any()) } just runs

                        val result = orderService.accept(order.id)
                        result.status shouldBe OrderStatus.ACCEPTED
                    }
                }
            }

            describe("serve 메소드는") {
                context("수락 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    it("IllegalStateException를 발생시킨다") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.WAITING
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        assertThrows<IllegalStateException> {
                            orderService.serve(order.id)
                        }
                    }
                }

                context("수락 상태의 주문 아이디가 주어지면") {
                    it("SERVED 상태로 변경한다.") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.ACCEPTED
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        orderService.serve(order.id)

                        order.status shouldBe OrderStatus.SERVED
                    }
                }
            }
            describe("startDelivery 메소드는") {
                context("정삭적인 주문 아이디가 주어지면") {
                    it("주문을 찾아서 배달을 시작한다.") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.SERVED
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        orderService.startDelivery(order.id)

                        order.status shouldBe OrderStatus.DELIVERING
                    }
                }

                context("배달 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    it("IllegalStateException를 발생시킨다") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.WAITING
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        assertThrows<IllegalStateException> {
                            orderService.startDelivery(order.id)
                        }
                    }
                }

                context("배달 타입이 아닌 주문 아이디가 주어지면") {
                    it("IllegalStateException를 반환한다.") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.SERVED
                                this.type = OrderType.EAT_IN
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        assertThrows<IllegalStateException> {
                            orderService.startDelivery(order.id)
                        }
                    }
                }

                context("배달 상태의 주문 아이디가 주어지면") {
                    it("DELIVERING 상태로 변경한다.") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.SERVED
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        orderService.startDelivery(order.id)

                        order.status shouldBe OrderStatus.DELIVERING
                    }
                }
            }

            describe("completeDelivery 메소드는") {
                context("정상적인 상태의 주문 아이디가 주어지면") {
                    it("주문을 찾아 배달을 완료한다.") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.DELIVERING
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        orderService.completeDelivery(order.id)

                        order.status shouldBe OrderStatus.DELIVERED
                    }
                }

                context("배달 중 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    it("IllegalStateException를 발생시킨다") {
                        val order =
                            makeOrderOne().apply {
                                this.status = OrderStatus.WAITING
                            }
                        every { orderRepository.findById(any()) } returns Optional.of(order)

                        assertThrows<IllegalStateException> {
                            orderService.completeDelivery(order.id)
                        }
                    }
                }
            }

            describe("complete 메소드는") {
                context("정상적인 주문 아이디가 주어지면") {
                    val deliveryTypeOrder =
                        makeOrderOne().apply {
                            this.id = UUID.randomUUID()
                            this.type = OrderType.DELIVERY
                            this.status = OrderStatus.DELIVERED
                        }

                    val eatInTypeOrder =
                        makeOrderOne().apply {
                            this.id = UUID.randomUUID()
                            this.type = OrderType.EAT_IN
                            this.status = OrderStatus.SERVED
                        }

                    val takeOutOrder =
                        makeOrderOne().apply {
                            this.id = UUID.randomUUID()
                            this.type = OrderType.TAKEOUT
                            this.status = OrderStatus.SERVED
                        }
                    every { orderRepository.findById(deliveryTypeOrder.id) } returns Optional.of(deliveryTypeOrder)
                    every { orderRepository.findById(eatInTypeOrder.id) } returns Optional.of(eatInTypeOrder)
                    every { orderRepository.findById(takeOutOrder.id) } returns Optional.of(takeOutOrder)
                    every {
                        orderRepository.existsByOrderTableAndStatusNot(eatInTypeOrder.orderTable, OrderStatus.COMPLETED)
                    } returns true

                    it("주문을 찾아 완료한다.") {
                        val deliveryOrderResult = orderService.complete(deliveryTypeOrder.id)
                        val eatInOrderResult = orderService.complete(eatInTypeOrder.id)
                        val takeOutOrderResult = orderService.complete(takeOutOrder.id)

                        deliveryOrderResult.status shouldBe OrderStatus.COMPLETED
                        eatInOrderResult.status shouldBe OrderStatus.COMPLETED
                        takeOutOrderResult.status shouldBe OrderStatus.COMPLETED
                    }
                }

                context("배달 타입인데 배달 완료 상태가 아닌 주문의 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.type = OrderType.DELIVERY
                            this.status = OrderStatus.WAITING
                        }
                    every { orderRepository.findById(any()) } returns Optional.of(order)

                    it("IllegalStateException를 발생시킨다") {
                        assertThrows<IllegalStateException> {
                            orderService.complete(order.id)
                        }
                    }
                }

                context("배달 타입이 아닌데 제공됨 상태가 아닌 주문의 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.type = OrderType.EAT_IN
                            this.status = OrderStatus.DELIVERING
                        }
                    every { orderRepository.findById(any()) } returns Optional.of(order)

                    it("IllegalStateException를 발생시킨다") {

                        assertThrows<IllegalStateException> {
                            orderService.complete(order.id)
                        }
                    }
                }
            }
        }
    }
}
