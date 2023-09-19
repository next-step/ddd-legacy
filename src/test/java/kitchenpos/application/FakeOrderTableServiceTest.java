package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.FakeOrderRepository;
import kitchenpos.fake.FakeOrderTableRepository;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FakeOrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final OrderRepository orderRepository = new FakeOrderRepository();

    private OrderTableService orderTableService;

    public FakeOrderTableServiceTest() {
        this.orderTableService = new OrderTableService(
                orderTableRepository,
                orderRepository
        );
    }

    @DisplayName("주문 테이블을 등록할 수 있다.")
    @Test
    void create() {
        final OrderTable orderTable = OrderTableFixture.create();
        final OrderTable createOrderTable = orderTableService.create(orderTable);

        assertAll(
                () -> assertThat(orderTable.getName()).isEqualTo(createOrderTable.getName()),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(createOrderTable.getNumberOfGuests()),
                () -> assertThat(orderTable.isOccupied()).isEqualTo(createOrderTable.isOccupied())
        );
    }

    @DisplayName("주문테이블 이름은 비어있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void isNameNullOrZeroLengthException(String name) {
        final OrderTable orderTable = OrderTableFixture.create(name);

        assertThatThrownBy(() -> orderTableService.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블에 착석할 수 있다.")
    @Test
    void orderTableSitTest() {
        final OrderTable orderTable = OrderTableFixture.create();
        OrderTable createOrderTable = orderTableService.create(orderTable);

        orderTableService.sit(createOrderTable.getId());

        assertThat(createOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("주문테이블 착성을 원할 시, 존재하지 않으면, 에러를 발생한다.")
    @Test
    void orderTableSitException() {
        final UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> orderTableService.sit(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 테이블에서 식사가 끝나면 정리를 할 수 있다.")
    @Test
    void orderTableSitClear() {
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create(4, true));
        orderRepository.save(OrderFixture.create(OrderStatus.COMPLETED, orderTable));

        OrderTable clearOrderTable = orderTableService.clear(orderTable.getId());

        assertAll(
                () -> assertThat(clearOrderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(clearOrderTable.isOccupied()).isFalse()
        );
    }

    @DisplayName("주문 테이블을 정리할때, 존재하지 않으면 에러를 발생한다.")
    @Test
    void isNotExistorderTableException() {
        final UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> orderTableService.clear(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문테이블을 정리할 때, 주문 상태가 완료된 상태가 아니면, 에러를 발생한다.")
    @Test
    void isExistOrderException() {
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create(4, true));
        orderRepository.save(OrderFixture.create(OrderStatus.WAITING, orderTable));

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("착석인원 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuestsTest() {
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create(3, true));
        orderTable.setNumberOfGuests(3);

        orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(3);
    }

    @DisplayName("착석인원 변경 시, 착석인원은 0미만이면 에러를 반환한다.")
    @Test
    void changeNumberOfGuestsZeroException() {
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create(3, true));
        orderTable.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석 인원 변경시, 주문테이블이 없을 시 에러를 반환한다.")
    @Test
    void changeNumberOfGuestsNotFoundOrderTableException() {
        OrderTable orderTable = OrderTableFixture.create();
        orderTable.setNumberOfGuests(3);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("착석 인원 변경시, 착석중이 아니면 에러를 발생시킨다.")
    @Test
    void changeNumberOfGuestsNotOccupiedException() {
        OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create(3, false));
        orderTable.setNumberOfGuests(4);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);

    }

}
