package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.dummy.DummyOrderTable.createOrderTable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OrderTableServiceTest {

    OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    OrderRepository orderRepository = new InMemoryOrderRepository();
    OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("[정상] 주문 테이블을 등록한다.")
    @Test
    void create() {
        OrderTable orderTable = createOrderTable();
        OrderTable actual = orderTableService.create(orderTable);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse(),
                () -> assertEquals(orderTable.getName(), actual.getName()));
    }

    @DisplayName("[오류] 테이블 이름이 없으면 테이블을 등록할 수 없다.")
    @Test
    void create_null_name() {
        OrderTable orderTable = createOrderTable();
        orderTable.setName(null);
        assertThatThrownBy(() -> orderTableService.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[정상] 주문 테이블에 손님이 앉는다.")
    @Test
    void sit() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        OrderTable actual = orderTableService.sit(orderTable.getId());
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertTrue(orderTable.isOccupied()));
    }

    @DisplayName("[정상] 주문 테이블의 손님이 모두 나간다.")
    @Test
    void clear() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        OrderTable sit = orderTableService.sit(orderTable.getId());
        OrderTable actual = orderTableService.clear(sit.getId());
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertFalse(orderTable.isOccupied()));
    }

    @DisplayName("[정상] 손님이 앉지 않은 테이블은 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_false() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        orderTable.setNumberOfGuests(10);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 손님이 앉은 테이블은 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests_true() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        orderTableService.sit(orderTable.getId());
        orderTable.setNumberOfGuests(10);
        OrderTable actual = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertEquals(10, orderTable.getNumberOfGuests()));
    }

    @DisplayName("[오류] 테이블의 손님 수를 0 미만으로 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_under_0_guest_test() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        orderTableService.sit(orderTable.getId());
        orderTable.setNumberOfGuests(-1);
        assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 테이블 점유 상태가 아니면 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_not_occupied_test() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 테이블을 조회한다.")
    @Test
    void findAll() {
        orderTableService.create(createOrderTable("테이블1"));
        orderTableService.create(createOrderTable("테이블2"));
        orderTableService.create(createOrderTable("테이블3"));
        assertThat(orderTableService.findAll().size()).isSameAs(3);
    }
}