package kitchenpos.application;


import static kitchenpos.application.OrderTableFixture.삼번_테이블;
import static kitchenpos.application.OrderTableFixture.일번_테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("식탁 생성 예외 - 식탁 이름 없음")
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

    @DisplayName("식탁 생성")
    @Test
    void create() {
        //given
        OrderTable 신규_테이블 = 식탁_생성("3번");

        given(orderTableRepository.save(any(OrderTable.class))).willReturn(삼번_테이블);
        //when
        OrderTable orderTable = orderTableService.create(신규_테이블);

        //then
        assertAll(
            () -> assertThat(orderTable.getName()).isEqualTo("3번"),
            () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
            () -> assertThat(orderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("손님 착석")
    @Test
    void sit() {
        //given
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(일번_테이블));

        //when
        OrderTable orderTable = orderTableService.sit(일번_테이블.getId());

        boolean actual = orderTable.isEmpty();

        //then
        assertThat(actual).isFalse();
    }

    @DisplayName("식탁 정리 예외 - 주문완료되지 않은 식탁")
    @Test
    void clearException() {
        //given
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(일번_테이블));
        given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
            .willReturn(true);

        //when
        ThrowingCallable actual = () -> orderTableService.clear(일번_테이블.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("식탁 정리")
    @Test
    void clear() {
        //given
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(일번_테이블));
        given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
            .willReturn(false);

        //when
        OrderTable cleanTable = orderTableService.clear(일번_테이블.getId());

        //then
        assertAll(
            () -> Assertions.assertThat(cleanTable.getNumberOfGuests()).isZero(),
            () -> Assertions.assertThat(cleanTable.isEmpty()).isTrue()
        );

    }

    @DisplayName("손님 수 변경 예외 - 0명 미만으로 변경 불가")
    @Test
    void changeNumberOfGuestsException() {
        //given
        OrderTable 손님_수_변경 = 식탁_생성("1번 테이블 손님", -1);

        //when
        ThrowingCallable actual = () -> orderTableService.changeNumberOfGuests(일번_테이블.getId(), 손님_수_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("손님 수 변경 예외 - 착석하지 않은 식탁")
    @Test
    void tableIsEmptyThenChangeNumberOfGuestsException() {
        //given
        OrderTable 손님_수_변경 = 식탁_생성("1번 테이블 손님", 3);

        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(일번_테이블));

        //when
        ThrowingCallable actual = () -> orderTableService.changeNumberOfGuests(일번_테이블.getId(), 손님_수_변경);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("손님 수 변경")
    @Test
    void changeNumberOfGuests() {
        //given
        OrderTable 손님_수_변경 = 식탁_생성("3번 테이블 손님", 3);

        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(일번_테이블));
        orderTableService.sit(일번_테이블.getId());

        //when
        OrderTable orderTable = orderTableService.changeNumberOfGuests(일번_테이블.getId(), 손님_수_변경);

        //then
        assertAll(
            () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(3),
            () -> assertThat(orderTable.isEmpty()).isFalse()
        );
    }

    @DisplayName("모든 식탁 조회")
    @Test
    void findAll() {
        //given
        given(orderTableRepository.findAll()).willReturn(Arrays.asList(일번_테이블, 삼번_테이블));

        //when
        List<OrderTable> orderTables = orderTableService.findAll();

        //then
        assertAll(
            () -> assertThat(orderTables).hasSize(2),
            () -> assertThat(orderTables).containsExactly(일번_테이블, 삼번_테이블)
        );
    }

    private OrderTable 식탁_생성(String orderTableName) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(orderTableName);
        return orderTable;
    }

    private OrderTable 식탁_생성(String orderTableName, int numberOfGuests) {
        OrderTable orderTable = 식탁_생성(orderTableName);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(false);
        return orderTable;
    }
}
