package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public
class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    OrderTableService sut;

    private final static UUID uuid = UUID.randomUUID();

    @ParameterizedTest(name = "주문 테이블의 이름이 없으면 주문 테이블을 생성할 수 없다: name = {0}")
    @NullAndEmptySource
    void notCreateOrderTableWithoutName(String name) {
        // given
        OrderTable request = createOrderTable(name, 3);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블을 생성할 수 있다")
    @Test
    void createOrderTable() {
        // given
        OrderTable request = createOrderTable("테이블1", 3);

        given(orderTableRepository.save(any())).willReturn(request);

        // when
        OrderTable result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(OrderTable.class);
    }

    @DisplayName("주문 테이블에 착석 처리를 할 수 있다")
    @Test
    void sit() {
        // given
        OrderTable orderTable = createOrderTable("테이블1", 3);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        sut.sit(uuid);

        // then
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("착석 처리가 되어 있지 않은 주문 테이블에 비움 처리를 할 수 있다")
    @Test
    void notClearIfNoOneIsSitting() {
        // given
        OrderTable orderTable = new OrderTable();

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sut.clear(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블에 비움 처리를 할 수 있다")
    @Test
    void clear() {
        // given
        OrderTable orderTable = createOrderTable("테이블1", 3);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        sut.clear(uuid);

        // then
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @DisplayName("손님의 수가 0미만이면 착석한 손님의 숫자를 바꿀 수 없다")
    @Test
    void notChangeNumberOfGuestsIfNumberOfGuestsIsLessThanZero() {
        // given
        OrderTable request = createOrderTable("테이블1", -1);

        // when & then
        assertThatThrownBy(() -> sut.changeNumberOfGuests(uuid, request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석 상태가 아닌 주문 테이블의 손님의 숫자를 바꿀 수 없다")
    @Test
    void notChangeNumberOfGuestsIfNoOneIsSitting() {
        // given
        OrderTable request = createOrderTable("테이블1", 3);

        OrderTable orderTable = createOrderTable("테이블1", 3);
        orderTable.setOccupied(false);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when & then
        assertThatThrownBy(() -> sut.changeNumberOfGuests(uuid, request))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블의 손님의 숫자를 바꿀 수 있다")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable request = createOrderTable("테이블1", 3);
        OrderTable orderTable = createOrderTable("테이블1", 3);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        sut.changeNumberOfGuests(uuid, request);

        // then
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(3);
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(true);
        return orderTable;
    }
}
