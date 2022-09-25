package kitchenpos.application;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("주문 테이블 테스트")
class OrderTableServiceTest {
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 생성한다.")
    @Test
    void create() {
        OrderTable request = new OrderTable();
        request.setName("주문테이블1");

        final OrderTable orderTable = orderTableService.create(request);

        assertAll(
                () -> assertThat(orderTable.isOccupied()).isFalse(),
                () -> assertThat(orderTable.getName()).isEqualTo(request.getName()),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("주문 테이블의 이름은 필수 이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void orderTable_name_is_NotEmpty(String name) {
        OrderTable request = new OrderTable();
        request.setName(name);

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderTableService.create(request)
        );
    }

    @DisplayName("등록된 주문테이블이 없으면 주문 테이블에 손님 있는 상태로 변경할 수 없다.")
    @Test
    void sitIsExistOrderTable() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.sit(UUID.randomUUID()));
    }

    @DisplayName("주문 테이블에 손님이 있는 상태로 변경한다.")
    @Test
    void sit() {
        OrderTable orderTable = 주문테이블이_생성됨(createOrderTable());

        final OrderTable sitOrderTable = orderTableService.sit(orderTable.getId());

        assertThat(sitOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("등록된 주문테이블이 없으면 주문 테이블에 빈 테이블로 변경할 수 없다.")
    @Test
    void clearIsExistOrderTable() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));
    }

    @DisplayName("주문이 완료되지 않은 주문 테이블을 빈테이블로 변경이 불가능하다.")
    @Test
    void orderComplete_Clear_Is_Enable_Clear() {
        final OrderTable 생성된_주문_테이블 = 주문테이블이_생성됨(createOrderTable());
        final Order order = new Order();
        order.setOrderTable(생성된_주문_테이블);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTableId(생성된_주문_테이블.getId());
        orderRepository.save(order);

        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(생성된_주문_테이블.getId()));
    }


    @DisplayName("빈테이블로 변경한다.")
    @Test
    void clear() {
        final OrderTable 생성된_주문_테이블 = 주문테이블이_생성됨(createOrderTable());

        final OrderTable clearTable = orderTableService.clear(생성된_주문_테이블.getId());

        assertAll(
                () -> assertThat(clearTable.isOccupied()).isFalse(),
                () -> assertThat(clearTable.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("변경할 손님수가 0미만 일 경우 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_not_lessThen_zero() {
        final OrderTable 생성된_주문_테이블 = 주문테이블이_생성됨(createOrderTable());
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderTableService.changeNumberOfGuests(생성된_주문_테이블.getId(), request)
        );

    }

    @DisplayName("변경할 주문 테이블이 존재 해야 손님수 변경이 가능하다.")
    @Test
    void changeNumberOfGuests_exist_orderTable() {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(1);
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                orderTableService.changeNumberOfGuests(UUID.randomUUID(), request)
        );
    }

    @DisplayName("변경할 주문 테이블이 주문 가능한 테이블이어야 가능하다.")
    @Test
    void changeNumberOfGuests_exist_orderTable_isOccupied() {
        final OrderTable 생성된_주문_테이블 = 주문테이블이_생성됨(createOrderTable());

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(1);

        assertThatIllegalStateException().isThrownBy(() ->
                orderTableService.changeNumberOfGuests(생성된_주문_테이블.getId(), request)
        );
    }

    @DisplayName("변경할 주문 테이블이 주문 가능한 테이블이어야 가능하다.")
    @Test
    void changeNumberOfGuests() {
        final OrderTable 생성된_주문_테이블 = 주문테이블이_생성됨(createOrderTable());
        orderTableService.sit(생성된_주문_테이블.getId());

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(1);

        final OrderTable changeOrderTable = orderTableService.changeNumberOfGuests(생성된_주문_테이블.getId(), request);

        assertThat(changeOrderTable.getNumberOfGuests()).isEqualTo(1);
    }

    @DisplayName("등록된_주문테이블을_조회한다")
    @Test
    void findAll() {
        주문테이블이_생성됨(createOrderTable("주문테이블1"));
        주문테이블이_생성됨(createOrderTable("주문테이블2"));

        final List<OrderTable> orderTalTables = orderTableService.findAll();

        assertThat(orderTalTables).hasSize(2);
        assertThat(orderTalTables).extracting("name").contains("주문테이블1", "주문테이블2");
    }


    private OrderTable 주문테이블이_생성됨(OrderTable orderTable) {
        return orderTableService.create(orderTable);
    }


}

