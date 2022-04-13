package kitchenpos.application;


import static kitchenpos.application.OrderTableFixture.삼번_식탁;
import static kitchenpos.application.OrderTableFixture.일번_식탁;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("식탁 관리")
class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("식탁은 이름이 있어야 한다.")
    @ParameterizedTest(name = "식탁 이름: [{arguments}]")
    @NullAndEmptySource
    void createException(String orderTableName) {
        //given
        OrderTable orderTable = 식탁_생성(orderTableName);

        //when
        ThrowingCallable actual = () -> orderTableService.create(orderTable);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식탁을 생성한다. 식탁의 기본 상태는 0명의 손님이며 비어있다.")
    @Test
    void create() {
        //given
        OrderTable 신규_식탁 = 식탁_생성("3번");

        //when
        OrderTable orderTable = orderTableService.create(신규_식탁);

        //then
        assertAll(
            () -> assertThat(orderTable.getName()).isEqualTo(신규_식탁.getName()),
            () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
            () -> assertThat(orderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("손님이 앉을 수 있다. 손님이 앉으면 착석 상태다.")
    @Test
    void sit() {
        //given
        OrderTable 빈_식탁 = 빈_식탁();
        orderTableRepository.save(빈_식탁);

        //when
        OrderTable actual = orderTableService.sit(빈_식탁.getId());

        //then
        assertThat(actual.isEmpty()).isFalse();
    }


    @DisplayName("착석한 손님의 수를 0명 미만으로 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsException() {
        //given
        OrderTable 손님_수_변경 = 식탁_생성(-1);

        //when
        ThrowingCallable actual = () -> orderTableService.changeNumberOfGuests(일번_식탁.getId(), 손님_수_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석하지 않은 식탁은 손님 수를 변경할 수 없다.")
    @Test
    void tableIsEmptyThenChangeNumberOfGuestsException() {
        //given
        OrderTable 빈_식탁 = 빈_식탁();
        orderTableRepository.save(빈_식탁);

        OrderTable 손님_수_변경 = 식탁_생성(3);

        //when
        ThrowingCallable actual = () -> orderTableService.changeNumberOfGuests(빈_식탁.getId(), 손님_수_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("착석한 손님의 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        //given
        OrderTable 삼번_식탁 = 식탁_생성("3번 식탁", 3, false);
        orderTableRepository.save(삼번_식탁);
        OrderTable 손님_수_변경 = 식탁_생성(4);

        //when
        OrderTable orderTable = orderTableService.changeNumberOfGuests(삼번_식탁.getId(), 손님_수_변경);

        //then
        assertAll(
            () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(4),
            () -> assertThat(orderTable.isEmpty()).isFalse()
        );
    }

    @DisplayName("주문완료되지 않은 식탁은 정리할 수 없다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"COMPLETED"}, mode = Mode.EXCLUDE)
    void clearException(OrderStatus orderStatus) {
        //given
        OrderTable 삼번_식탁 = 식탁_생성("3번 식탁", 3, false);
        orderTableRepository.save(삼번_식탁);

        Order 매장_주문 = new Order();
        매장_주문.setOrderTableId(삼번_식탁.getId());
        매장_주문.setStatus(orderStatus);
        orderRepository.save(매장_주문);

        //when
        ThrowingCallable actual = () -> orderTableService.clear(삼번_식탁.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료된 식탁은 정리할 수 있다. 정리된 식탁은 기본 상태가 된다.")
    @Test
    void clear() {
        //given
        OrderTable 삼번_식탁 = 식탁_생성("3번 식탁", 3, false);
        orderTableRepository.save(삼번_식탁);

        Order 매장_주문 = new Order();
        매장_주문.setOrderTableId(삼번_식탁.getId());
        매장_주문.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(매장_주문);

        //when
        OrderTable cleanTable = orderTableService.clear(삼번_식탁.getId());

        //then
        assertAll(
            () -> Assertions.assertThat(cleanTable.getNumberOfGuests()).isZero(),
            () -> Assertions.assertThat(cleanTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("모든 식탁 조회")
    @Test
    void findAll() {
        //given
        orderTableRepository.save(일번_식탁);
        orderTableRepository.save(삼번_식탁);

        //when
        List<OrderTable> orderTables = orderTableService.findAll();

        //then
        assertAll(
            () -> assertThat(orderTables).hasSize(2),
            () -> assertThat(orderTables).containsExactlyInAnyOrder(일번_식탁, 삼번_식탁)
        );
    }

    private OrderTable 식탁_생성(String name) {
        return 식탁_생성(name, 0, true);
    }

    private OrderTable 빈_식탁() {
        return 식탁_생성("빈 식탁", 0, true);
    }

    private OrderTable 식탁_생성(int numberOfGuests) {
        return 식탁_생성("이름없는 식탁", numberOfGuests, false);
    }

    private OrderTable 식탁_생성(String name, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}
