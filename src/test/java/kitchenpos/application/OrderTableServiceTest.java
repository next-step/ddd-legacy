package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.objectmother.OrderTableMaker.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableRepository orderTableRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableService orderTableService;

    @DisplayName("테이블생성시 요청한 데이터로 테이블이 생성되야 한다.")
    @Test
    void 테이블생성() {
        // when
        OrderTable orderTable = orderTableService.create(테이블_1);

        // then
        assertThat(orderTable.getName()).isEqualTo(테이블_1.getName());
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(테이블_1.getNumberOfGuests());
        assertThat(orderTable.isOccupied()).isEqualTo(테이블_1.isOccupied());
    }

    @DisplayName("테이블생성 시 이름이 없을경우 에러를 던진다.")
    @Test
    void 테이블생성실패_이름없음() {
        // when then
        assertThatThrownBy(() -> orderTableService.create(테이블_이름없음))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블에 착석시 해당 테이블을 조회할경우 착석여부가 착석상태로 조회되야 한다.")
    @Test
    void 테이블착석() {
        // given
        OrderTable orderTable = orderTableService.create(테이블_1);

        // when
        OrderTable sitOrderTable = orderTableService.sit(orderTable.getId());

        // then
        assertThat(sitOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("테이블에 착석시 테이블이 존재하지 않을경우 에러를 던진다.")
    @Test
    void 테이블착석실패_테이블미존재() {
        // when then
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블을 치울경우 고객 수는 0으로 변경되며 착석여부는 비착석으로 변경된다.")
    @Test
    void 테이블청소() {
        // given
        OrderTable orderTable = orderTableService.create(테이블_1);
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

        // when
        OrderTable clearOrderTable = orderTableService.clear(orderTable.getId());

        // then
        assertThat(clearOrderTable.getNumberOfGuests()).isZero();
        assertThat(clearOrderTable.isOccupied()).isFalse();
    }

    @DisplayName("테이블에 착석 한 고객의 주문이 처리되지 않은 경우 테이블을 초기화 할 수 없다.")
    @Test
    void 테이블청소실패_테이블주문미처리() {
        // given
        OrderTable orderTable = orderTableService.create(테이블_1);
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

        // when then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("착석한 테이블에 고객수를 얘기하면 변경된다.")
    @Test
    void 고객수변경() {
        // given
        OrderTable orderTable = orderTableService.create(테이블_1);
        OrderTable sitOrderTable = orderTableService.sit(orderTable.getId());

        // when
        OrderTable guestOrderTable = orderTableService.changeNumberOfGuests(sitOrderTable.getId(), 테이블_고객_4명);

        // then
        assertThat(guestOrderTable.getNumberOfGuests()).isEqualTo(4);
        assertThat(guestOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("착석한 테이블에 음수에 고객수를 얘기하면 에러를 던진다.")
    @Test
    void 고객수변경실패_고객수음수() {
        // given
        OrderTable orderTable = orderTableService.create(테이블_1);
        OrderTable sitOrderTable = orderTableService.sit(orderTable.getId());

        // when then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(sitOrderTable.getId(), 테이블_고객_음수))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석하지 않은 테이블에 고객수를 얘기하면 에러를 던진다.")
    @Test
    void 고객수변경실패_미착석테이블() {
        // given
        OrderTable orderTable = orderTableService.create(테이블_1);

        // when then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), 테이블_고객_4명))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 전체조회시 지금까지 등록된 테이블이 전부 조회되야 한다.")
    @Test
    void 테이블전체조회() {
        // given
        orderTableService.create(테이블_1);
        orderTableService.create(테이블_2);

        // when
        List<OrderTable> tables = orderTableService.findAll();

        // when
        assertThat(tables)
                .hasSize(2)
                .extracting(OrderTable::getName, OrderTable::getNumberOfGuests, OrderTable::isOccupied)
                .containsExactlyInAnyOrder(
                        Tuple.tuple(테이블_1.getName(), 테이블_1.getNumberOfGuests(), 테이블_1.isOccupied()),
                        Tuple.tuple(테이블_2.getName(), 테이블_2.getNumberOfGuests(), 테이블_2.isOccupied())
                );
    }

}