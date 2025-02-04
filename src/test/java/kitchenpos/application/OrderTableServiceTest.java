package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("OrderTableService 클래스의")
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;
    @MockBean
    private OrderRepository orderRepository;

    private OrderTable orderTableRequest;

    @BeforeEach
    void setUp() {
        orderTableRequest = TestFixture.createOrderTable("orderTable", 0, false);
    }

    @DisplayName("create 메소드는")
    @Nested
    class Create {

        @DisplayName("테이블의 이름은 필수요청 값 이다.")
        @Test
        void createWithEmptyName() {
            // given
            orderTableRequest.setName("");

            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderTableService.create(orderTableRequest));
        }

        @DisplayName("테이블을 등록 할 수 있다.")
        @Test
        void create() {
            // when
            final OrderTable orderTable = orderTableService.create(orderTableRequest);

            // then
            assertNotNull(orderTable.getId());
            assertEquals(orderTableRequest.getName(), orderTable.getName());
            assertEquals(0, orderTable.getNumberOfGuests());
            assertFalse(orderTable.isOccupied());
        }
    }

    @DisplayName("changeNumberOfGuests 메소드는")
    @Nested
    class ChangeNumberOfGuests {

        @DisplayName("테이블을 찾을 수 없으면 예외를 던진다.")
        @Test
        void changeNumberOfGuestsWithNonExistOrderTable() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTableRequest));
        }

        @DisplayName("테이블의 인원수는 0명보다는 큰 값이어야 한다.")
        @Test
        void changeNumberOfGuestsWithNegativeNumberOfGuests() {
            // given
            OrderTable savedOrderTable = orderTableService.create(orderTableRequest);
            orderTableRequest.setNumberOfGuests(-1);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTableRequest));
        }

        @DisplayName("테이블이 미착석인 경우에는 인원수를 변경 할 수 없다.")
        @Test
        void changeNumberOfGuestsWithEmptyOrderTable() {
            // given
            OrderTable savedOrderTable = orderTableService.create(orderTableRequest);

            // when & then
            assertThrows(IllegalStateException.class, () -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTableRequest));
        }

        @DisplayName("테이블의 인원수를 변경 할 수 있다")
        @Test
        void changeNumberOfGuests() {
            // given
            OrderTable savedOrderTable = orderTableService.create(orderTableRequest);
            orderTableService.sit(savedOrderTable.getId());
            orderTableRequest.setNumberOfGuests(4);

            // when
            final OrderTable orderTable = orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTableRequest);

            // then
            assertEquals(orderTableRequest.getNumberOfGuests(), orderTable.getNumberOfGuests());
        }
    }

    @DisplayName("sit 메소드는")
    @Nested
    class Sit {

        @DisplayName("테이블을 찾을 수 없으면 예외를 던진다.")
        @Test
        void sitWithNonExistOrderTable() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderTableService.sit(UUID.randomUUID()));
        }

        @DisplayName("테이블을 착석 할 수 있다.")
        @Test
        void sit() {
            // given
            OrderTable savedOrderTable = orderTableService.create(orderTableRequest);

            // when
            final OrderTable orderTable = orderTableService.sit(savedOrderTable.getId());

            // then
            assertTrue(orderTable.isOccupied());
        }
    }

    @DisplayName("clear 메소드는")
    @Nested
    class Clear {

        @DisplayName("테이블을 찾을 수 없으면 예외를 던진다.")
        @Test
        void clearWithNonExistOrderTable() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderTableService.clear(UUID.randomUUID()));
        }

        @DisplayName("해당 테이블에 완료되지 않은 주문이 남아 있을 경우 테이블을 비울 수 없다.")
        @Test
        void clearWithExistOrder() {
            // given
            OrderTable savedOrderTable = orderTableService.create(orderTableRequest);
            orderTableService.sit(savedOrderTable.getId());
            when(orderRepository.existsByOrderTableAndStatusNot(savedOrderTable, OrderStatus.COMPLETED)).thenReturn(true);

            // when & then
            assertThrows(IllegalStateException.class, () -> orderTableService.clear(savedOrderTable.getId()));
        }

        @DisplayName("인원수를 0명으로 변경하고, 착석여부를 미착석으로 변경한다.")
        @Test
        void clear() {
            // given
            OrderTable savedOrderTable = orderTableService.create(orderTableRequest);
            orderTableService.sit(savedOrderTable.getId());
            when(orderRepository.existsByOrderTableAndStatusNot(savedOrderTable, OrderStatus.COMPLETED)).thenReturn(false);

            // when
            final OrderTable orderTable = orderTableService.clear(savedOrderTable.getId());

            // then
            assertEquals(0, orderTable.getNumberOfGuests());
            assertFalse(orderTable.isOccupied());
        }
    }

    @DisplayName("findAll 메소드는")
    @Nested
    class FindAll {

        @DisplayName("테이블이 존재하지 않으면 빈 리스트를 반환한다.")
        @Test
        void findAllWithEmptyOrderTable() {
            // when
            final List<OrderTable> orderTables = orderTableService.findAll();

            // then
            assertThat(orderTables).isEmpty();
        }

        @DisplayName("테이블을 조회한다.")
        @Test
        void findAll() {
            // given
            orderTableService.create(orderTableRequest);

            // when
            final List<OrderTable> orderTables = orderTableService.findAll();

            // then
            assertThat(orderTables).isNotEmpty();
        }
    }
}
