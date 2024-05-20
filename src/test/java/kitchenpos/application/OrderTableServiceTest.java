package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.OrderTableFixture.createTable;
import static kitchenpos.fixture.OrderTableFixture.두명;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("테이블정보 등록 테스트")
    class CreateTable {
        @Test
        @DisplayName("테이블정보를 정상으로 등록할 수 있다.")
        void success() {
            final var orderTable = createTable();
            final var response = createTable();

            given(orderTableRepository.save(any())).willReturn(response);

            OrderTable actual = orderTableService.create(orderTable);

            assertAll(
                    () -> assertNotNull(actual),
                    () -> assertEquals(response.getName(), actual.getName())
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("[실패] 테이블 이름은 필수로 입력해야한다.")
        void fail1(final String input) {
            final var orderTable = OrderTableFixture.createEmptyTable(input);

            assertThrows(IllegalArgumentException.class, () -> orderTableService.create(orderTable));
        }

        @Test
        @DisplayName("최초 등록시 테이블의 손님 수와 착석여부 상태는 모두 빈 좌석으로 저장된다. (손님수 0명, 미착석 상태)")
        void fail2() {
            OrderTable actual = createTable();

            assertAll(
                    "테이블 초기화 상태 assertEquals",
                    () -> assertEquals(0, actual.getNumberOfGuests()),
                    () -> assertFalse(actual.isOccupied())
            );
        }
    }

    @Nested
    @DisplayName("테이블 착석여부 변경")
    class changeOccupied {
        @Test
        @DisplayName("테이블의 착석여부를 미착석에서 착성중으로 변경할 수 있다.")
        void success1() {
            final var orderTable = createTable();
            final var tableId = orderTable.getId();

            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            assertFalse(orderTable.isOccupied());
            OrderTable sitting = orderTableService.sit(tableId);
            assertTrue(sitting.isOccupied());
        }

        @Test
        @DisplayName("테이블의 착석여부를 착성중에서 미착석 상태로 변경할 수 있다.")
        void success2() {
            final var orderTable = createTable("테이블", true, 1);
            final var tableId = orderTable.getId();

            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            assertTrue(orderTable.isOccupied());
            OrderTable cleared = orderTableService.clear(tableId);
            assertFalse(cleared.isOccupied());
            assertEquals(0, orderTable.getNumberOfGuests());
        }

        @Test
        @DisplayName("등록된 테이블정보가 아닌 경우 처리가 불가능하다.")
        void fail1() {
            final var orderTable = createTable();
            final var tableId = orderTable.getId();

            assertAll(
                    "테이블 착석여부 케이스 모두 확인",
                    () -> assertThrows(NoSuchElementException.class, () -> orderTableService.sit(tableId)),
                    () -> assertThrows(NoSuchElementException.class, () -> orderTableService.clear(tableId))
            );
        }

        @Test
        @DisplayName("테이블에 주문된적이 있고 주문상태가 COMPLETED 가 아닌 경우 처리가 불가능하다.")
        void fail2() {
            final var orderTable = createTable();

            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .willReturn(true);

            assertThrows(IllegalStateException.class, () -> orderTableService.clear(orderTable.getId()));
        }

    }

    @Nested
    @DisplayName("손님수 변경")
    class changeGuests {
        @ParameterizedTest
        @ValueSource(ints = {두명, 0})
        @DisplayName("손님수를 변경할 수 있다.")
        void success(final int input) {
            final var orderTable = createTable();
            final var tableId = orderTable.getId();

            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(orderTable));
            orderTable.setNumberOfGuests(input);
            orderTableService.sit(tableId);

            orderTableService.changeNumberOfGuests(tableId, orderTable);

            assertEquals(input, orderTable.getNumberOfGuests());
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -100})
        @DisplayName("변경하려는 손님 수가 0명보다 적은 경우 변경할 수 없다.")
        void fail1(final int input) {
            final var orderTable = createTable();
            orderTable.setNumberOfGuests(input);

            assertThrows(IllegalArgumentException.class,
                    () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @Test
        @DisplayName("등록된 테이블 정보가 아닌 경우 손님 수를 변경할 수 없다.")
        void fail2() {
            final var orderTable = createTable();

            orderTable.setNumberOfGuests(두명);

            assertThrows(NoSuchElementException.class,
                    () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @Test
        @DisplayName("테이블이 손님 착성중 상태가 아닌 경우 손님 수를 변경할 수 없다.")
        void fail3() {
            final var orderTable = createTable();
            final var tableId = orderTable.getId();

            orderTable.setNumberOfGuests(두명);

            given(orderTableRepository.findById(tableId)).willReturn(Optional.of(orderTable));

            assertAll(
                    () -> assertFalse(orderTable.isOccupied()),
                    () -> assertThrows(IllegalStateException.class,
                            () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
            );
        }
    }

}
