package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.OrderTable;
import kitchenpos.mock.fixture.OrderTableFixture;
import kitchenpos.mock.persistence.FakeOrderRepository;
import kitchenpos.mock.persistence.FakeOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class OrderTableServiceTest {

    private static final String ORDER_TABLE_NAME = "9번";
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        FakeOrderTableRepository orderTableRepository = new FakeOrderTableRepository();
        FakeOrderRepository orderRepository = new FakeOrderRepository();
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("이름으로 가게 테이블을 생성할 수 있다.")
    @Test
    void create() {
        // given
        OrderTable orderTable = OrderTableFixture.create(ORDER_TABLE_NAME);

        // when
        OrderTable savedOrderTable = orderTableService.create(orderTable);

        // then
        assertThat(savedOrderTable.getName()).isEqualTo(ORDER_TABLE_NAME);
    }

    @DisplayName("이름 없이 가게 테이블을 생성 시, 예외가 발생한다.")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createNameException(String name) {
        // given
        OrderTable orderTable = OrderTableFixture.create(name);

        // when then
        assertThatThrownBy(() -> orderTableService.create(orderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가게 테이블을 착석 처리할 수 있다.")
    @Test
    void sit() {
        // given
        OrderTable savedOrderTable = orderTableService.create(
            OrderTableFixture.create(ORDER_TABLE_NAME));
        UUID savedId = savedOrderTable.getId();

        // when
        OrderTable updatedOrderTable = orderTableService.sit(savedId);

        // then
        assertThat(updatedOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("존재하지 않는 가게 테이블을 착석 처리 시, 예외가 발생한다.")
    @Test
    void sitException() {
        // given
        UUID nothingnessId = UUID.randomUUID();

        // when then
        assertThatThrownBy(() -> orderTableService.sit(nothingnessId))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가게 테이블을 공석 처리할 수 있다.")
    @Test
    void clear() {
        // given
        OrderTable savedOrderTable = orderTableService.create(
            OrderTableFixture.create(ORDER_TABLE_NAME));
        UUID savedId = savedOrderTable.getId();

        // when
        OrderTable updatedOrderTable = orderTableService.clear(savedId);

        // then
        assertThat(updatedOrderTable.isOccupied()).isFalse();
        assertThat(updatedOrderTable.getNumberOfGuests()).isZero();
    }

    @DisplayName("존재하지 않는 가게 테이블을 공석 처리 시, 예외가 발생한다.")
    @Test
    void clearNoSuchException() {
        // given
        UUID nothingnessId = UUID.randomUUID();

        // when then
        assertThatThrownBy(() -> orderTableService.clear(nothingnessId))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가게 테이블의 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable savedOrderTable = orderTableService.create(
            OrderTableFixture.create(ORDER_TABLE_NAME));
        UUID savedId = savedOrderTable.getId();
        int numberOfGuests = 5;
        OrderTable request = OrderTableFixture.create(numberOfGuests);
        orderTableService.sit(savedId);

        // when
        OrderTable updatedOrderTable = orderTableService.changeNumberOfGuests(savedId, request);

        // then
        assertThat(updatedOrderTable.getId()).isEqualTo(savedId);
        assertThat(updatedOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("0명 미만으로 가게 테이블의 손님 수를 변경 시, 예외가 발생한다.")
    @Test
    void changeNumberOfGuestsException() {
        // given
        OrderTable savedOrderTable = orderTableService.create(
            OrderTableFixture.create(ORDER_TABLE_NAME));
        UUID savedId = savedOrderTable.getId();
        int numberOfGuests = -1;
        OrderTable request = OrderTableFixture.create(numberOfGuests);

        // when then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(savedId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석되지 않은 가게 테이블의 손님 수를 변경 시, 예외가 발생한다.")
    @Test
    void changeNotOccupiedException() {
        // given
        OrderTable savedOrderTable = orderTableService.create(
            OrderTableFixture.create(ORDER_TABLE_NAME));
        UUID savedId = savedOrderTable.getId();
        int numberOfGuests = 2;
        OrderTable request = OrderTableFixture.create(numberOfGuests);

        // when then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(savedId, request))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("저장된 가게 테이블 리스트를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        List<String> orderTableNames = List.of("1번", "2번", "3번", "4번", "5번", "6번", "7번", "8번",
            "9번");
        orderTableNames.forEach(orderTableName -> {
            orderTableService.create(
                OrderTableFixture.create(orderTableName));
        });

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertThat(orderTables)
            .hasSize(orderTableNames.size())
            .extracting(OrderTable::getName)
            .containsExactlyInAnyOrder(orderTableNames.toArray(String[]::new));
    }
}