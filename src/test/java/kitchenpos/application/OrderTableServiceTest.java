package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
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
class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    OrderTableService sut;

    private final static UUID uuid = UUID.randomUUID();

    @ParameterizedTest(name = "주문_테이블의_이름이_없으면_주문_테이블을_생성할_수_없다: name = {0}")
    @NullAndEmptySource
    void 주문_테이블의_이름이_없으면_주문_테이블을_생성할_수_없다(String name) {
        // given
        OrderTable request = createOrderTable(name, 3);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블을_생성할_수_있다() {
        // given
        OrderTable request = createOrderTable("테이블1", 3);

        given(orderTableRepository.save(any())).willReturn(request);

        // when
        OrderTable result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(OrderTable.class);
    }

    @Test
    void 주문_테이블에_착석_처리를_할_수_있다() {
        // given
        OrderTable orderTable = createOrderTable("테이블1", 3);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        sut.sit(uuid);

        // then
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @Test
    void 착석_처리가_되어_있지_않은_주문_테이블에_비움_처리를_할_수_있다() {
        // given
        OrderTable orderTable = new OrderTable();

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sut.clear(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_테이블에_비움_처리를_할_수_있다() {
        // given
        OrderTable orderTable = createOrderTable("테이블1", 3);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        sut.clear(uuid);

        // then
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @Test
    void 손님의_수가_0미만이면_착석한_손님의_숫자를_바꿀_수_없다() {
        // given
        OrderTable request = createOrderTable("테이블1", -1);

        // when & then
        assertThatThrownBy(() -> sut.changeNumberOfGuests(uuid, request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 착석_상태가_아닌_주문_테이블의_손님의_숫자를_바꿀_수_없다() {
        // given
        OrderTable request = createOrderTable("테이블1", 3);

        OrderTable orderTable = createOrderTable("테이블1", 3);
        orderTable.setOccupied(false);

        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when & then
        assertThatThrownBy(() -> sut.changeNumberOfGuests(uuid, request))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_테이블의_손님의_숫자를_바꿀_수_있다() {
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
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(true);
        return orderTable;
    }
}
