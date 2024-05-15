package kitchenpos.application.order;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Application: 주문 테이블 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;


    @Nested
    @DisplayName("주문 테이블을 생성할 수 있다.")
    class create {

        @Test
        @DisplayName("주문 테이블을 생성할 수 있다.")
        void case_1() {
            OrderTable table = OrderTestFixture.aOrderTable("1번_테이블");

            when(orderTableRepository.save(any(OrderTable.class))).thenReturn(table);
            OrderTable createdTable = orderTableService.create(table);

            assertEquals(table.getName(), createdTable.getName());
            assertEquals(table.getNumberOfGuests(), createdTable.getNumberOfGuests());
            assertEquals(table.isOccupied(), createdTable.isOccupied());
        }

        @ParameterizedTest(name = "이름이 {0}인 경우")
        @ValueSource(strings = {""})
        @NullSource
        @DisplayName("이름이 null이거나 공백인 주문 테이블을 생성할 수 없다.")
        void case_2(String name) {
            OrderTable table = OrderTestFixture.aOrderTable(name);

            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> orderTableService.create(table));
        }
    }

    @Nested
    @DisplayName("주문 테이블에 손님을 입장시킬 수 있다.")
    class sit {
        @Test
        @DisplayName("주문 테이블에 손님을 입장시킬 수 있다.")
        void case_1() {
            OrderTable table = OrderTestFixture.aOrderTable("1번_테이블");
            table.setOccupied(false);

            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(table));
            OrderTable sitTable = orderTableService.sit(table.getId());

            assertTrue(sitTable.isOccupied());
        }

        @Test
        @DisplayName("주문 테이블이 존재 하지 않는 경우 예외가 발생한다.")
        void case_2() {
            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            Assertions.assertThrows(NoSuchElementException.class,
                    () -> orderTableService.sit(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("주문 테이블의 손님을 퇴장시킬 수 있다.")
    class clear {
        @Test
        @DisplayName("주문 테이블의 손님을 퇴장시킬 수 있다.")
        void case_1() {
            OrderTable table = OrderTestFixture.aOrderTable("1번_테이블");
            table.setOccupied(true);

            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(table));
            when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any())).thenReturn(false);
            OrderTable clearTable = orderTableService.clear(table.getId());

            assertEquals(0, clearTable.getNumberOfGuests());
            assertFalse(clearTable.isOccupied());
        }

        @Test
        @DisplayName("주문 테이블이 존재 하지 않는 경우 예외가 발생한다.")
        void case_2() {
            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            Assertions.assertThrows(NoSuchElementException.class,
                    () -> orderTableService.clear(UUID.randomUUID()));
        }

        @Test
        @DisplayName("테이블의 주문이 완료되지 않고 주문 테이블의 손님을 퇴장시킬 수 없다.")
        void case_3() {
            OrderTable table = OrderTestFixture.aOrderTable("1번_테이블");
            table.setOccupied(true);

            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(table));
            when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any())).thenReturn(true);

            Assertions.assertThrows(IllegalStateException.class,
                    () -> orderTableService.clear(table.getId()));
        }

    }

    @Nested
    @DisplayName("주문 테이블의 손님 수를 변경할 수 있다.")
    class changeNumberOfGuests {
        @Test
        @DisplayName("주문 테이블의 손님 수를 변경할 수 있다.")
        void case_1() {
            //given
            OrderTable table = OrderTestFixture.aOrderTableWithGuests("1번_테이블", 1);
            ReflectionTestUtils.setField(table, "occupied", true);

            //when
            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(table));
            OrderTable changeTable = orderTableService.changeNumberOfGuests(table.getId(), table);

            //then
            assertEquals(table.getNumberOfGuests(), changeTable.getNumberOfGuests());

        }

        @Test
        @DisplayName("변경할 손님수가 0보다 작은 경우 예외가 발생한다.")
        void case_2() {
            OrderTable table = OrderTestFixture.aOrderTableWithGuests("1번_테이블", -1);

            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> orderTableService.changeNumberOfGuests(table.getId(), table));
        }

        @Test
        @DisplayName("주문 테이블이 없는 경우 예외가 발생한다.")
        void case_3() {
            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            Assertions.assertThrows(NoSuchElementException.class,
                    () -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), new OrderTable()));
        }

        @Test
        @DisplayName("손님이 없는데 손님수를 변경하려고 할때 예외가 발생한다.")
        void case_4() {
            OrderTable table = OrderTestFixture.aOrderTable("1번_테이블");
            table.setOccupied(false);

            when(orderTableRepository.findById(any(UUID.class))).thenReturn(Optional.of(table));

            Assertions.assertThrows(IllegalStateException.class,
                    () -> orderTableService.changeNumberOfGuests(table.getId(), table));
        }
    }
}
