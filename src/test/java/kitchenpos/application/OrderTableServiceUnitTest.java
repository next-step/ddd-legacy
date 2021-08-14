package kitchenpos.application;

import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceUnitTest {

    @Mock
    OrderTableRepository orderTableRepository;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderTableService orderTableService;

    private static final String NUMBER_OF_GUESTS_ILLEGAL_ARGUMENT = "인원 수는 0명 이상이어야 합니다.";
    private static final String EMPTY_TABLE_ILLEGAL_STATE = "테이블이 비어있습니다.";

    @DisplayName("주문 테이블 추가")
    @Test
    void create() {
        //given
        final OrderTable newOrderTable = new OrderTable();
        newOrderTable.setName("2인 테이블");

        when(orderTableRepository.save(any(OrderTable.class))).then(AdditionalAnswers.returnsFirstArg());

        //when
        OrderTable createdOrderTable = orderTableService.create(newOrderTable);

        //then
        assertAll(
                () -> assertThat(createdOrderTable).isNotNull(),
                () -> assertThat(createdOrderTable.getId()).isNotNull(),
                () -> assertThat(createdOrderTable.getName()).isEqualTo(newOrderTable.getName()),
                () -> assertThat(createdOrderTable.isEmpty()).isTrue(),
                () -> assertThat(createdOrderTable.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("주문 테이블 추가 실패 - 이름 미지정")
    @NullSource
    @EmptySource
    @ParameterizedTest
    void create_fail_null_or_empty_name(final String name) {
        //given
        OrderTable newOrderTable = OrderTableFixture.generateEmptyOrderTable(name);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderTableService.create(newOrderTable));
    }

    @DisplayName("주문 테이블에 손님이 앉는다.")
    @Test
    void sit() {
        //given
        final OrderTable orderTable = OrderTableFixture.generateEmptyOrderTable("2인 테이블");

        doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTable.getId());

        //when
        OrderTable occupiedOrderTable = orderTableService.sit(orderTable.getId());

        //then
        assertThat(occupiedOrderTable.isEmpty()).isFalse();
    }

    @DisplayName("손님이 앉을 수 없다. - 존재하지 않는 테이블")
    @Test
    void can_not_sit_non_existent_table() {
        //given
        final OrderTable orderTable = OrderTableFixture.generateEmptyOrderTable("2인 테이블");

        doReturn(Optional.empty()).when(orderTableRepository).findById(orderTable.getId());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderTableService.sit(orderTable.getId()));
    }

    @DisplayName("손님이 앉을 수 없다. - 비어있지 않은 테이블")
    @Test
    void can_not_sit_not_empty_table() {
        //given
        final OrderTable orderTable = OrderTableFixture.generateEmptyOrderTable("2인 테이블");

        doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTable.getId());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderTableService.sit(orderTable.getId()));
    }

    @DisplayName("테이블을 정리한다.")
    @Test
    void clear() {
        //given
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();

        doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTable.getId());
        doReturn(false).when(orderRepository).existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED);

        //when
        OrderTable clearedOrderTable = orderTableService.clear(orderTable.getId());

        //then
        assertAll(
                () -> assertThat(clearedOrderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(clearedOrderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("테이블을 정리할 수 없다. - 존재하지 않는 테이블")
    @Test
    void can_not_clear_non_existent_table() {
        //given
        doReturn(Optional.empty()).when(orderTableRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));
    }

    @DisplayName("테이블을 정리할 수 없다. - 주문 처리가 완료된 상태가 아니다.")
    @Test
    void can_not_clear_not_completed_orderstatus() {
        //given
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();

        doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTable.getId());
        doReturn(true).when(orderRepository).existsByOrderTableAndStatusNot(any(OrderTable.class), eq(OrderStatus.COMPLETED));

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }

    @DisplayName("테이블에 앉은 손님 수 변경")
    @Test
    void change_nunmber_of_guests() {
        //given
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();
        orderTable.setNumberOfGuests(10);

        doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTable.getId());

        //when
        OrderTable changedOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        //then
        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("테이블에 앉은 손님 수 변경 실패 - 손님 인원은 0명 이상이어야 한다.")
    @Test
    void change_fail_negative_nunmber_of_guests() {
        //given
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();
        orderTable.setNumberOfGuests(-3);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .withMessageContaining(NUMBER_OF_GUESTS_ILLEGAL_ARGUMENT);
    }

    @DisplayName("테이블에 앉은 손님 수 변경 실패 - 존재하지 않는 테이블")
    @Test
    void change_fail_non_existent_table() {
        //given
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();
        orderTable.setNumberOfGuests(10);

        doReturn(Optional.empty()).when(orderTableRepository).findById(orderTable.getId());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("테이블에 앉은 손님 수 변경 실패 - 빈 테이블")
    @Test
    void change_fail_empty_table() {
        //given
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();
        orderTable.setEmpty(true);

        doReturn(Optional.of(orderTable)).when(orderTableRepository).findById(orderTable.getId());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .withMessageContaining(EMPTY_TABLE_ILLEGAL_STATE);
    }

    @DisplayName("모든 주문 테이블 조회")
    @Test
    void findAll() {
        //given
        OrderTable orderTable01 = OrderTableFixture.generateEmptyOrderTable("1번 테이블");
        OrderTable orderTable02 = OrderTableFixture.generateEmptyOrderTable("2번 테이블");
        OrderTable orderTable03 = OrderTableFixture.generateEmptyOrderTable("3번 테이블");

        List<OrderTable> orderTables = Arrays.asList(orderTable01, orderTable02, orderTable03);

        doReturn(orderTables).when(orderTableRepository).findAll();

        //when
        List<OrderTable> actual = orderTableService.findAll();

        //then
        assertAll(
                () -> assertThat(actual).hasSize(orderTables.size()),
                () -> assertThat(actual.get(0).getId()).isEqualTo(orderTables.get(0).getId())
        );
    }
}
