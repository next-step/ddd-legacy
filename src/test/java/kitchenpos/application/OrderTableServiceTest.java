package kitchenpos.application;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableFixture;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.domain.OrderTableFixture.TABLE_1_EMPTY;
import static kitchenpos.domain.OrderTableFixture.TABLE_1_NOT_EMPTY;
import static kitchenpos.domain.OrderTableFixture.TABLE_2_EMPTY;
import static kitchenpos.domain.OrderTableFixture.TABLE_3_NOT_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService sut;

    @Test
    @DisplayName("주문테이블 생성 시 이름이 비어 있으면 오류")
    void createFail() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.create(new OrderTable()));
    }

    @Test
    @DisplayName("주문테이블 생성 성공")
    void createSuccess() {
        // given
        given(orderTableRepository.save(any())).willReturn(TABLE_1_EMPTY);

        // when
        OrderTable actual = sut.create(TABLE_1_EMPTY);

        // then
        assertThat(actual).isEqualTo(TABLE_1_EMPTY);
    }

    @Test
    @DisplayName("주문테이블에 사람이 앉은 명령을 정상적으로 처리")
    void sitSuccess() {
        // given
        UUID id = UUID.randomUUID();
        given(orderTableRepository.findById(any())).willReturn(Optional.of(TABLE_1_EMPTY));

        // when
        OrderTable actual = sut.sit(id);

        // then
        assertThat(actual.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("주문테이블의 상태가 COMPLETED가 아니면 청소 불가능")
    void clearFail() {
        // given
        given(orderTableRepository.findById(any())).willReturn(Optional.of(TABLE_1_NOT_EMPTY));
        given(orderRepository.existsByOrderTableAndStatusNot(TABLE_1_NOT_EMPTY, OrderStatus.COMPLETED))
            .willReturn(true);

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.clear(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문테이블 청소 성공")
    void clearSuccess() {
        // given
        given(orderTableRepository.findById(any())).willReturn(Optional.of(TABLE_1_NOT_EMPTY));
        given(orderRepository.existsByOrderTableAndStatusNot(TABLE_1_NOT_EMPTY, OrderStatus.COMPLETED))
            .willReturn(false);

        // when
        OrderTable actual = sut.clear(UUID.randomUUID());

        // then
        assertAll(Arrays.asList(
            () -> assertThat(actual.isEmpty()).isTrue(),
            () -> assertThat(actual.getNumberOfGuests()).isZero()
        ));
    }

    @Test
    @DisplayName("주문 테이블 인원 수는 0명 미만으로 변경 불가")
    void changeNumberOfGuestsFail01() {
        // given
        OrderTable orderTable = OrderTableFixture.builder()
                                                 .numberOfGuests(-1)
                                                 .build();

        // when
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sut.changeNumberOfGuests(UUID.randomUUID(), orderTable));
    }

    @Test
    @DisplayName("비어 있는 주문 테이블의 인원 수는 변경 불가")
    void changeNumberOfGuestsFail02() {
        // given
        OrderTable orderTable = OrderTableFixture.builder()
                                                 .numberOfGuests(3)
                                                 .build();

        given(orderTableRepository.findById(any())).willReturn(Optional.of(TABLE_2_EMPTY));

        // when
        assertThatIllegalStateException()
            .isThrownBy(() -> sut.changeNumberOfGuests(UUID.randomUUID(), orderTable));
    }

    @Test
    @DisplayName("주문 테이블 인원 수 변경 성공")
    void changeNumberOfGuestsSuccess() {
        // given
        OrderTable orderTable = OrderTableFixture.builder()
                                                 .numberOfGuests(3)
                                                 .build();

        given(orderTableRepository.findById(any())).willReturn(Optional.of(TABLE_3_NOT_EMPTY));

        // when
        OrderTable actual = sut.changeNumberOfGuests(UUID.randomUUID(), orderTable);

        // then
        assertThat(actual.getNumberOfGuests()).isEqualTo(3);
    }
}
