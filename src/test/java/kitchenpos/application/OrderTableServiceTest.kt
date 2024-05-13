package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import java.util.*

@ExtendWith(MockKExtension::class)
internal class OrderTableServiceTest {
    @MockK
    private lateinit var orderTableRepository: OrderTableRepository

    @MockK
    private lateinit var orderRepository: OrderRepository

    @InjectMockKs
    private lateinit var orderTableService: OrderTableService

    @Nested
    inner class `테이블 생성 테스트` {
        @DisplayName("이름이 null 또는 비어있다면, IllegalArgumentException 예외 처리를 한다.")
        @ParameterizedTest
        @NullAndEmptySource
        fun test1(name: String?) {
            // given
            val nullOrEmpty_테이블 = getOrderTable(name = name)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderTableService.create(nullOrEmpty_테이블)
            }
        }

        @DisplayName("정상 요청이라면, id 할당, 요청한 이름으로 테이블 설정, 손님 수 0명, 점유하지 않은 상태로 초기화되어 저장된다.")
        @Test
        fun test2() {
            // given
            val 정상_요청 = getOrderTable(name = "정상 테이블")

            every { orderTableRepository.save(any()) } returns 정상_요청

            // when
            val result = orderTableService.create(정상_요청)

            // then
            result.id shouldNotBe null
            result.name shouldBe 정상_요청.name
            result.numberOfGuests shouldBe 0
            result.isOccupied shouldBe false
        }
    }

    @Nested
    inner class `테이블 앉기 테스트` {
        @DisplayName("존재하지 않는 id 라면, NoSuchElementException 예외 처리한다.")
        @Test
        fun test1() {
            // given
            val orderTableId = UUID.randomUUID()

            every { orderTableRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderTableService.sit(orderTableId)
            }
        }

        @DisplayName("정상적인 요청이라면, 테이블 점유 상태가 된다.")
        @Test
        fun test2() {
            // given
            val orderTableId = UUID.randomUUID()
            val 정상_앉기_요청 = getOrderTable(
                id = orderTableId,
                isOccupied = false,
            )

            every { orderTableRepository.findById(any()) } returns Optional.of(정상_앉기_요청)

            // when
            val result = orderTableService.sit(orderTableId)

            // then
            result.isOccupied shouldBe true
        }
    }

    @Nested
    inner class `테이블 치우기 테스트` {
        @DisplayName("존재하지 않는 테이블 아이디이면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderTableId = UUID.randomUUID()

            every { orderTableRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderTableService.clear(orderTableId)
            }
        }

        @DisplayName("주문 상태가 완료인 주문을 제외하고, 해당 테이블이 속해있는 주문이 있다면, IllegalStateException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val orderTableId = UUID.randomUUID()
            val 중복된_테이블 = getOrderTable(
                id = orderTableId,
            )

            every { orderTableRepository.findById(any()) } returns Optional.of(중복된_테이블)
            every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns true

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderTableService.clear(orderTableId)
            }
        }

        @DisplayName("정상 요청이라면, 해당 테이블을 치운다.")
        @Test
        fun test3() {
            // given
            val orderTableId = UUID.randomUUID()
            val 정상_테이블_요청 = getOrderTable(
                id = orderTableId
            )

            every { orderTableRepository.findById(any()) } returns Optional.of(정상_테이블_요청)
            every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns false

            // when
            val result = orderTableService.clear(orderTableId)

            // then
            result.numberOfGuests shouldBe 0
            result.isOccupied shouldBe false
        }
    }

    @Nested
    inner class `손님 수 변경 테스트` {
        @DisplayName("손님 수를 음수로 변경하려고 하면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test1() {
            // given
            val orderTableId = UUID.randomUUID()
            val 손님_수_음수_요청 = getOrderTable(
                id = orderTableId,
                numberOfGuest = -1,
            )

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderTableService.changeNumberOfGuests(orderTableId, 손님_수_음수_요청)
            }
        }

        @DisplayName("존재하지 않는 id 라면, NoSuchElementException 예외 처리를 한다.")
        @Test
        fun test2() {
            // given
            val orderTableId = UUID.randomUUID()
            val 존재_하지_않는_테이블 = getOrderTable(
                id = UUID.randomUUID(),
                numberOfGuest = 4,
            )

            every { orderTableRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                orderTableService.changeNumberOfGuests(orderTableId, 존재_하지_않는_테이블)
            }
        }

        @DisplayName("해당 테이블이 점유 중이지 않다면, IllegalStateException 예외 처리를 한다.")
        @Test
        fun test3() {
            // given
            val orderTableId = UUID.randomUUID()
            val 점유_중_이지_않은_테이블 = getOrderTable(
                id = orderTableId,
                numberOfGuest = 4,
                isOccupied = false,
            )

            every { orderTableRepository.findById(any()) } returns Optional.of(점유_중_이지_않은_테이블)
            every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns false

            // when & then
            shouldThrowExactly<IllegalStateException> {
                orderTableService.changeNumberOfGuests(orderTableId, 점유_중_이지_않은_테이블)
            }
        }

        @DisplayName("정상 요청이라면, 손님 수를 변경한다.")
        @Test
        fun test4() {
            // given
            val orderTableId = UUID.randomUUID()
            val 정상_테이블 = getOrderTable(
                id = orderTableId,
                numberOfGuest = 4,
                isOccupied = true,
            )

            every { orderTableRepository.findById(any()) } returns Optional.of(정상_테이블)
            every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns false

            // when
            val result = orderTableService.changeNumberOfGuests(orderTableId, 정상_테이블)

            // then
            result.numberOfGuests shouldBe 정상_테이블.numberOfGuests
        }
    }

    private fun getOrderTable(
        id: UUID = UUID.randomUUID(),
        name: String? = "정상 테이블",
        numberOfGuest: Int = 0,
        isOccupied: Boolean = false,
    ) = OrderTable().apply {
        this.id = id
        this.name = name
        this.numberOfGuests = numberOfGuest
        this.isOccupied = isOccupied
    }
}