package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.exception.OrderTableIsUsingException;
import kitchenpos.inMemory.InMemoryOrderRepository;
import kitchenpos.inMemory.InMemoryOrderTableRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static kitchenpos.fixture.OrderTableFixture.createSimpleOrderTable;
import static org.assertj.core.api.Assertions.*;

class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블은 반드시 고유 ID, 테이블명, 손님수, 자리 사용여부가 있어야 한다.")
    @Test
    void create() {

        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("Number1");

        // when
        final OrderTable actual = orderTableService.create(orderTable);

        // then
        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isNotNull(),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isEmpty()).isEqualTo(Boolean.TRUE)
        );
    }

    @DisplayName("등록한 모든 주문 테이블 목록을 볼 수 있다.")
    @Test
    void findAll() {

        // given
        orderTableRepository.save(createSimpleOrderTable("Number1"));
        orderTableRepository.save(createSimpleOrderTable("Number2"));

        // when
        final List<OrderTable> actual = orderTableService.findAll();

        // then
        assertThat(actual).hasSize(2);
    }

    @DisplayName("비어있는 테이블에 손님이 앉을 수 있다.")
    @Test
    void sit() {

        // given
        final OrderTable orderTable = orderTableRepository.save(createSimpleOrderTable("Number1"));

        // when - then
        assertThat(orderTableService.sit(orderTable.getId())
                .isEmpty())
                .isNotEqualTo(Boolean.TRUE);
    }

    @DisplayName("손님이 식사를 완료할 때 까지 테이블을 치울 수 없다.")
    @Test
    void clearThrowException() {

        // given
        final OrderTable orderTable = orderTableRepository.save(createSimpleOrderTable("Number1"));

        final OrderTable finalOrderTable = orderTableService.sit(orderTable.getId());

        // when - then
        assertThatThrownBy(() -> orderTableService.clear(finalOrderTable.getId()))
                .isInstanceOf(OrderTableIsUsingException.class);
    }

    @DisplayName("테이블 손님의 수를 0명 이상으로 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {4, 6, 8})
    void changeNumberOfGuests(int numberOfGuests) {
        // given
        final OrderTable orderTable = orderTableRepository.save(createSimpleOrderTable("Number1"));

        // when
        OrderTable actual = orderTableService.sit(orderTable.getId());
        orderTable.setNumberOfGuests(numberOfGuests);
        actual = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("[추가] 테이블 손님의 수는 0명 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -8})
    void checkNumberOfGuests(int numberOfGuests) {
        // given
        final OrderTable orderTable = orderTableRepository.save(createSimpleOrderTable("Number1"));

        // when
        OrderTable actual = orderTableService.sit(orderTable.getId());
        orderTable.setNumberOfGuests(numberOfGuests);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
