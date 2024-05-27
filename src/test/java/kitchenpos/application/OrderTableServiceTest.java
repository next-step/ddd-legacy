package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("OrderTableService.create 메서드 테스트")
    @Nested
    class create {
        @DisplayName("주문 테이블을 등록할 수 있다.")
        @Test
        void create() {
            // given
            OrderTable request = createRequest("1호");
            OrderTable orderTable = createOrderTable("1호", 0, false);
            given(orderTableRepository.save(any())).willReturn(orderTable);

            // when
            OrderTable actual = orderTableService.create(request);

            // then
            assertThat(actual.getId()).isNotNull();
        }

        @DisplayName("주문 테이블의 이름은 NULL 또는 빈 값을 넣을 수 없다.(IllegalArgumentException)")
        @NullAndEmptySource
        @ParameterizedTest
        void create_name(final String name) {
            // given
            OrderTable request = createRequest(name);

            // when then
            assertThatThrownBy(() -> orderTableService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @DisplayName("주문 테이블을 착석 테이블 상태로 만들 수 있다.")
    @Test
    void sit() {
        // given
        OrderTable orderTable = createOrderTable("1호", 0, false);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        OrderTable sitTable = orderTableService.sit(orderTable.getId());

        // then
        assertThat(sitTable.isOccupied()).isTrue();
    }

    @DisplayName("OrderTableService.clear 메서드 테스트")
    @Nested
    class clear {

        @DisplayName("주문테이블을 빈 테이블 상태로 만들 수 있다.")
        @Test
        void clear() {
            // given
            OrderTable orderTable = createOrderTable("1호", 3, true);

            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            // when
            OrderTable sitTable = orderTableService.clear(orderTable.getId());

            // then
            assertThat(sitTable.isOccupied()).isFalse();
            assertThat(sitTable.getNumberOfGuests()).isEqualTo(0);
        }

        @DisplayName("해당 테이블의 주문이 끝나지 않았을 경우(COMPLETED), 빈 테이블 처리를 할 수 없다.")
        @Test
        void order_not_finish() {
            // given
            OrderTable orderTable = createOrderTable("1호", 3, true);

            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

            // when then
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @DisplayName("OrderTableService.changeNumberOfGuests 메서드 테스트")
    @Nested
    class changeNumberOfGuests {

        @DisplayName("주문 테이블이 착석 테이블일 경우 손님수를 지정 또는 변경할 수 있다.")
        @Test
        void changeNumberOfGuests() {
            // given
            OrderTable request = createRequest(4);

            OrderTable orderTable = createOrderTable("1호", 0, true);

            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            // when
            OrderTable actual = orderTableService.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThat(actual.getNumberOfGuests()).isEqualTo(4);
        }

        @DisplayName("손님 수가 0 미만을 경우 예외를 발생시킨다.(IllegalArgumentException)")
        @Test
        void numberOfGuests_ZERO_LESS_UNDER() {
            // given
            OrderTable request = createRequest(-1);

            // when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블의 상태가 착석 테이블이 아닌 경우 손님 수를 지정 또는 변경할 수 없다.")
        @Test
        void orderTable_Occupied_false() {
            // given
            OrderTable request = createRequest(4);
            OrderTable orderTable = createOrderTable("1호", 0, false);
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            // when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request))
                    .isInstanceOf(IllegalStateException.class);
        }


    }

    @DisplayName("주문 테이블 항목들을 조회할 수 있다. ")
    @Test
    void findAll() {
        OrderTable orderTable1 = createOrderTable("1호", 3, false);
        OrderTable orderTable2 = createOrderTable("2호", 4, false);
        given(orderTableRepository.findAll()).willReturn(List.of(orderTable1, orderTable2));

        List<OrderTable> orderTables = orderTableService.findAll();

        assertThat(orderTables).hasSize(2)
                .extracting("name", "numberOfGuests")
                .containsExactlyInAnyOrder(
                        tuple("1호", 3),
                        tuple("2호", 4)
                );
    }

    private static OrderTable createRequest(String name) {
        OrderTable request = new OrderTable();
        request.setName(name);
        return request;
    }

    private static OrderTable createRequest(int numberOfGuests) {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(numberOfGuests);
        return request;
    }


    private static OrderTable createOrderTable(String name, int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
