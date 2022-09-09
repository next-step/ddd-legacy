package kitchenpos.application;

import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.application.support.TestFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderTableRepository = new FakeOrderTableRepository();
        orderRepository = new FakeOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블 생성이 가능하다")
    @Test
    void create_order_table() {
        final OrderTable orderTable = TestFixture.createGeneralOrderTable();

        final OrderTable result = orderTableService.create(orderTable);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(TestFixture.ORDER_TABLE_NAME);
        assertThat(result.getNumberOfGuests()).isEqualTo(TestFixture.ORDER_TABLE_GUEST);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블의 이름이 Null이거나 비어있다면 IllegalArgumentException를 발생시킨다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_order_table_with_nll_and_empty_name(final String name) {
        OrderTable orderTable = TestFixture.createOrderTableWithName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("주문 테이블의 사람이 앉을 수 있다")
    @Test
    void sit() {
        final OrderTable orderTable = TestFixture.createGeneralOrderTable();
        orderTableRepository.save(orderTable);

        final OrderTable result = orderTableService.sit(orderTable.getId());
        assertThat(result).isNotNull();
        assertThat(result.isOccupied()).isTrue();
    }

    @DisplayName("주문이 완료된 상태라면 주문 테이블을 치울 수 있다")
    @Test
    void clear() {
        OrderTable orderTable = TestFixture.createGeneralOrderTable();
        orderTableRepository.save(orderTable);

        final OrderTable result = orderTableService.clear(orderTable.getId());
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블에 손님이 앉았다면 앉아 있는 손님의 숫자를 변경 할 수 있다")
    @Test
    void change_number_of_guest() {
        OrderTable orderTable = TestFixture.createOrderTableWithOccupied(true);
        orderTableRepository.save(orderTable);

        final int changeGuest = 3;
        OrderTable updateOrderTable = TestFixture.createOrderTableWithGuest(changeGuest);

        final OrderTable result = orderTableService.changeNumberOfGuests(updateOrderTable.getId(), updateOrderTable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfGuests()).isEqualTo(changeGuest);
    }

    @DisplayName("주문 테이블을 사용 가능한 손님의 숫자는 음수라면 IllegalArgumentException를 발생시킨다")
    @Test
    void change_number_of_guest_by_negative_number() {
        OrderTable orderTable = TestFixture.createOrderTableWithOccupied(true);

        final int changeGuest = -1;
        orderTable.setNumberOfGuests(changeGuest);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("비어있는 테이블의 손님 숫자를 변경하면 IllegalStateException를 발생시킨다")
    @Test
    void change_number_of_guest_in_occupied() {
        OrderTable orderTable = TestFixture.createOrderTableWithOccupied(false);
        orderTable.setNumberOfGuests(3);

        orderTableRepository.save(orderTable);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("생성된 주문 테이블을 조회 할 수 있다")
    @Test
    void select_all_order_tables() {
        final OrderTable firstOrderTable = TestFixture.createOrderTableWithName("1번 테이블");
        final OrderTable secondOrderTable = TestFixture.createOrderTableWithName("2번 테이블");
        orderTableService.create(firstOrderTable);
        orderTableService.create(secondOrderTable);

        final List<OrderTable> orderTables = Arrays.asList(firstOrderTable, secondOrderTable);

        final List<OrderTable> result = orderTableService.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(orderTables.size());
    }
}
