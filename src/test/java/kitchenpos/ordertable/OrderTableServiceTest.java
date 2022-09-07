package kitchenpos.ordertable;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kitchenpos.ordertable.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @DisplayName("주문 테이블을 생성한다.")
    @Test
    void create() {
        when(orderTableRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        OrderTable 일번테이블 = orderTable("1번 테이블");

        OrderTable 생성된_주문테이블 = orderTableService.create(일번테이블);

        assertAll(
                () -> assertThat(일번테이블.getName()).isEqualTo(생성된_주문테이블.getName()),
                () -> assertThat(일번테이블.getNumberOfGuests()).isEqualTo(생성된_주문테이블.getNumberOfGuests()),
                () -> assertThat(일번테이블.isOccupied()).isEqualTo(생성된_주문테이블.isOccupied())
        );
    }

    @DisplayName("이름이 없는 주문 테이블을 생성할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithNullName(String name) {
        OrderTable 일번테이블 = orderTable(name);

        assertThatThrownBy(() -> orderTableService.create(일번테이블))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블에 앉는다.")
    @Test
    void sit() {
        OrderTable 일번테이블 = orderTableWithRandomId("1번 테이블");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(일번테이블));

        OrderTable orderTable = orderTableService.sit(일번테이블.getId());

        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("테이블을 치운다.")
    @Test
    void clear() {
        OrderTable 일번테이블 = orderTableWithRandomId("1번 테이블");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(일번테이블));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

        OrderTable orderTable = orderTableService.clear(일번테이블.getId());

        assertThat(orderTable.isOccupied()).isFalse();
    }

    @DisplayName("완료되지 않은 주문이 있는 테이블을 치울 수 없다.")
    @Test
    void clearFail() {
        OrderTable 일번테이블 = orderTableWithRandomId("1번 테이블");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(일번테이블));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(일번테이블.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블의 인원 수를 변경한다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable 일번테이블 = orderTableWithRandomId("1번 테이블");
        일번테이블.setOccupied(true);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(일번테이블));

        OrderTable orderTable = orderTableService.changeNumberOfGuests(일번테이블.getId(), changeOrderTableRequest(2));

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(2);
    }

    @DisplayName("인원 수를 음수로 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsNegative() {
        OrderTable 일번테이블 = orderTableWithRandomId("1번 테이블");
        일번테이블.setOccupied(true);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(일번테이블.getId(), changeOrderTableRequest(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("손님이 앉은 상태가 아닌 경우 인원 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsNotOccupied() {
        OrderTable 일번테이블 = orderTableWithRandomId("1번 테이블");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(일번테이블));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(일번테이블.getId(), changeOrderTableRequest(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블 목록을 조회한다.")
    @Test
    void findAll() {
        OrderTable 일번테이블 = orderTable("1번 테이블");
        OrderTable 이번테이블 = orderTable("2번 테이블");
        OrderTable 삼번테이블 = orderTable("3번 테이블");
        when(orderTableRepository.findAll()).thenReturn(List.of(일번테이블, 이번테이블, 삼번테이블));

        List<String> orderTables = orderTableService.findAll().stream()
                .map(OrderTable::getName)
                .collect(Collectors.toList());

        assertThat(orderTables).containsExactly("1번 테이블", "2번 테이블", "3번 테이블");
    }
}
