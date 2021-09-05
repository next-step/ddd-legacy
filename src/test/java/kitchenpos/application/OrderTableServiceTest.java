package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.mock.InMemoryOrderRepository;
import kitchenpos.mock.InMemoryOrderTableRepository;
import kitchenpos.utils.fixture.OrderFixture;
import kitchenpos.utils.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableServiceTest {
    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 등록할 수 있다.")
    @Test
    void create() {
        final OrderTable orderTable = OrderTableFixture.주문테이블();
        final OrderTable saved = 주문테이블등록(orderTable);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(orderTable.getName()),
                () -> assertThat(saved.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(saved.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문 테이블 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        final OrderTable orderTable = OrderTableFixture.주문테이블();
        orderTable.setName(name);

        assertThatThrownBy(() -> 주문테이블등록(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비어있는 주문 테이블에 앉을 수 있다.")
    @Test
    void sit() {
        final OrderTable saved = orderTableRepository.save(OrderTableFixture.주문테이블());

        final OrderTable expected = 주문테이블에앉기(saved.getId());

        assertThat(expected.isEmpty()).isFalse();
    }

    @DisplayName("테이블을 비울 수 있다.")
    @Test
    void clear() {
        final OrderTable orderTable = OrderTableFixture.주문테이블();
        orderTable.setEmpty(false);
        final OrderTable seated = orderTableRepository.save(orderTable);

        final OrderTable expected = 주문테이블비우기(seated.getId());

        assertAll(
                () -> assertThat(expected.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(expected.isEmpty()).isTrue()
        );
    }

    @DisplayName("해당 테이블에 식사가 완료되지 않은 주문이 있다면 테이블을 비울 수 없다.")
    @Test
    void clear_order() {
        final OrderTable table = orderTableRepository.save(OrderTableFixture.주문테이블());
        final Order order = orderRepository.save(OrderFixture.매장주문(new Menu(), table));

        assertThatThrownBy(() -> 주문테이블비우기(table.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블에 앉은 인원수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        final OrderTable seated = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(5);

        final OrderTable expected = 주문테이블인원수변경(seated.getId(), request);

        assertThat(expected.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
    }

    @DisplayName("테이블에 앉을 인원수는 0이상이어야 한다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void changeNumberOfGuests(int numberOfGuests) {
        final OrderTable seated = orderTableRepository.save(OrderTableFixture.앉은테이블());
        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(numberOfGuests);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 주문테이블인원수변경(seated.getId(), request));
    }

    @DisplayName("테이블의 인원수를 변경하려면 테이블에 이미 앉아있어야한다")
    @Test
    void changeNumberOfGuests_sit() {
        final OrderTable saved = orderTableRepository.save(OrderTableFixture.주문테이블());
        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(5);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 주문테이블인원수변경(saved.getId(), request));
    }

    @DisplayName("테이블을 전체조회할 수 있다.")
    @Test
    void findAll() {
        final OrderTable saved1 = orderTableRepository.save(OrderTableFixture.주문테이블());
        final OrderTable saved2 = orderTableRepository.save(OrderTableFixture.주문테이블());

        List<OrderTable> expected = 주문테이블전체조회();

        assertThat(expected).containsOnly(saved1, saved2);
    }

    private OrderTable 주문테이블등록(final OrderTable orderTable) {
        return orderTableService.create(orderTable);
    }

    private OrderTable 주문테이블에앉기(final UUID orderTableId) {
        return orderTableService.sit(orderTableId);
    }

    private OrderTable 주문테이블비우기(final UUID orderTableId) {
        return orderTableService.clear(orderTableId);
    }

    private OrderTable 주문테이블인원수변경(final UUID orderTableId, OrderTable request) {
        return orderTableService.changeNumberOfGuests(orderTableId, request);
    }

    private List<OrderTable> 주문테이블전체조회() {
        return orderTableService.findAll();
    }
}
