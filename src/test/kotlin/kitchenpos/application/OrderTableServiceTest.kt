package kitchenpos.application

import domain.OrderTableFixtures.makeOrderTableOne
import domain.OrderTableFixtures.makeOrderTableTwo
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import org.junit.jupiter.api.assertThrows
import java.util.*

class OrderTableServiceTest : DescribeSpec() {
    init {
        describe("OrderTableService 클래스의") {
            val orderTableRepository = mockk<OrderTableRepository>()
            val orderRepository = mockk<OrderRepository>()
            val orderTableService = OrderTableService(orderTableRepository, orderRepository)

            describe("create 메서드는") {
                context("정상적인 주문 테이블 요청이 주어졌을 때") {
                    it("주문 테이블을 생성한다") {
                        val orderTable = makeOrderTableTwo()

                        every { orderTableRepository.save(any()) } returns orderTable

                        val result = orderTableService.create(orderTable)

                        result.name shouldBe orderTable.name
                        result.isOccupied shouldBe false
                        result.numberOfGuests shouldBe orderTable.numberOfGuests
                    }
                }
            }

            describe("sit 메서드는") {
                context("주문 테이블 아이디가 주어졌을때") {
                    it("주문 테이블을 점유한다") {
                        val orderTable = makeOrderTableTwo()
                        every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

                        val result = orderTableService.sit(orderTable.id)

                        result.isOccupied shouldBe true
                    }
                }
            }

            describe("clear 메서드는") {
                context("주문 테이블 아이디가 주어졌을때") {
                    it("주문 테이블을 비운다") {
                        val orderTable = makeOrderTableOne()

                        every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

                        every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns false

                        val result = orderTableService.clear(orderTable.id)

                        result.isOccupied shouldBe false
                        result.numberOfGuests shouldBe 0
                    }
                }

                context("완료 상태가 아닌 주문이 남아있을때") {
                    it("IllegalStateException을 던진다") {
                        val orderTable = makeOrderTableTwo()

                        every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

                        every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns true

                        assertThrows<IllegalStateException> {
                            orderTableService.clear(orderTable.id)
                        }
                    }
                }
            }

            describe("changeNumberOfGuests 메서드는") {
                context("주문 테이블 아이디와 인원수가 주어졌을때") {
                    it("주문 테이블의 인원수를 변경한다") {
                        val orderTable = makeOrderTableOne()
                        every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

                        val result =
                            orderTableService.changeNumberOfGuests(
                                UUID.fromString("6924640b-b0fc-4c86-84f9-b750eeba0205"),
                                makeOrderTableOne().apply {
                                    this.numberOfGuests = 5
                                },
                            )

                        result.numberOfGuests shouldBe 5
                    }

                    it("주문 테이블이 점유중 상태가 아니면 예외를 던진다") {
                        val orderTable = makeOrderTableTwo()
                        val numberOfGuests = 4

                        every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

                        assertThrows<IllegalStateException> {
                            orderTableService.changeNumberOfGuests(
                                orderTable.id,
                                OrderTable().apply { this.numberOfGuests = numberOfGuests },
                            )
                        }
                    }
                }
            }
        }
    }
}
