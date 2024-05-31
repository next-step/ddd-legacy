package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixtures.Fixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock private OrderTableRepository orderTableRepository;
    @Mock private OrderRepository orderRepository;
    @InjectMocks private OrderTableService orderTableService;

    @Test
    @DisplayName(value = "주문테이블을 생성하기 위해 이름을 입력해야 한다.")
    void case1() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();

        BDDMockito.given(orderTableRepository.save(any())).willReturn(orderTable);

        final OrderTable actual = orderTableService.create(orderTable);
        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName(value = "주문테이블의 자리가 비어있도록 생성된다.")
    void case2() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();

        BDDMockito.given(orderTableRepository.save(any())).willReturn(orderTable);

        final OrderTable actual = orderTableService.create(orderTable);
        Assertions.assertThat(actual.isOccupied()).isFalse();
    }

    @Test
    @DisplayName(value = "주문테이블을 사용하기 위해 주문테이블이 존재해야 한다.")
    void case3() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();

        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
        Assertions.assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    @DisplayName(value = "주문테이블의 자리를 고객이 앉도록 설정한다.")
    void case4() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();

        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
        Assertions.assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    @DisplayName(value = "주문테이블에 앉은 인원을 입력하기 위해 주문테이블이 존재해야 한다.")
    void case5() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();

        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName(value = "주문테이블에 앉은 인원을 입력하기 위해 1명 이상은 있어야 한다.")
    void case6() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();

        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName(value = "주문테이블에 앉은 인원을 입력하기 위해 자리가 비어있어야 한다.")
    void case7() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();
        orderTable.setOccupied(true);

        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        Assertions.assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.sit(orderTable.getId(), 3));
    }

    @Test
    @DisplayName(value = "주문테이블을 청소하기 위해 주문테이블이 존재해야 한다, 주문테이블을 청소하기 위해 주문이 {완료} 되어야 한다.")
    void case8() {
        final OrderTable orderTable = Fixture.fixtureOrderTable();
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(5);

        BDDMockito.given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        BDDMockito.given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

        final OrderTable clear = orderTableService.clear(orderTable.getId());
        Assertions.assertThat(clear.isOccupied()).isFalse();
        Assertions.assertThat(clear.getNumberOfGuests()).isZero();
    }
}
