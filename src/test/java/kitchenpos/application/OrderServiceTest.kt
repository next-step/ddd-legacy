package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import kitchenpos.domain.FakeMenuRepository
import kitchenpos.domain.FakeOrderRepository
import kitchenpos.domain.FakeOrderTableRepository
import kitchenpos.domain.Order
import kitchenpos.domain.OrderStatus.ACCEPTED
import kitchenpos.domain.OrderStatus.COMPLETED
import kitchenpos.domain.OrderStatus.DELIVERED
import kitchenpos.domain.OrderStatus.DELIVERING
import kitchenpos.domain.OrderStatus.SERVED
import kitchenpos.domain.OrderStatus.WAITING
import kitchenpos.domain.OrderStatus.entries
import kitchenpos.domain.OrderType.DELIVERY
import kitchenpos.domain.OrderType.EAT_IN
import kitchenpos.domain.OrderType.TAKEOUT
import kitchenpos.infra.KitchenridersClient
import java.util.UUID

private val orderRepository = FakeOrderRepository()
private val menuRepository = FakeMenuRepository()
private val orderTableRepository = FakeOrderTableRepository()
private val kitchenridersClient = mockk<KitchenridersClient>()

private val orderService = OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient)

private fun Order.persistAll() = this.persistMenu().persistOrderTable().also { orderRepository.save(this) }

private fun Order.persistMenu() = this.also { this.orderLineItems.forEach { menuRepository.save(it.menu) } }

private fun Order.persistOrderTable() = this.also { orderTableRepository.save(this.orderTable) }

private val Int.won: Int get() = this
private val Int.pcs: Long get() = this.toLong()

class OrderServiceTest : BehaviorSpec({
    given("주문할 때") {
        `when`("입력 값이 정상이면") {
            val newOrder =
                buildOrder {
                    orderType(TAKEOUT)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistMenu()

            then("주문이 생성된다.") {
                with(orderService.create(newOrder)) {
                    id shouldNotBe null
                    status shouldBe WAITING
                }
            }
        }

        `when`("주문 타입이 존재하지 않으면") {
            val newOrder = buildOrder { type = null }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    orderService.create(newOrder)
                }
            }
        }

        `when`("주문 항목이 비어있으면") {
            val newOrder =
                buildOrder {
                    orderType(EAT_IN)
                    requestMenu {}
                }

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    orderService.create(newOrder)
                }
            }
        }

        `when`("매장 주문이 아닐떄 수량이 0보다 작은경우") {
            val newOrder =
                buildOrder {
                    orderType(TAKEOUT)
                    requestMenu {
                        item("메뉴", 1000.won, (-1).pcs)
                    }
                }.persistMenu()

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    orderService.create(newOrder)
                }
            }
        }

        `when`("메뉴 상태가 비노출인 경우") {
            val newOrder =
                buildOrder {
                    orderType(TAKEOUT)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs, false)
                    }
                }.persistMenu()

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderService.create(newOrder)
                }
            }
        }

        `when`("배달 주문일 때 주소가 존재하지 않으면") {
            val newOrder =
                buildOrder {
                    orderType(DELIVERY)
                    deliveryAddress(null)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistMenu()

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    orderService.create(newOrder)
                }
            }
        }

        `when`("매장 주문일 때 테이블이 사용중이라면") {
            val newOrder =
                buildOrder {
                    orderType(EAT_IN)
                    table(occupied = true)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs, false)
                    }
                }.persistMenu().persistOrderTable()

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderService.create(newOrder)
                }
            }
        }
    }

    given("주문을 수락할 때") {
        `when`("입력 값이 정상적인 경우") {
            val newOrder =
                buildOrder {
                    orderType(EAT_IN)
                    orderStatus(WAITING)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("주문이 수락된다.") {
                with(orderService.accept(newOrder.id)) {
                    status shouldBe ACCEPTED
                }
            }
        }

        `when`("주문이 존재하지 않으면") {
            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    orderService.accept(UUID.randomUUID())
                }
            }
        }

        `when`("주문 상태가 대기가 아닌 경우") {
            val orders =
                entries.filterNot { it == WAITING }.map { status ->
                    buildOrder {
                        orderType(EAT_IN)
                        orderStatus(status)
                        requestMenu {
                            item("메뉴", 1000.won, 1.pcs)
                        }
                    }.persistAll()
                }

            then("예외가 발생한다.") {
                orders.forEach { order ->
                    shouldThrow<IllegalStateException> {
                        orderService.accept(order.id)
                    }
                }
            }
        }
    }

    given("주문된 메뉴를 전달할 때") {
        `when`("정상 입력 값이 주어지면") {
            val newOrder =
                buildOrder {
                    orderType(EAT_IN)
                    orderStatus(ACCEPTED)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("메뉴가 전달된다.") {
                with(orderService.serve(newOrder.id)) {
                    status shouldBe SERVED
                }
            }
        }

        `when`("주문이 존재하지 않으면") {
            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    orderService.serve(UUID.randomUUID())
                }
            }
        }

        `when`("주문 상태가 접수가 아니라면") {
            val orders =
                entries.filterNot { it == ACCEPTED }.map { status ->
                    buildOrder {
                        orderType(EAT_IN)
                        orderStatus(status)
                        requestMenu {
                            item("메뉴", 1000.won, 1.pcs)
                        }
                    }.persistAll()
                }

            then("예외가 발생한다.") {
                orders.forEach { order ->
                    shouldThrow<IllegalStateException> {
                        orderService.serve(order.id)
                    }
                }
            }
        }
    }

    given("배달을 시작할 때") {
        `when`("정상 입력 값이 주어지면") {
            val newOrder =
                buildOrder {
                    orderType(DELIVERY)
                    orderStatus(SERVED)
                    deliveryAddress("주소")
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("배달이 시작된다.") {
                with(orderService.startDelivery(newOrder.id)) {
                    status shouldBe DELIVERING
                }
            }
        }

        `when`("주문이 존재하지 않으면") {
            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    orderService.startDelivery(UUID.randomUUID())
                }
            }
        }

        `when`("주문 타입이 배달이 아니면") {
            val order =
                buildOrder {
                    orderType(EAT_IN)
                    orderStatus(SERVED)
                    deliveryAddress("주소")
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderService.startDelivery(order.id)
                }
            }
        }

        `when`("주문 상태가 전달이 아니라면") {
            val orders =
                entries.filterNot { it == SERVED }.map { status ->
                    buildOrder {
                        orderType(DELIVERY)
                        orderStatus(status)
                        deliveryAddress("주소")
                        requestMenu {
                            item("메뉴", 1000.won, 1.pcs)
                        }
                    }.persistAll()
                }

            then("예외가 발생한다.") {
                orders.forEach { order ->
                    shouldThrow<IllegalStateException> {
                        orderService.startDelivery(order.id)
                    }
                }
            }
        }
    }

    given("배달이 완료되었을 떄") {
        `when`("정상 입력 값이 주어지면") {
            val newOrder =
                buildOrder {
                    orderType(DELIVERY)
                    orderStatus(DELIVERING)
                    deliveryAddress("주소")
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("배달이 완료된다.") {
                with(orderService.completeDelivery(newOrder.id)) {
                    status shouldBe DELIVERED
                }
            }
        }
    }

    given("주문이 존재하지 않으면") {
        then("예외가 발생한다.") {
            shouldThrow<NoSuchElementException> {
                orderService.completeDelivery(UUID.randomUUID())
            }
        }
    }

    given("주문 상태가 배달중이 아니라면") {
        val orders =
            entries.filterNot { it == DELIVERING }.map { status ->
                buildOrder {
                    orderType(DELIVERY)
                    orderStatus(status)
                    deliveryAddress("주소")
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()
            }

        then("예외가 발생한다.") {
            orders.forEach { order ->
                shouldThrow<IllegalStateException> {
                    orderService.completeDelivery(order.id)
                }
            }
        }
    }

    given("주문이 완료될 때") {
        `when`("정상 입력 값이 주어지면") {
            val newOrder =
                buildOrder {
                    orderType(DELIVERY)
                    orderStatus(DELIVERED)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("주문이 완료된다.") {
                with(orderService.complete(newOrder.id)) {
                    status shouldBe COMPLETED
                }
            }
        }

        `when`("주문이 존재하지 않으면") {
            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    orderService.complete(UUID.randomUUID())
                }
            }
        }

        `when`("배달 주문일 때 배달이 완료되지 않았으면") {
            val newOrder =
                buildOrder {
                    orderType(DELIVERY)
                    orderStatus(DELIVERING)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderService.complete(newOrder.id)
                }
            }
        }

        `when`("매장 주문일 때 메뉴가 전달되지 않았으면") {
            val newOrder =
                buildOrder {
                    orderType(EAT_IN)
                    orderStatus(ACCEPTED)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderService.complete(newOrder.id)
                }
            }
        }

        `when`("포장 주문일 때 메뉴가 전달되지 않았으면") {
            val newOrder =
                buildOrder {
                    orderType(TAKEOUT)
                    orderStatus(ACCEPTED)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderService.complete(newOrder.id)
                }
            }
        }

        `when`("매장 주문일 때 주문이 완료되면") {
            val newOrder =
                buildOrder {
                    orderType(EAT_IN)
                    orderStatus(SERVED)
                    table(occupied = false, numberOfGuest = 5)
                    requestMenu {
                        item("메뉴", 1000.won, 1.pcs)
                    }
                }.persistAll()

            then("테이블 상태가 변경된다.") {
                with(orderService.complete(newOrder.id)) {
                    status shouldBe COMPLETED
                    orderTable.isOccupied shouldBe false
                    orderTable.numberOfGuests shouldBe 0
                }
            }
        }
    }
})
