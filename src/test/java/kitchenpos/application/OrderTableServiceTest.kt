package kitchenpos.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTableRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import random.randomOrderTable
import random.randomOrderTableId
import java.util.*

class OrderTableServiceTest {
    private val orderTableRepository: OrderTableRepository = mockk()
    private val orderRepository: OrderRepository = mockk()
    private lateinit var orderTableService: OrderTableService

    @BeforeEach
    fun setUp() {
        orderTableService = OrderTableService(orderTableRepository, orderRepository)
    }

    @Nested
    inner class `주문 테이블 생성`() {

        @ParameterizedTest
        @NullAndEmptySource
        fun `주문 테이블명은 필수값이다 아닌 경우 예외발생`(input: String?) {
            // given
            val orderTable = randomOrderTable(name = input)

            // when then
            assertThrows<IllegalArgumentException> {
                orderTableService.create(orderTable)
            }
        }

        @Test
        fun `손님 수는 0으로 초기 설정, 자리 상태는 비움으로 초기 설정한다`() {
            // given
            val orderTable = randomOrderTable()
            every { orderTableRepository.save(any()) } returns randomOrderTable(numberOfGuests = 0, occupied = false)
            // when then
            orderTableService.create(orderTable).run {
                this.numberOfGuests shouldBe 0
                this.isOccupied shouldBe false
            }
        }
    }

    @Nested
    inner class `주문 테이블 착석` {

        @Test
        fun `주문 테이블이 존재해야 한다 아닌 경우 예외 발생`() {
            // given
            val orderTableId = randomOrderTableId()
            every { orderTableRepository.findById(orderTableId) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                orderTableService.sit(orderTableId)
            }
        }

        @Test
        fun `자리 상태를 사용중으로 변경해야 한다`() {
            // given
            val orderTableId = randomOrderTableId()
            every { orderTableRepository.findById(orderTableId) } returns Optional.of(randomOrderTable(occupied = false))

            // when then
            orderTableService.sit(orderTableId).run {
                this.isOccupied shouldBe true
            }
        }
    }

    @Nested
    inner class `주문 테이블 비우기` {

        @Test
        fun `주문 테이블이 존재해야 한다 아닌 경우 예외 발생`() {
            // given
            val orderTableId = randomOrderTableId()
            every { orderTableRepository.findById(orderTableId) } returns Optional.empty()
            // when then
            assertThrows<NoSuchElementException> {
                orderTableService.clear(orderTableId)
            }
        }

        @Test
        fun `주문상태가 완료상태여야한다 아닌 경우 예외 발생`() {
            // given
            val orderTable = randomOrderTable()
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)
            every { orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED) } returns true

            // when then
            assertThrows<IllegalStateException> {
                orderTableService.clear(orderTable.id)
            }
        }

        @Test
        fun `손님 수를 0으로 설정, 자리 상태를 비움으로 변경`() {
            // given
            val orderTable = randomOrderTable(numberOfGuests = 5, occupied = true)
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)
            every { orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED) } returns false

            // when then
            orderTableService.clear(orderTable.id).run {
                this.numberOfGuests shouldBe 0
                this.isOccupied shouldBe false
            }
        }
    }

    @Nested
    inner class `주문 테이블 손님 수 변경` {

        @Test
        fun `손님 수는 0보다 커야 한다 아닌 경우 예외 발생`() {
            // given
            val orderTable = randomOrderTable(numberOfGuests = -1)
            // when then
            assertThrows<IllegalArgumentException> {
                orderTableService.changeNumberOfGuests(randomOrderTableId(), orderTable)
            }
        }

        @Test
        fun `주문 테이블이 존재해야 한다 아닌 경우 예외 발생`() {
            // given
            val orderTable = randomOrderTable()
            every { orderTableRepository.findById(orderTable.id) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                orderTableService.changeNumberOfGuests(orderTable.id, orderTable)
            }
        }

        @Test
        fun `자리 상태가 사용중 상태여야 한다 아닌 경우 예외 발생`() {
            // given
            val orderTable = randomOrderTable(occupied = false)
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            // when then
            assertThrows<IllegalStateException> {
                orderTableService.changeNumberOfGuests(orderTable.id, orderTable)
            }
        }

        @Test
        fun `변경한 손님 수 정상 반환`() {
            // given
            val orderTable = randomOrderTable(occupied = true)
            every { orderTableRepository.findById(orderTable.id) } returns Optional.of(orderTable)

            // when then
            orderTableService.changeNumberOfGuests(orderTable.id, orderTable).run {
                this.numberOfGuests shouldBe orderTable.numberOfGuests
            }
        }
    }
}
