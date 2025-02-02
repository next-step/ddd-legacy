package kitchenops.application

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.application.OrderTableService
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import spec.BaseUnitSpec
import java.util.*

internal class OrderTableServiceTest : BaseUnitSpec({

    val orderRepository = mockk<OrderRepository>()
    val orderTableRepository = mockk<OrderTableRepository>()

    val sut = OrderTableService(orderTableRepository, orderRepository)

    context("주문 테이블을 생성할 수 있다") {
        test("이름을 지정하지 않으면 생성할 수 없다") {
            // given
            val request = OrderTable().apply { name = null }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("이름이 비어있으면 생성할 수 없다") {
            // given
            val request = OrderTable().apply { name = "" }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("생성한다") {
            // given
            val request = OrderTable().apply { name = "1234" }

            every { orderTableRepository.save(match { it.name == "1234" && !it.isOccupied && it.numberOfGuests == 0 }) } returns OrderTable().apply {
                name = "1234"
                isOccupied = false
                numberOfGuests = 0
            }

            // when
            val actual = sut.create(request)

            // then
            actual should {
                it.name shouldBe "1234"
                it.isOccupied shouldBe false
                it.numberOfGuests shouldBe 0
            }

            verify(exactly = 1) { orderTableRepository.save(any()) }
        }
    }

    context("주문 테이블을 전체 조회할 수 있다") {
        test("전체 조회한다") {
            // given
            val orderTable1 = OrderTable()
            val orderTable2 = OrderTable()
            every { orderTableRepository.findAll() } returns listOf(orderTable1, orderTable2)

            // when
            val actual = sut.findAll()

            // then
            actual shouldBe listOf(orderTable1, orderTable2)
        }
    }

    context("손님은 주문 테이블에 착석할 수 있다") {
        test("존재하지 않는 주문 테이블에 착석할 수 없다") {
            // given
            val id = UUID.randomUUID()
            every { orderTableRepository.findById(id) } returns Optional.empty()

            // when
            val actual = runCatching { sut.sit(id) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("착석한다") {
            // given
            val orderTable = OrderTable().apply { isOccupied = false }
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            // when
            val actual = sut.sit(orderTable.id)

            // then
            actual.isOccupied shouldBe true
        }
    }

    context("주문 테이블을 정리할 수 있다") {
        test("존재하지 않는 주문 테이블을 정리할 수 없다") {
            // given
            val id = UUID.randomUUID()
            every { orderTableRepository.findById(id) } returns Optional.empty()

            // when
            val actual = runCatching { sut.clear(id) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        test("주문이 완료되지 않았을경우 테이블을 정리할 수 없다") {
            // given
            val orderTable = OrderTable()
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            every { orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED) } returns true

            // when
            val actual = runCatching { sut.clear(orderTable.id) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("정리한다") {
            // given
            val orderTable = OrderTable().apply {
                numberOfGuests = 3
                isOccupied = true
            }
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            every { orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED) } returns false

            // when
            val actual = sut.clear(orderTable.id)

            // then
            actual should {
                it.isOccupied shouldBe false
                it.numberOfGuests shouldBe 0
            }
        }
    }

    context("주문 테이블의 손님 수를 변경할 수 있다") {
        test("손님 수가 0명 미만일경우 변경할 수 없다") {
            // given
            val request = OrderTable().apply { numberOfGuests = -1 }

            // when
            val actual = kotlin.runCatching { sut.changeNumberOfGuests(request.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("존재하지 않는 주문 테이블을 변경할 수 없다") {
            // given
            val id = UUID.randomUUID()
            every { orderTableRepository.findById(id) } returns Optional.empty()

            val request = OrderTable().apply { numberOfGuests = 1 }

            // when
            val actual = kotlin.runCatching { sut.changeNumberOfGuests(id, request) }

            // then
            actual.exceptionOrNull() shouldBe NoSuchElementException()
        }

        // TODO: isOccupied 이외에도 numberOfGuests가 같이 있어서 numberOfGuests가 있는데 isOccupied는 false 인거와 같은 불일치가 있을 수 있음. numberOfGuests 하나로 통일해야 할 듯?
        test("주문 테이블에 손님이 없으면 변경할 수 없다") {
            // given
            val orderTable = OrderTable().apply { isOccupied = false }
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            val request = OrderTable().apply { numberOfGuests = 1 }

            // when
            val actual = kotlin.runCatching { sut.changeNumberOfGuests(orderTable.id, request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalStateException()
        }

        test("손님 수를 변경한다") {
            // given
            val orderTable = OrderTable().apply {
                isOccupied = true
                numberOfGuests = 3
            }
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            val request = OrderTable().apply { numberOfGuests = 1 }

            // when
            val actual = sut.changeNumberOfGuests(orderTable.id, request)

            // then
            actual.numberOfGuests shouldBe 1
        }
    }
})
