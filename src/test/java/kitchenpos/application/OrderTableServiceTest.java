package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    OrderTableService orderTableService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderTableRepository orderTableRepository;

    @Test
    void 테이블을_생성한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        given(orderTableRepository.save(any())).willReturn(request);

        OrderTable actual = orderTableService.create(request);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 테이블_생성_시_이름이_없으면_에러가_발생한다() {
        OrderTable request = new OrderTable();

        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블을_사용상태로_변경한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        given(orderTableRepository.findById(any())).willReturn(java.util.Optional.of(request));

        OrderTable actual = orderTableService.sit(request.getId());

        assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    void 테이블_사용상태_변경_시_테이블이_없으면_에러가_발생한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.sit(request.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 빈테이블로_상태를_변경한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        given(orderTableRepository.findById(any())).willReturn(Optional.of(request));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

        OrderTable actual = orderTableService.clear(request.getId());

        assertThat(actual.isOccupied()).isFalse();
        assertThat(actual.getNumberOfGuests()).isEqualTo(0);
    }

    @Test
    void 빈테이블로_상태_변경_시_테이블이_없으면_에러가_발생한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.clear(request.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 빈테이블로_상태_변경_시_주문이_존재하면_에러가_발생한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        given(orderTableRepository.findById(any())).willReturn(Optional.of(request));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(request.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 테이블의_손님수를_변경한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        request.setNumberOfGuests(3);
        request.setOccupied(true);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(request));

        OrderTable actual = orderTableService.changeNumberOfGuests(request.getId(), request);

        assertThat(actual.getNumberOfGuests()).isEqualTo(3);
    }

    @Test
    void 테이블의_손님수_변경_시_테이블이_없으면_에러가_발생한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        request.setNumberOfGuests(3);
        request.setOccupied(true);
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 테이블의_손님수_변경_시_손님수가_0미만이면_에러가_발생한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        request.setNumberOfGuests(-1);
        request.setOccupied(true);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 테이블의_손님수_변경_시_테이블이_사용중이_아니면_에러가_발생한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        request.setNumberOfGuests(3);
        request.setOccupied(false);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(request));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(request.getId(), request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 테이블을_전체_조회한다() {
        OrderTable request = new OrderTable();
        request.setId(UUID.randomUUID());
        request.setName("테이블1");
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(request);
        given(orderTableRepository.findAll()).willReturn(orderTables);

        List<OrderTable> actual = orderTableService.findAll();

        assertThat(actual.size()).isEqualTo(1);
    }
}
