package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static kitchenpos.Fixtures.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;


    @Test
    @DisplayName("주문테이블을 생성한다.")
    void create01() {
        OrderTable orderTable = createOrderTable("1번 테이블", false);

        OrderTable savedOrderTable = orderTableService.create(orderTable);

        OrderTable findOrderTable = orderTableRepository.findById(savedOrderTable.getId()).orElseThrow();
        assertThat(findOrderTable.getId()).isNotNull();
    }

    @Test
    @DisplayName("테이블의 이름은 비어있을 수 없다.")
    void create02() {
        OrderTable orderTable = createOrderTable(null, false);

        assertThatThrownBy(() -> orderTableService.create(orderTable)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블에 손님이 앉으면, 테이블을 점유한다.")
    void sit01() {
        OrderTable orderTable = createOrderTable("1번 테이블", false);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        assertThat(orderTable.isOccupied()).isFalse();

        OrderTable sitOrderTable = orderTableService.sit(savedOrderTable.getId());

        assertThat(sitOrderTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블을 치운다.")
    void clear01() {
        OrderTable orderTable = createOrderTable("1번 테이블", true);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);

        OrderTable sitOrderTable = orderTableService.clear(savedOrderTable.getId());

        assertThat(sitOrderTable.isOccupied()).isFalse();
        assertThat(sitOrderTable.getNumberOfGuests()).isZero();
    }

    @Test
    @DisplayName("테이블 손님의 수를 변경한다.")
    void changeNumberOfGuests01() {
        OrderTable orderTable = createOrderTable("1번 테이블", true);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        orderTable.setNumberOfGuests(10);

        OrderTable changedOrderTable = orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTable);

        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(10);
    }

    @Test
    @DisplayName("손님의 수는 0보다 작을 수 없다.")
    void changeNumberOfGuests02() {
        OrderTable orderTable = createOrderTable("1번 테이블", true);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        orderTable.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블이 점유되어있지 않으면 변경할 수 없다.")
    void changeNumberOfGuests03() {
        OrderTable orderTable = createOrderTable("1번 테이블", false);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }
}
