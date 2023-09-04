package kitchenpos.application;

import kitchenpos.UnitTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderTableServiceTest extends UnitTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("[정상] 주문 테이블을 등록합니다.")
    @Test
    void create_success() {
        OrderTable givenOrderTable = OrderTableFixture.create();
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(givenOrderTable);

        OrderTable actualResult = orderTableService.create(givenOrderTable);

        assertEquals(givenOrderTable.getId(), actualResult.getId());
        assertEquals(givenOrderTable.getName(), actualResult.getName());
        assertEquals(givenOrderTable.getNumberOfGuests(), 0);
        assertFalse(givenOrderTable.isOccupied());
    }

    static Object[][] create_fail_because_illegal_name() {
        return new Object[][]{
            {OrderTableFixture.create(UUID.randomUUID(), "")},
            {OrderTableFixture.create(UUID.randomUUID(), null)},
        };
    }

    @DisplayName("[예외] 비정상적인 이름으로 주문 테이블 등록에 실패합니다.")
    @MethodSource
    @ParameterizedTest
    void create_fail_because_illegal_name(OrderTable givenOrderTable) {
        assertThatThrownBy(() -> orderTableService.create(givenOrderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[정상] 주문 테이블을 착석 처리 합니다.")
    @Test
    void sit() {
        OrderTable givenOrderTable = OrderTableFixture.create();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));

        OrderTable actualResult = orderTableService.sit(givenOrderTable.getId());

        assertTrue(actualResult.isOccupied());
    }

    @DisplayName("[정상] 주문 테이블을 정리 처리 합니다.")
    @Test
    void clear() {
        OrderTable givenOrderTable = OrderTableFixture.create();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(givenOrderTable, OrderStatus.COMPLETED)).thenReturn(false);

        OrderTable actualResult = orderTableService.clear(givenOrderTable.getId());

        assertEquals(0, actualResult.getNumberOfGuests());
        assertFalse(actualResult.isOccupied());
    }

    @DisplayName("[정상] 주문 테이블에 고객 수를 변경합니다.")
    @Test
    void changeNumberOfGuests_success() {
        OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블", 0, true);
        OrderTable changingNumberOfGuest = OrderTableFixture.create(
            givenOrderTable.getId(), givenOrderTable.getName(), 3, givenOrderTable.isOccupied()
        );
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));

        OrderTable actualResult = orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changingNumberOfGuest);

        assertEquals(changingNumberOfGuest.getNumberOfGuests(), actualResult.getNumberOfGuests());
        assertTrue(actualResult.isOccupied());
    }

    @DisplayName("[예외] 주문 테이블에 고객 수를 0명 미만으로 변경합니다.")
    @Test
    void changeNumberOfGuests_fail_because_change_zero_guest() {
        OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블", 0, true);
        OrderTable changingNumberOfGuest = OrderTableFixture.create(
            givenOrderTable.getId(), givenOrderTable.getName(), -1, givenOrderTable.isOccupied()
        );

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changingNumberOfGuest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 점유되지 않은 주문 테이블에 대해 고객 수 변경을 시도합니다.")
    @Test
    void changeNumberOfGuests_fail_because_order_table_is_not_occupied() {
        OrderTable givenOrderTable = OrderTableFixture.create(UUID.randomUUID(), "테이블", 0, false);
        OrderTable changingNumberOfGuest = OrderTableFixture.create(
            givenOrderTable.getId(), givenOrderTable.getName(), 3, givenOrderTable.isOccupied()
        );
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(givenOrderTable));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changingNumberOfGuest))
            .isInstanceOf(IllegalStateException.class);
    }

}