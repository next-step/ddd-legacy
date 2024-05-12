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
            val request = OrderTable().apply {
                this.name = name
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                orderTableService.create(request)
            }
        }

        @DisplayName("정상 요청이라면, id 할당, 요청한 이름으로 테이블 설정, 손님 수 0명, 점유하지 않은 상태로 초기화되어 저장된다.")
        @Test
        fun test2() {
            // given
            val request = OrderTable().apply {
                this.id = UUID.randomUUID()
                this.name = "1번 테이블"
            }

            every { orderTableRepository.save(any()) } returns request

            // when
            val result = orderTableService.create(request)

            // then
            result.id shouldNotBe null
            result.name shouldBe request.name
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
            val orderTable = OrderTable()

            every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)

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
            val orderTable = OrderTable()

            every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)
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
            val orderTable = OrderTable()

            every { orderTableRepository.findById(any()) } returns Optional.of(orderTable)
            every { orderRepository.existsByOrderTableAndStatusNot(any(), any()) } returns false

            // when
            val result = orderTableService.clear(orderTableId)

            // then
            result.numberOfGuests shouldBe 0
            result.isOccupied shouldBe false
        }
    }
}