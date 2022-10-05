package kitchenpos.application;

import kitchenpos.application.fakeobject.FakeOrderRepository;
import kitchenpos.application.fakeobject.FakeOrderTableRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTableServiceTest {
    private OrderTableService orderTableService;

    private FakeOrderTableRepository fakeOrderTableRepository;

    private FakeOrderRepository fakeOrderRepository;

    @BeforeEach
    void setUp() {
        this.fakeOrderTableRepository = new FakeOrderTableRepository();
        this.fakeOrderRepository = new FakeOrderRepository();
        this.orderTableService = new OrderTableService(fakeOrderTableRepository, fakeOrderRepository);
    }

    @DisplayName("이름이 없으면 주문테이블 추가 실패한다.")
    @Test
    public void create_with_null_order_table_name() {
        //given
        OrderTable orderTable = new OrderTable();

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderTableService.create(orderTable));
    }

    @DisplayName("이름이 없으면 주문테이블 추가 실패한다.")
    @Test
    public void create_with_order_table_name() {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setName("asdf");

        //when & then
        assertThat(orderTableService.create(orderTable)).isNotNull();
    }

    @DisplayName("주문 테이블이 존재하지 않으면 테이블 착석 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderTableId")
    @ParameterizedTest
    public void sit_non_exist_order_table(UUID orderTableId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderTableService.sit(orderTableId));
    }

    @DisplayName("주문 테이블이 존재하면 테이블 착석 성공한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideExistOrderTableId")
    @ParameterizedTest
    public void sit_exist_order_table(UUID orderTableId) {
        //given & when & then
        assertThat(orderTableService.sit(orderTableId))
                .isNotNull();
    }

    @DisplayName("주문 테이블이 존재하지 않으면 테이블 비우기 실패한다.")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderTableId")
    @ParameterizedTest
    public void clear_non_exist_order_table(UUID orderTableId) {
        //given & when & then
        assertThrows(NoSuchElementException.class, () -> orderTableService.clear(orderTableId));
    }

    @DisplayName("주문 테이블의 주문이 완료되지 않았을 경우 테이블 비우기 실패한다.")
    @Test
    public void clear_not_completed_order_table() {
        //given
        OrderTable orderTable = makeRandomOrderTable();
        fakeOrderTableRepository.save(orderTable);

        Order order = makeRandomOrder();
        order.setStatus(OrderStatus.WAITING);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        fakeOrderRepository.save(order);

        fakeOrderRepository.setOrderTablesOnOrder(fakeOrderTableRepository.findAll());

        //when & then
        assertThrows(IllegalStateException.class, () -> orderTableService.clear(orderTable.getId()));
    }

    @DisplayName("주문 테이블의 주문이 완료되었을 경우 테이블 비우기 실행한다.")
    @Test
    public void clear_completed_order_table() {
        //given
        OrderTable orderTable = makeRandomOrderTable();
        fakeOrderTableRepository.save(orderTable);

        Order order = makeRandomOrder();
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        fakeOrderRepository.save(order);

        //when & then
        assertThat(orderTableService.clear(orderTable.getId()))
                .isNotNull();
    }

    @DisplayName("손님수가 음수일 경우 손님수 변경 실패.")
    @Test
    public void changeNumberOfGuests_minus_guest() {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(-1);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> orderTableService.changeNumberOfGuests(null, orderTable));
    }

    @DisplayName("주문테이블이 없을 경우 손님수 변경 실패.")
    @MethodSource("kitchenpos.application.InputProvider#provideNonExistOrderTableId")
    @ParameterizedTest
    public void changeNumberOfGuests_non_exist_order_table(UUID orderTableId) {
        //given
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(1);

        //when & then
        assertThrows(NoSuchElementException.class, () -> orderTableService.changeNumberOfGuests(orderTableId, orderTable));
    }

    @DisplayName("주문 테이블이 점유되지 않았을 경우 손님수 변경 실패.")
    @Test
    public void changeNumberOfGuests_not_occupied_order_table() {
        //given
        OrderTable orderTable = makeRandomOrderTable();
        orderTable.setOccupied(false);
        fakeOrderTableRepository.save(orderTable);

        //when & then
        assertThrows(IllegalStateException.class, () -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("주문 테이블이 점유되었고 손님수가 양수이면 손님수 변경 성공.")
    @Test
    public void changeNumberOfGuests_occupied_order_table() {
        //given
        OrderTable orderTable = makeRandomOrderTable();
        orderTable.setOccupied(true);
        fakeOrderTableRepository.save(orderTable);

        //when & then
        assertThat(orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isNotNull();
    }

    private OrderTable makeRandomOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("asdf");
        return orderTable;
    }

    private Order makeRandomOrder() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setType(OrderType.DELIVERY);
        return order;
    }
}
