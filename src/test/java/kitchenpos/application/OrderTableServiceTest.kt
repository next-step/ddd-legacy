package kitchenpos.application

import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderTable
import kitchenpos.fixture.initOrderTable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.jdbc.Sql

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@Sql("classpath:db/data.sql")
class OrderTableServiceTest {
    @MockBean
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var sut: OrderTableService

    @DisplayName("사장님은 가게의 테이블을 등록할 수 있다.")
    @Nested
    inner class Create {
        @DisplayName("사장님은 가게의 테이블에 이름을 부여하여 등록할 수 있다.")
        @Test
        fun case_1() {
            // given
            val request = initOrderTable()

            // when
            val createdTable = sut.create(request)

            // then
            assertThat(createdTable.id).isNotNull()
        }

        @DisplayName("최초 테이블의 상태는 다음과 같이 등록된다. (손님 0명 / 빈자리)")
        @Test
        fun case_2() {
            // given
            val request = initOrderTable()

            // when
            val createdTable = sut.create(request)

            // then
            with(createdTable) {
                assertThat(this.id).isNotNull()
                assertThat(this.numberOfGuests).isEqualTo(0)
                assertThat(this.isOccupied).isFalse()
            }
        }
    }

    @DisplayName("손님이 테이블에 앉으면 테이블의 상태는 다음과 같이 변경된다. (1번 테이블 / 손님 0명 / 사용중)")
    @Test
    fun case_3() {
        // given
        val tableId = createTable().id

        // when
        val occupiedTable = sut.sit(tableId)

        // then
        with(occupiedTable) {
            assertThat(this.id).isNotNull()
            assertThat(this.numberOfGuests).isEqualTo(0)
            assertThat(this.isOccupied).isTrue()
        }
    }

    @DisplayName("테이블에 앉은 손님의 숫자를 변경할 수 있다.")
    @Nested
    inner class Sit {
        @DisplayName("테이블을 사용하는 손님이 없을때에는 사용하는 손님의 숫자를 변경할 수 없다.")
        @Test
        fun case_4() {
            // given
            val tableId = createTable().id
            val request = initOrderTable(id = tableId, numberOfGuests = 3)

            // when
            // then
            assertThrows<IllegalStateException> { sut.changeNumberOfGuests(tableId, request) }
        }

        @DisplayName("테이블을 사용하는 손님의 숫자를 변경시에는 0보다 큰 숫자만 가능하다.")
        @Test
        fun case_5() {
            // given
            val tableId = createTable().id
            val request = initOrderTable(id = tableId, numberOfGuests = -1)

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.changeNumberOfGuests(tableId, request) }
        }

        @DisplayName("테이블을 사용하는 손님이 있을때에만 사용하는 손님의 숫자를 변경 가능하다.")
        @Test
        fun case_6() {
            // given
            val tableId = createTable().id
            val request = initOrderTable(id = tableId, numberOfGuests = 3)

            // when
            sut.sit(tableId)
            val occupiedTable = sut.changeNumberOfGuests(tableId, request)

            // then
            with(occupiedTable) {
                assertThat(this.id).isNotNull()
                assertThat(this.numberOfGuests).isEqualTo(3)
                assertThat(this.isOccupied).isTrue()
            }
        }
    }

    @DisplayName("손님이 테이블을 사용 완료하여 테이블을 정리하면 다시 상태를 변경할 수 있다.")
    @Nested
    inner class Clear {
        @DisplayName("주문 상태가 `완료` 인 경우에만, 해당 주문 테이블을 정리할 수 있다.")
        @Test
        fun case_7() {
            // given
            val sitTable = sitTable(createTable())
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false)

            // when
            val clearedTable = sut.clear(sitTable.id)

            // then
            with(clearedTable) {
                assertThat(this.id).isEqualTo(sitTable.id)
                assertThat(this.isOccupied).isEqualTo(false)
                assertThat(this.numberOfGuests).isEqualTo(0)
            }
        }
    }

    @DisplayName("등록된 테이블의 목록을 조회할 수 있다.")
    @Test
    fun case_8() {
        // given
        // when
        val tables = sut.findAll()

        // then
        assertThat(tables.size).isEqualTo(8)
    }

    private fun createTable(): OrderTable {
        val request = initOrderTable()
        return sut.create(request)
    }

    private fun sitTable(orderTable: OrderTable): OrderTable {
        return sut.sit(orderTable.id)
    }
}
