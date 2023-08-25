package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;
    private UUID 가_테이블id;

    @BeforeEach
    void setUp() {
        가_테이블id = UUID.randomUUID();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("매장 테이블을 등록한다.")
    class create {
        @DisplayName("가격이 null이면 매장 테이블 등록이 불가능 하다.")
        @ParameterizedTest
        @NullAndEmptySource
        void priceIsNull(String name) {
            //given
            OrderTable 가_테이블 = new OrderTable(가_테이블id, name);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(가_테이블));
        }

        @DisplayName("가격이 0보다 작으면 매장 테이블 등록이 불가능 하다.")
        @Test
        void priceIsUnderZero() {
            //given
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "");

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(가_테이블));
        }

        @DisplayName("매장 테이블이 정상 등록된다.")
        @Test
        void normalCreate() {
            //given
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
            given(orderTableRepository.save(any())).willReturn(가_테이블);

            //when
            OrderTable returnOrderTable = orderTableService.create(가_테이블);

            //then
            assertThat(returnOrderTable.getName()).isEqualTo(가_테이블.getName());
        }
    }

    @Nested
    @DisplayName("매장 테이블에 손님이 앉는다.")
    class sit {

        @DisplayName("등록되지 않은 매장 테이블을 요청하면  매장 테이블에 앉을 수 없다.")
        @Test
        void notExitsOrderTable() {
            //given
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.empty());

            //when
            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.sit(가_테이블id));
        }

        @DisplayName("정상 착석 처리 된다.")
        @Test
        void normalSit() {
            //given
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.of(가_테이블));

            //when
            OrderTable returnOrderTable = orderTableService.sit(가_테이블id);

            //then
            assertThat(returnOrderTable.isOccupied()).isTrue();
        }
    }

    @Nested
    @DisplayName("매장 테이블을 치운다(clear).")
    class clear {
        @DisplayName("등록되지 않은 매장 테이블을 치우면 치울 수 없다.")
        @Test
        void notExitsOrderTableByClear() {
            //given
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.empty());

            //when
            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.clear(가_테이블id));
        }

        @DisplayName("완료되지 않은 주문을 치우면 치울 수 없다.")
        @Test
        void notCompletedOrder() {
            //given
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.of(가_테이블));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                .willReturn(true);

            //when
            //then
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(가_테이블id));
        }

        @DisplayName("정상적으로 매장테이블이 치워(clear)진다.")
        @Test
        void normalClear() {
            //given
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
            가_테이블.setNumberOfGuests(3);
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.of(가_테이블));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                .willReturn(false);

            //when
            OrderTable returnOrderTable = orderTableService.clear(가_테이블id);

            //then
            assertThat(returnOrderTable.isOccupied()).isFalse();
            assertThat(returnOrderTable.getNumberOfGuests()).isSameAs(0);
        }
    }

    @Nested
    @DisplayName("매장테이블이 앉은 손님 수를 변경한다.")
    class changeNumberOfGuests {
        @DisplayName("변경 요청한 숫자가 0보다 작으면 손님 수를 변경 할 수 없다.")
        @Test
        void guestNumberUnderZero() {
            //given
            OrderTable 변경_요청_테이블 = new OrderTable();
            변경_요청_테이블.setNumberOfGuests(-1);

            //when
            //then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(가_테이블id, 변경_요청_테이블));
        }

        @DisplayName("등록되지 않은 매장 테이블을 요청하면  손님 수를 변경 할 수 없다.")
        @Test
        void notExitsOrderTable() {
            //given
            OrderTable 변경_요청_테이블 = new OrderTable();
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.empty());

            //when
            //then
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(가_테이블id, 변경_요청_테이블));
        }

        @DisplayName("착석하지 않은 매장테이블의 손님수를 변경하면  손님 수를 변경 할 수 없다.")
        @Test
        void isOccupiedTable() {
            //given
            int changedGuest = 3;
            OrderTable 변경_요청_테이블 = new OrderTable();
            변경_요청_테이블.setNumberOfGuests(changedGuest);
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.of(가_테이블));

            //when
            //then
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(가_테이블id, 변경_요청_테이블));
        }

        @DisplayName("정상적으로 손님수가 변경 된다.")
        @Test
        void normalChanged() {
            //given
            int changedGuest = 3;
            OrderTable 변경_요청_테이블 = new OrderTable();
            변경_요청_테이블.setNumberOfGuests(changedGuest);
            OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
            가_테이블.setOccupied(true);
            given(orderTableRepository.findById(가_테이블id)).willReturn(Optional.of(가_테이블));

            //when
            OrderTable returnOrderTable = orderTableService.changeNumberOfGuests(가_테이블id, 변경_요청_테이블);
            //then
            assertThat(returnOrderTable.getNumberOfGuests()).isSameAs(changedGuest);
        }
    }

    @DisplayName("매장 테이블을 전체 조회 된다.")
    @Test
    void findAll() {
        //given
        OrderTable 가_테이블 = new OrderTable(가_테이블id, "가");
        OrderTable 나_테이블 = new OrderTable(가_테이블id, "가");
        given(orderTableRepository.findAll()).willReturn(List.of(가_테이블, 나_테이블));

        //when
        List<OrderTable> orderTables = orderTableService.findAll();

        //then
        assertThat(orderTables).containsOnly(가_테이블, 나_테이블);
    }

}