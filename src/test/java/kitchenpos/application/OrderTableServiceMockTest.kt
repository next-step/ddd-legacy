package kitchenpos.application

import kitchenpos.domain.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@DisplayName("주문 테이블 서비스")
@ExtendWith(MockitoExtension::class)
class OrderTableServiceMockTest {

    @Mock
    private lateinit var orderTableRepository: OrderTableRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @InjectMocks
    private lateinit var orderTableService: OrderTableService

    @Test
    fun `주문 테이블 생성 - 정상적인 주문 테이블 생성 성공`() {
        val request = OrderTable()
        request.name = "1"
        request.numberOfGuests = 10

        Assertions.assertThatCode { orderTableService.create(request) }
            .doesNotThrowAnyException()
    }

    @DisplayName("주문 테이블 손님 수 변경")
    @Nested
    inner class `주문 테이블의 손님 수 변경` {
        @Test
        fun `주문 테이블의 손님 수 변경 - 정상적인 테이블 손님 수 변경 성공`() {
            val request = OrderTable()
            request.numberOfGuests = 3

            val orderTable = OrderTable()
            orderTable.id = UUID.randomUUID()
            orderTable.isOccupied = true
            orderTable.numberOfGuests = 1

            `when`(orderTableRepository.findById(orderTable.id))
                .thenReturn(Optional.of(orderTable))

            //when & then
            val result = orderTableService.changeNumberOfGuests(orderTable.id, request)

            Assertions.assertThat(result.numberOfGuests).isEqualTo(request.numberOfGuests)
        }

        @Test
        fun `주문 테이블의 손님 수 변경 - 테이블이 사용중이지 않을 경우 실패`() {
            val request = OrderTable()
            request.numberOfGuests = 3

            val orderTable = OrderTable()
            orderTable.id = UUID.randomUUID()
            orderTable.isOccupied = false
            orderTable.numberOfGuests = 1

            `when`(orderTableRepository.findById(orderTable.id))
                .thenReturn(Optional.of(orderTable))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderTableService.changeNumberOfGuests(orderTable.id, request) }
        }
    }

    @DisplayName("주문 테이블을 미사용 상태로 변경")
    @Nested
    inner class `주문 테이블을 미사용 상태로 변경` {
        @Test
        fun `주문 테이블을 미사용 상태로 변경 - 정상적인 테이블 미사용 상태 변경 성공`() {
            val orderTable = OrderTable()
            orderTable.id = UUID.randomUUID()
            orderTable.isOccupied = true
            orderTable.numberOfGuests = 3

            `when`(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .thenReturn(false)

            `when`(orderTableRepository.findById(orderTable.id))
                .thenReturn(Optional.of(orderTable))

            //when
            val result = orderTableService.clear(orderTable.id)

            //then
            Assertions.assertThat(result.isOccupied).isFalse()
            Assertions.assertThat(result.numberOfGuests).isZero()
        }

        @Test
        fun `주문 테이블을 미사용 상태로 변경 - 테이블에 미완료 주문이 존재할 경우 실패`() {
            val orderTable = OrderTable()
            orderTable.id = UUID.randomUUID()

            `when`(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .thenReturn(true)

            `when`(orderTableRepository.findById(orderTable.id))
                .thenReturn(Optional.of(orderTable))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { orderTableService.clear(orderTable.id) }
        }
    }
}
