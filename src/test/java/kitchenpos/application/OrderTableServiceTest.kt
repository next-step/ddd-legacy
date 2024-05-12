package kitchenpos.application

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import java.util.UUID

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
}