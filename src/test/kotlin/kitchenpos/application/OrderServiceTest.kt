package kitchenpos.application

import domain.MenuFixtures.makeMenuOne
import domain.OrderFixtures.makeOrderOne
import domain.OrderTableFixtures.makeOrderTableOne
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kitchenpos.application.fake.FakeKitchenridersClient
import kitchenpos.application.fake.FakeMenuRepository
import kitchenpos.application.fake.FakeOrderRepository
import kitchenpos.application.fake.FakeOrderTableRepository
import kitchenpos.domain.Order
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderType
import org.junit.jupiter.api.assertThrows
import java.util.*

class OrderServiceTest : DescribeSpec() {
    init {
        describe("OrderService 클래스의") {
            val orderRepository = FakeOrderRepository()
            val menuRepository = FakeMenuRepository()
            val orderTableRepository = FakeOrderTableRepository()
            val kitchenridersClient = FakeKitchenridersClient()

            val orderService =
                OrderService(
                    orderRepository,
                    menuRepository,
                    orderTableRepository,
                    kitchenridersClient,
                )

            describe("create 메서드는") {
                context("정상적인 주문이 주어졌을 때") {
                    menuRepository.save(makeMenuOne())

                    val createOrderRequest = makeOrderOne()
                    it("주문을 생성한다") {
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

                        assertThrows<IllegalArgumentException> {
                            orderService.create(orderRequest)
                        }
                    }
                }

                context("미노출 메뉴가 있으면") {
                    menuRepository.save(
                        makeMenuOne().apply {
                            this.isDisplayed = false
                        },
                    )

                    val orderRequest = makeOrderOne()

                    it("IllegalStateException 던진다") {
                        assertThrows<IllegalStateException> {
                            orderService.create(orderRequest)
                        }
                    }
                }
            }

            describe("accept 메소드는") {
                context("대기 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    val order = makeOrderOne()
                    orderRepository.save(order)

                    it("IllegalStateException를 발생시킨다") {

                        assertThrows<IllegalStateException> {
                            orderService.accept(order.id)
                        }
                    }
                }

                context("대기 상태이며 배달 타입의 주문이 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.WAITING
                        }
                    orderRepository.save(order)

                    it("kitchenriders서비스에 배달 요청을 보내고, 주문의 상태를 수락으로 변경한다") {
                        val result = orderService.accept(order.id)
                        result.status shouldBe OrderStatus.ACCEPTED
                    }
                }
            }

            describe("serve 메소드는") {
                context("수락 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.WAITING
                        }
                    orderRepository.save(order)
                    it("IllegalStateException를 발생시킨다") {

                        assertThrows<IllegalStateException> {
                            orderService.serve(order.id)
                        }
                    }
                }

                context("수락 상태의 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.ACCEPTED
                        }
                    orderRepository.save(order)

                    it("SERVED 상태로 변경한다.") {
                        orderService.serve(order.id)

                        order.status shouldBe OrderStatus.SERVED
                    }
                }
            }
            describe("startDelivery 메소드는") {
                context("정삭적인 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.SERVED
                        }
                    orderRepository.save(order)

                    it("주문을 찾아서 배달을 시작한다.") {
                        orderService.startDelivery(order.id)

                        order.status shouldBe OrderStatus.DELIVERING
                    }
                }

                context("배달 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.WAITING
                        }
                    orderRepository.save(order)

                    it("IllegalStateException를 발생시킨다") {
                        assertThrows<IllegalStateException> {
                            orderService.startDelivery(order.id)
                        }
                    }
                }

                context("배달 타입이 아닌 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.SERVED
                            this.type = OrderType.EAT_IN
                        }
                    orderRepository.save(order)

                    it("IllegalStateException를 반환한다.") {
                        assertThrows<IllegalStateException> {
                            orderService.startDelivery(order.id)
                        }
                    }
                }

                context("배달 상태의 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.SERVED
                        }
                    orderRepository.save(order)

                    it("DELIVERING 상태로 변경한다.") {
                        orderService.startDelivery(order.id)

                        order.status shouldBe OrderStatus.DELIVERING
                    }
                }
            }

            describe("completeDelivery 메소드는") {
                context("정상적인 상태의 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.DELIVERING
                        }
                    orderRepository.save(order)

                    it("주문을 찾아 배달을 완료한다.") {
                        orderService.completeDelivery(order.id)

                        order.status shouldBe OrderStatus.DELIVERED
                    }
                }

                context("배달 중 상태의 주문이 아닌 주문 아이디가 주어지면") {
                    val order =
                        makeOrderOne().apply {
                            this.status = OrderStatus.WAITING
                        }
                    orderRepository.save(order)

                    it("IllegalStateException를 발생시킨다") {
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
                            this.orderTable = makeOrderTableOne()
                        }

                    val takeOutOrder =
                        makeOrderOne().apply {
                            this.id = UUID.randomUUID()
                            this.type = OrderType.TAKEOUT
                            this.status = OrderStatus.SERVED
                        }

                    orderRepository.save(deliveryTypeOrder)
                    orderRepository.save(eatInTypeOrder)
                    orderRepository.save(takeOutOrder)

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
                    orderRepository.save(order)

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
                    orderRepository.save(order)

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
